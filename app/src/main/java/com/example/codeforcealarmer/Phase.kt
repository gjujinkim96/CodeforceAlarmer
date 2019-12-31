package com.example.codeforcealarmer

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class Phase{
    BEFORE, CODING, PENDING_SYSTEM_TEST, SYSTEM_TEST, FINISHED;
    companion object {
        fun fromStr(str: String) =
            when (str){
                "BEFORE" -> BEFORE
                "CODING" -> CODING
                "PENDING_SYSTEM_TEST" -> PENDING_SYSTEM_TEST
                "SYSTEM_TEST" -> SYSTEM_TEST
                "FINISHED" -> FINISHED
                else -> throw IllegalArgumentException()
            }
    }
}

class PhaseConverters{
    @TypeConverter
    fun intToPhase(v: Int) =
        when (v){
            Phase.BEFORE.ordinal -> Phase.BEFORE
            Phase.CODING.ordinal -> Phase.CODING
            Phase.PENDING_SYSTEM_TEST.ordinal -> Phase.PENDING_SYSTEM_TEST
            Phase.SYSTEM_TEST.ordinal -> Phase.SYSTEM_TEST
            Phase.FINISHED.ordinal -> Phase.FINISHED
            else -> null
        }
    @TypeConverter
    fun phaseToInt(phase: Phase?) = phase?.ordinal
}