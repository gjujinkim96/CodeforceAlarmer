package com.example.codeforcealarmer

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*

class ContestViewModel(application: Application) : AndroidViewModel(application) {
    private val coroutineScope = viewModelScope + Dispatchers.Default + Dispatchers.IO
    private var isBeforeUpdating: Boolean = false
    private var isBeforeRestart: Boolean = false
    private val mutex: Mutex = Mutex()

    private var beforeContestRaw: MutableList<Contest> = mutableListOf()
    val beforeContests: MutableLiveData<MutableList<Contest>> by lazy {
        MutableLiveData<MutableList<Contest>>()
    }

    private var afterContestRaw: MutableList<Contest> = mutableListOf()
    val afterContests: MutableLiveData<MutableList<Contest>> by lazy {
        MutableLiveData<MutableList<Contest>>()
    }

    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isInternetConnection: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isRefreshing: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var divFilter: ContestType = ContestType()
        private set
    var startTime: LocalTime = LocalTime.now()
        private set
    var endTime: LocalTime = LocalTime.now()
        private set

    init {
        Log.v("VIEWMODEL_INIT", "INIT")
        isLoading.value = true
        isRefreshing.value = false
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    fun changeBeforeSetting(newStartTime: LocalTime? = null,
                      newEndTime: LocalTime? = null,
                      newIsDiv1Checked: Boolean? = null,
                      newIsDiv2Checked: Boolean? = null,
                      newIsDiv3Checked: Boolean? = null,
                      newIsOtherChecked: Boolean? = null){
        var hasDataSetChanged = false
        if (newStartTime != null && startTime != newStartTime){
            startTime = newStartTime
            hasDataSetChanged = true
        }

        if (newEndTime != null && endTime != newEndTime){
            endTime = newEndTime
            hasDataSetChanged = true
        }

        if (newIsDiv1Checked != null && divFilter.setType(newIsDiv1Checked, ContestType.Type.DIV1))
            hasDataSetChanged = true

        if (newIsDiv2Checked != null && divFilter.setType(newIsDiv2Checked, ContestType.Type.DIV2))
            hasDataSetChanged = true

        if (newIsDiv3Checked != null && divFilter.setType(newIsDiv3Checked, ContestType.Type.DIV3))
            hasDataSetChanged = true

        if (newIsOtherChecked != null && divFilter.setType(newIsOtherChecked, ContestType.Type.OTHER))
            hasDataSetChanged = true

        if (hasDataSetChanged && beforeContestRaw.isNotEmpty()){
            coroutineScope.launch(Dispatchers.Main) { makeBeforeContestData() }
        }
    }


    private fun filter(rawData: MutableList<Contest>?) : MutableList<Contest>{
        val ret = arrayListOf<Contest>()

        if (rawData == null)
            return ret

        rawData.forEach{
            if (it.startTimeSeconds != null){
                val instant = Instant.ofEpochSecond(it.startTimeSeconds)
                val zoneId = ZoneId.systemDefault()
                val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
                val localTime = zonedDateTime.toLocalTime()
                if (divFilter.contains(it.contestType)){
                    if (startTime <= endTime){
                        if (startTime <= localTime && localTime <= endTime)
                            ret.add(it)
                    } else if (startTime <= localTime || localTime <= endTime)
                        ret.add(it)
                }
            }
        }

        return ret
    }

    private suspend fun makeBeforeContestData(){
        Log.v("COOROUTINE_TIME", "Start")

        mutex.withLock {
            if (isBeforeUpdating) {
                isBeforeRestart = true
                return
            }
            else{
                isBeforeUpdating = true
            }
        }

        var isRunning: Boolean = true
        while (isRunning){
            mutex.withLock {
                isBeforeRestart = false
            }

            beforeContests.value = withContext(Dispatchers.Default) {
                filter(beforeContestRaw).apply {
                    sortBy { it.startTimeSeconds }
                }
            }

            mutex.withLock {
                isRunning = isBeforeRestart
                if (!isRunning){
                    isBeforeUpdating = false
                }
            }
        }

        Log.v("COOROUTINE_TIME", "end")
    }

    private suspend fun makeAfterContestData(){
        afterContests.value = withContext(Dispatchers.Default){
            afterContestRaw.apply{
                sortByDescending { it.startTimeSeconds }
            }
        }
    }

    private fun isThereInternet(): Boolean {
        val cm = getApplication<Application>().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as?
                ConnectivityManager ?: throw IllegalArgumentException()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            val newInfo = cm.activeNetworkInfo ?: return false
            return newInfo.isConnected
        }
    }


    fun loadData(){
        val url = "https://codeforces.com/api/contest.list"
        if (isThereInternet()) {
            viewModelScope.launch(CoroutineName("getContest")){ getContest(url) }
        }
        else{
            beforeContestRaw = mutableListOf()
            afterContestRaw = mutableListOf()
            beforeContests.value = mutableListOf()
            afterContests.value = mutableListOf()
            isInternetConnection.value = false
            isLoading.value = false
            isRefreshing.value = false
        }
    }

    private suspend fun getContest(url: String){
        try {
            val resultsAsync =
                viewModelScope.async(CoroutineName("getContestAsync")) { getContestAsync(url) }

            val results = resultsAsync.await()

            beforeContestRaw = results.first
            afterContestRaw = results.second
            makeBeforeContestData()
            makeAfterContestData()

            Log.v("LOADING_STUFF", "loading done")
            isInternetConnection.value = isThereInternet()
            isLoading.value = false
            isRefreshing.value = false
        }
        catch (e: CancellationException){
            Log.v("COROUTINE_CHECK", "caught cancel")
            isLoading.value = false
            beforeContestRaw = mutableListOf()
            afterContestRaw = mutableListOf()
            beforeContests.value = mutableListOf()
            afterContests.value = mutableListOf()
        }
    }

    private suspend fun getContestAsync(url: String): Pair<MutableList<Contest>, MutableList<Contest>>{
        val jsonString =HttpHandler.fetchFromUrl(url)
        if (jsonString == null){
            Log.e(this::class.java.simpleName, "failed to read jsonString")
            return Pair(mutableListOf(), mutableListOf())
        }

        val jsonResult =  JsonContestParser(jsonString).apply { parse() }
        if (jsonResult.status != JsonContestParser.Status.OK) {
            Log.e(this::class.java.simpleName, "there was error parsing JSON")
            return Pair(mutableListOf(), mutableListOf())
        }

        val curContest = jsonResult.contests

        val phaseFilter = EnumSet.range(Phase.BEFORE, Phase.CODING)
        val beforeTmp: MutableList<Contest> = mutableListOf()
        val afterTmp: MutableList<Contest> = mutableListOf()
        curContest?.forEach {
            if (phaseFilter.contains(it.phase))
                beforeTmp.add(it)
            else
                afterTmp.add(it)
        }

        return Pair(beforeTmp, afterTmp)
    }
}