package com.example.codeforcealarmer

class AlarmOffsetRepo(val alarmOffsetDao: AlarmOffsetDao) {
    suspend fun update(id: Long, newOffset: Long?) = alarmOffsetDao.update(id, newOffset)
    suspend fun delete(id: Long) = alarmOffsetDao.delete(id)
    suspend fun insert(alarmOffset: AlarmOffset) = alarmOffsetDao.insert(alarmOffset)
}