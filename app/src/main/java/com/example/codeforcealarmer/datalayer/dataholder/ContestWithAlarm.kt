package com.example.codeforcealarmer.datalayer.dataholder

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity
data class ContestWithAlarm(
    var id: Int,
    var name: String,
    @Embedded var contestType: ContestType,
    var phase: Phase,
    var durationSeconds: Long,
    var startTimeSeconds: Long?,
    var offset: Long?
)

fun ContestWithAlarm.getUrl() = "https://codeforces.com/contests/$id"