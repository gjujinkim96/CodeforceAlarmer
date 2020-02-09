package com.example.codeforcealarmer.datalayer.repo

import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetDao
import com.example.codeforcealarmer.datalayer.dataholder.AlarmData
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset

class AlarmOffsetRepo(val alarmOffsetDao: AlarmOffsetDao) {
    suspend fun update(id: Int, newData: AlarmData) = alarmOffsetDao.update(id, newData)
    fun delete(id: Int, data: AlarmData) = alarmOffsetDao.delete(id, data)
    suspend fun insert(alarmOffset: AlarmOffset) : Long = alarmOffsetDao.insert(alarmOffset)
    suspend fun getAll() = alarmOffsetDao.getAll()
}