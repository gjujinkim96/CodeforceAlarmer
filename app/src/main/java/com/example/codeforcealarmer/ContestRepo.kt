package com.example.codeforcealarmer

import android.util.Log

class ContestRepo(val contestDao: ContestDao, val networkChecker: NetworkChecker) {
    fun getBeforeContests() = contestDao.getBetweenPhases(Phase.BEFORE, Phase.CODING)
    fun getAfterContests() = contestDao.getBetweenPhases(Phase.PENDING_SYSTEM_TEST, Phase.FINISHED)

    suspend fun updateFinishedPhase() = contestDao.updateFinshedPhase()
    suspend fun updateCodingPhase() = contestDao.updateCodingPhase()

    // load inner function where current return return null or data
    // and outer function if ret was null check internet and return appropriate loadcontestrsult
    suspend fun load() : LoadContestResult{
        val contests = loadContest() ?:
            return if (networkChecker.isThereInternet()) LoadContestResult.OTHER_ERROR else LoadContestResult.NETWORK_ERROR

        contestDao.insertUpdated(contests)
        return LoadContestResult.OKAY

//        val phaseFilter = EnumSet.range(Phase.BEFORE, Phase.CODING)
//        val beforeTmp: MutableList<Contest> = mutableListOf()
//        val afterTmp: MutableList<Contest> = mutableListOf()
//        curContest?.forEach {
//            if (phaseFilter.contains(it.phase))
//                beforeTmp.add(it)
//            else
//                afterTmp.add(it)
//        }
    }

    private suspend fun loadContest(): List<Contest>?{
        val jsonString = HttpHandler.fetchFromUrl(OldContestRepository.url)
        if (jsonString == null){
            Log.e(this::class.java.simpleName, "failed to read jsonString")
            return null
        }

        val jsonResult =  JsonContestParser(jsonString).apply { parse() }
        if (jsonResult.status != JsonContestParser.Status.OK) {
            Log.e(this::class.java.simpleName, "there was error parsing JSON")
            return null
        }

        return jsonResult.contests
    }
}