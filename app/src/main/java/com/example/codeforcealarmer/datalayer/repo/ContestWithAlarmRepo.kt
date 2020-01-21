package com.example.codeforcealarmer.datalayer.repo

import com.example.codeforcealarmer.datalayer.dao.ContestWithAlarmDao
import com.example.codeforcealarmer.datalayer.dataholder.Phase

class ContestWithAlarmRepo(val contestWithAlarmDao: ContestWithAlarmDao) {
    fun getBeforeContests() = contestWithAlarmDao.getBetweenPhases(
        Phase.BEFORE,
        Phase.CODING, true)
}