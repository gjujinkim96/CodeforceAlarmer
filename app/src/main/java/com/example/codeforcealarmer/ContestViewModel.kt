package com.example.codeforcealarmer

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class ContestViewModel(application: Application) : AndroidViewModel(application) {
    private val coroutineScope = viewModelScope + Dispatchers.Default + Dispatchers.IO
    private var isBeforeUpdating: Boolean = false
    private var isBeforeRestart: Boolean = false
    private val mutex: Mutex = Mutex()
    private val contestRepository = OldContestRepository()

    val contestFilter: ContestFilter
        get() = contestRepository.contestFilter


    val beforeContests: MutableLiveData<MutableList<Contest>> by lazy {
        MutableLiveData<MutableList<Contest>>()
    }

    val afterContests: LiveData<List<Contest>> = contestRepository.afterContests
    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isInternetConnection: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isRefreshing: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

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
                      newcontestType: ContestType? = null){
        val newFilter = contestRepository.contestFilter.copyWithNull(newStartTime, newEndTime, newcontestType)
        if (newFilter != contestRepository.contestFilter){
            coroutineScope.launch(Dispatchers.Main) { contestRepository.changeFilter(newFilter) }
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
                filter(contestRepository.beforeContests).apply {
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
        if (isThereInternet()) {
            viewModelScope.launch(CoroutineName("getContest")){
                try {
                    contestRepository.getNew()
                    makeBeforeContestData()

                    Log.v("LOADING_STUFF", "loading done")
                    isInternetConnection.value = isThereInternet()
                    isLoading.value = false
                    isRefreshing.value = false
                }catch (e: CancellationException){
                    Log.v("COROUTINE_CHECK", "caught cancel")
                    isLoading.value = false
                    beforeContests.value = mutableListOf()
                }
            }
        }
        else{
            beforeContests.value = mutableListOf()
            isInternetConnection.value = false
            isLoading.value = false
            isRefreshing.value = false
        }
    }
}