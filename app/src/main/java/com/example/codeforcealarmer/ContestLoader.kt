package com.example.codeforcealarmer

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import java.util.*

class ContestLoader(context: Context, val url : String) : AsyncTaskLoader<Pair<MutableList<Contest>, MutableList<Contest>>>(context) {
    var cache : Pair<MutableList<Contest>, MutableList<Contest>>? = null
    override fun onStartLoading() {
        if (cache == null)
            forceLoad()
        else
            deliverResult(cache)
    }

    override fun deliverResult(data: Pair<MutableList<Contest>, MutableList<Contest>>?) {
        cache = data
        super.deliverResult(data)
    }

    override fun loadInBackground(): Pair<MutableList<Contest>, MutableList<Contest>>? {
        val jsonString = HttpHandler.fetchFromUrl(url)
        if (jsonString == null){
            Log.e(this::class.java.simpleName, "failed to read jsonString")
            return null
        }

        val jsonResult =  JsonContestParser(jsonString)
        if (jsonResult.status != JsonContestParser.Status.OK) {
            return null
        }

        val curContest = jsonResult.contests

        val phaseFilter = EnumSet.range(Phase.BEFORE, Phase.CODING)
        val beforeContests: MutableList<Contest> = mutableListOf()
        val afterContest: MutableList<Contest> = mutableListOf()
        curContest?.forEach {
            if (phaseFilter.contains(it.phase))
                beforeContests.add(it)
            else
                afterContest.add(it)
        }

        return Pair(beforeContests, afterContest)
    }
}