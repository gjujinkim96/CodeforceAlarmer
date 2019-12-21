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
import java.util.*

class ContestViewModel(application: Application) : AndroidViewModel(application) {
    val coroutineScope = viewModelScope + Dispatchers.Default + Dispatchers.IO

    val beforeContests: MutableLiveData<MutableList<Contest>> by lazy {
        MutableLiveData<MutableList<Contest>>()
    }

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

    fun loadData(){
        val url = "https://codeforces.com/api/contest.list"
        if (isThereInternet()) {
            viewModelScope.launch(CoroutineName("getContest")){ getContest(url) }
        }
        else{
            beforeContests.value = mutableListOf()
            afterContests.value = mutableListOf()
            isInternetConnection.value = false
            isLoading.value = false
            isRefreshing.value = false
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

    private suspend fun getContest(url: String){
        try {
            val results_async =
                viewModelScope.async(CoroutineName("getContestAsync")) { getContestAsync(url) }

            val results = results_async.await()

            beforeContests.value = results.first
            afterContests.value = results.second

            Log.v("LOADING_STUFF", "loading done")
            isInternetConnection.value = isThereInternet()
            isLoading.value = false
            isRefreshing.value = false
        }
        catch (e: CancellationException){
            Log.v("COROUTINE_CHECK", "caught cancel")
            isLoading.value = false
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