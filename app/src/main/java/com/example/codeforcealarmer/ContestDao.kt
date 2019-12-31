package com.example.codeforcealarmer

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
@TypeConverters(PhaseConverters::class)
abstract class ContestDao {
    @Query("SELECT * From Contest WHERE phase BETWEEN :startPhase and :endPhase")
    abstract fun getBetweenPhases(startPhase: Phase, endPhase: Phase) : LiveData<MutableList<Contest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUpdated(newContests: List<Contest>)

    // use without any parameter
    @Query("UPDATE Contest SET phase=:changePhase WHERE startTimeSeconds IS NOT NULL AND " +
            "startTimeSeconds + durationSeconds < (SELECT strftime('%s', 'now'))")
    abstract suspend fun updateFinshedPhase(changePhase: Phase = Phase.FINISHED)

    // use without any parameter
    @Query("UPDATE Contest SET phase=:changePhase WHERE startTimeSeconds IS NOT NULL AND" +
            " startTimeSeconds < (SELECT strftime('%s', 'now')) AND " +
            "startTimeSeconds + durationSeconds >= (SELECT strftime('%s', 'now'))")
    abstract suspend fun updateCodingPhase(changePhase: Phase = Phase.CODING)
}
