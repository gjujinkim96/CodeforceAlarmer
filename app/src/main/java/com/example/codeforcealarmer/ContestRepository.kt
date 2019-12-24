package com.example.codeforcealarmer

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import java.util.*

class ContestRepository {
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