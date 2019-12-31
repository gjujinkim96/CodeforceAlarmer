package com.example.codeforcealarmer

class AlarmOffsetWithStartTimeRepo(val alarmOffsetWithStartTimeDao: AlarmOffsetWithStartTimeDao) {
    suspend fun getAlarmedData() : List<AlarmOffsetWithStartTime> = alarmOffsetWithStartTimeDao.getAlarmedData()
}