package com.example.codeforcealarmer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.*

class OldContestRepository {
    companion object {
        val url = "https://codeforces.com/api/contest.list"
    }

    var beforeContests: MutableList<Contest> = mutableListOf()
        private set
    var afterContests: MutableLiveData<List<Contest>> = MutableLiveData()
        private set
    var contestFilter: ContestFilter = ContestFilter()
        private set

    suspend fun getNew(){
        val (beforeTmp, afterTmp) = getNewHelper()
        beforeContests = beforeTmp

        afterContests.value = withContext(Dispatchers.Default){
            afterTmp.sortedByDescending { it.startTimeSeconds }
        }
    }

    suspend fun changeFilter(newFilter: ContestFilter){
        contestFilter = newFilter
    }

    private suspend fun getNewHelper(): Pair<MutableList<Contest>, MutableList<Contest>>{
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