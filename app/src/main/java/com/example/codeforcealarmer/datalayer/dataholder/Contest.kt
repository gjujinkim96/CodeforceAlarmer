package com.example.codeforcealarmer.datalayer.dataholder

import androidx.room.*

@Entity
data class Contest(
    @PrimaryKey var id: Int,
    var name: String,
    @Embedded var contestType: ContestType,
    var phase: Phase,
    var durationSeconds: Long,
    var startTimeSeconds: Long?
    ){

    companion object{
        fun makeContest(id: Int, name: String, phase: Phase, durationSeconds: Long, startTimeSeconds: Long?) : Contest {
            val contestType =
                ContestType.makeAllFalse()
            if (name.contains("Div. 1", true))
                contestType.div1 = true
            if (name.contains("Div. 2", true))
                contestType.div2 = true
            if (name.contains("Div. 3", true))
                contestType.div3 = true
            if (!contestType.isDiv())
                contestType.other = true


            return Contest(
                id,
                name,
                contestType,
                phase,
                durationSeconds,
                startTimeSeconds
            )
        }
    }
}

fun Contest.getUrl() = "https://codeforces.com/contests/$id"