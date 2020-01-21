package com.example.codeforcealarmer.datalayer.dao

import androidx.room.*
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset

@Dao
interface AlarmOffsetDao{
    @Query("UPDATE AlarmOffset SET `offset`=:newOffset WHERE id=:id")
    suspend fun update(id: Int, newOffset: Long?)

    @Query("DELETE FROM AlarmOffset WHERE id=:id")
    suspend fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmOffset: AlarmOffset) : Long

    @Query("SELECT * FROM AlarmOffset")
    suspend fun getAll() : List<AlarmOffset>
}