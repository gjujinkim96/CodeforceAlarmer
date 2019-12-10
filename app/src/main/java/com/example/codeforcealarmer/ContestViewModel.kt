package com.example.codeforcealarmer

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ContestViewModel : ViewModel() {
    val beforeContests: MutableLiveData<MutableList<Contest>> by lazy {
        MutableLiveData<MutableList<Contest>>()
    }

    val afterContests: MutableLiveData<MutableList<Contest>> by lazy {
        MutableLiveData<MutableList<Contest>>()
    }

    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        val url = "https://codeforces.com/api/contest.list"
        loadData(url)
    }

    private fun loadData(url: String){
        isLoading.value = true

        ContestAsyncTask().execute(url)
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