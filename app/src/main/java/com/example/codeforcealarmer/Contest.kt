package com.example.codeforcealarmer

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


data class Contest(val id: Int, val name: String, val phase: Phase, val durationSeconds: Long, val startTimeSeconds: Long?)