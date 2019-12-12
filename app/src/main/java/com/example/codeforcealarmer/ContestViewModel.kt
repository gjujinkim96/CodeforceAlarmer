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
import java.util.*

class ContestViewModel(application: Application) : AndroidViewModel(application) {
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

    init {
        val url = "https://codeforces.com/api/contest.list"
        loadData(url)
    }

    private fun loadData(url: String){
        isLoading.value = true

        if (isThereInternet()) {
            isInternetConnection.value = true
            ContestAsyncTask().execute(url)
        }
        else{
            beforeContests.value = mutableListOf()
            afterContests.value = mutableListOf()
            isInternetConnection.value = false
            isLoading.value = false
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

    inner class ContestAsyncTask : AsyncTask<String, Void, Pair<MutableList<Contest>, MutableList<Contest>>>(){
        override fun doInBackground(vararg args: String?): Pair<MutableList<Contest>, MutableList<Contest>> {
            if (args == null || args.isEmpty()){
                Log.e(this::class.java.simpleName, "there is no input url")
                return Pair(mutableListOf(), mutableListOf())
            }

            val url = args[0] ?: return Pair(mutableListOf(), mutableListOf())

            val jsonString = HttpHandler.fetchFromUrl(url)
            if (jsonString == null){
                Log.e(this::class.java.simpleName, "failed to read jsonString")
                return Pair(mutableListOf(), mutableListOf())
            }

            val jsonResult =  JsonContestParser(jsonString)
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

        override fun onPostExecute(result: Pair<MutableList<Contest>, MutableList<Contest>>?) {
            if (result != null){
                beforeContests.value = result.first
                afterContests.value = result.second
            }

            isLoading.value = false
        }

        override fun onCancelled(result: Pair<MutableList<Contest>, MutableList<Contest>>?) {
            isLoading.value = false
            beforeContests.value = mutableListOf()
            afterContests.value = mutableListOf()
        }
    }
}