package com.example.codeforcealarmer.datalayer.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.codeforcealarmer.datalayer.dataholder.AlarmData
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset

@Dao
interface AlarmOffsetDao{
    @Query("UPDATE AlarmOffset SET data=:newData WHERE id=:id")
    suspend fun update(id: Int, newData: AlarmData)

    @Query("DELETE FROM AlarmOffset WHERE id=:id AND data=:data")
    suspend fun delete(id: Int, data: AlarmData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmOffset: AlarmOffset) : Long

    @Query("SELECT * FROM AlarmOffset")
    fun getAll() : LiveData<List<AlarmOffset>>
}