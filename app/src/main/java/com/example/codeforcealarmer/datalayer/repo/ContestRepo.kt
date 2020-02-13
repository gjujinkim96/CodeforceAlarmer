package com.example.codeforcealarmer.datalayer.repo

import android.content.Context
import android.util.Log
import com.example.codeforcealarmer.datalayer.dao.ContestDao
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.LoadContestResult
import com.example.codeforcealarmer.datalayer.dataholder.Phase
import com.example.codeforcealarmer.network.DownloadEstimator
import com.example.codeforcealarmer.network.HttpHandler
import com.example.codeforcealarmer.network.JsonContestParser
import com.example.codeforcealarmer.network.NetworkChecker

class ContestRepo(val context: Context, val contestDao: ContestDao) {
    companion object {
        const val URL = "https://codeforces.com/api/contest.list"
    }

    fun getBeforeContests() = contestDao.getBetweenPhases(
        Phase.BEFORE,
        Phase.CODING, true)
    fun getAfterContests() = contestDao.getBetweenPhases(
        Phase.PENDING_SYSTEM_TEST,
        Phase.FINISHED, false)

    suspend fun updateFinishedPhase() = contestDao.updateFinshedPhase()
    suspend fun updateCodingPhase() = contestDao.updateCodingPhase()

    // load inner function where current return return null or data
    // and outer function if ret was null check internet and return appropriate loadcontestrsult
    suspend fun load() : LoadContestResult {
        Log.v("UPDATE_PERIODICALLY", "load")
        val contests = loadContest() ?:
            return if (NetworkChecker.isThereInternet(context)) LoadContestResult.OTHER_ERROR else LoadContestResult.NETWORK_ERROR

        contestDao.upsert(contests)
        return LoadContestResult.OKAY
    }

    suspend fun getName(id: Int) = contestDao.getName(id)

    private suspend fun loadContest(): List<Contest>?{
        val jsonString = HttpHandler.fetchFromUrl(URL)
        jsonString?.let {
            DownloadEstimator.setEstimate(context, jsonString.toByteArray().size.toLong())
        }

        if (jsonString == null){
            Log.e(this::class.java.simpleName, "failed to read jsonString")
            return null
        }

        val jsonResult =  JsonContestParser(jsonString).apply {
            parse()
        }

        if (jsonResult.status != JsonContestParser.Status.OK) {
            Log.e(this::class.java.simpleName, "there was error parsing JSON")
            return null
        }

        return jsonResult.contests
    }
}