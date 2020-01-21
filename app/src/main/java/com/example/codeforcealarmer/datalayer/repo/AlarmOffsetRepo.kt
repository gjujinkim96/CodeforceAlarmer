package com.example.codeforcealarmer.datalayer.repo

import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetDao
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset

class AlarmOffsetRepo(val alarmOffsetDao: AlarmOffsetDao) {
    suspend fun update(id: Int, newOffset: Long?) = alarmOffsetDao.update(id, newOffset)
    suspend fun delete(id: Int) = alarmOffsetDao.delete(id)
    suspend fun insert(alarmOffset: AlarmOffset) : Long = alarmOffsetDao.insert(alarmOffset)
    suspend fun getAll() = alarmOffsetDao.getAll()
}