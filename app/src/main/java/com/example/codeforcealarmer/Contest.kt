package com.example.codeforcealarmer

enum class SCORE_SYSTEM{
    CF, IOI, ICPC;
    companion object {
        fun fromStr(str: String) =
            when (str){
                "CF" -> CF
                "IOI" -> IOI
                "ICPC" -> ICPC
                else -> throw IllegalArgumentException()
            }
    }
}


enum class PHASE{
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


data class Contest(val id: Int, val name: String, val type: SCORE_SYSTEM, val phase: PHASE,
    val durationSeconds: Int, val startTimeSeconds: Int?, val relativeTimeSeconds: Int?,
                   val websiteUrl: String?)