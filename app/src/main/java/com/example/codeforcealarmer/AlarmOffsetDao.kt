package com.example.codeforcealarmer

import androidx.room.*

@Dao
interface AlarmOffsetDao{
    @Query("UPDATE AlarmOffset SET `offset`=:newOffset WHERE id=:id")
    suspend fun update(id: Long, newOffset: Long?)

    @Query("DELETE FROM AlarmOffset WHERE id=:id")
    suspend fun delete(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmOffset: AlarmOffset)
}