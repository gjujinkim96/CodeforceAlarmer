package com.example.codeforcealarmer.datalayer.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.codeforcealarmer.datalayer.dataholder.Phase
import com.example.codeforcealarmer.datalayer.dataholder.PhaseConverters
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import android.util.Log

@Dao
interface ContestDao {
    @Query(
"""
    SELECT * From Contest WHERE phase BETWEEN :startPhase and :endPhase
    ORDER BY CASE WHEN :isAsc = 1 THEN startTimeSeconds END ASC,
    CASE WHEN :isAsc = 0 THEN startTimeSeconds END DESC
    """
    )
    fun getBetweenPhases(startPhase: Phase, endPhase: Phase, isAsc: Boolean) : LiveData<List<Contest>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contests: List<Contest>) : List<Long>

    @Update
    suspend fun update(newContest: Contest) : Int

    // return value for checking what problems
    @Transaction
    suspend fun upsert(newContests: List<Contest>) : List<Contest>{
        val rowIds = insert(newContests)
        val contestsToUpdate = rowIds.mapIndexedNotNull { index, rowId ->
            if (rowId != -1L) null else newContests[index]
        }
        Log.v("LOADING_PB", rowIds.toString())
        var updatedCnt = 0
        contestsToUpdate.forEach{
            val curContest = get(it.id)
            if (curContest != it)
                updatedCnt += update(it)
        }
        Log.v("CALLED_THREE", "updatedCnt: $updatedCnt")

        return newContests
    }

    // use without any parameter
    @Query(
"""
      UPDATE Contest SET phase=:changePhase WHERE phase <> :changePhase AND
      startTimeSeconds IS NOT NULL AND
      startTimeSeconds + durationSeconds < CAST((SELECT strftime('%s', 'now')) AS INTEGER)
      """
    )
    suspend fun updateFinshedPhase(changePhase: Phase = Phase.FINISHED) : Int

    // use without any parameter
    @Query(
        """
            UPDATE Contest SET phase=:changePhase WHERE phase <> :changePhase AND
            startTimeSeconds IS NOT NULL AND
            startTimeSeconds < (SELECT strftime('%s', 'now')) AND
            startTimeSeconds + durationSeconds >= (SELECT strftime('%s', 'now'))
        """
    )
    suspend fun updateCodingPhase(changePhase: Phase = Phase.CODING) : Int

    @Query("SELECT name From Contest WHERE id=:id")
    suspend fun getName(id: Int) : String

    @Query("SELECT * FROM Contest WHERE id=:id")
    suspend fun get(id: Int) : Contest
}
