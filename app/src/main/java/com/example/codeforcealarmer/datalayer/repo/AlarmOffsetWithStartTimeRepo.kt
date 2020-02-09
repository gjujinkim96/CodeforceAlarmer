package com.example.codeforcealarmer.datalayer.repo

import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetWithStartTimeDao
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime

class AlarmOffsetWithStartTimeRepo(val alarmOffsetWithStartTimeDao: AlarmOffsetWithStartTimeDao) {
    fun getAlarmedData() : List<AlarmOffsetWithStartTime> = alarmOffsetWithStartTimeDao.getAlarmedData()
}