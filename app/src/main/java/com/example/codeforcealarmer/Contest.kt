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

class ContestType(isDiv1: Boolean = false, isDiv2: Boolean = false, isDiv3: Boolean = false){
    private val DIV1 = 1
    private val DIV2 = 2
    private val DIV3 = 4

    private fun getBits(mask: Int) = (bits and mask) > 0

    private fun setBits(value: Boolean, mask: Int) {
        bits = if (value) {
            bits or mask
        } else {
            bits and mask.inv()
        }
    }

    private var bits: Int = 0
    var div1: Boolean
        get() = getBits(DIV1)
        set(value) = setBits(value, DIV1)

    var div2: Boolean
        get() = getBits(DIV2)
        set(value) = setBits(value, DIV2)

    var div3: Boolean
        get() = getBits(DIV3)
        set(value) = setBits(value, DIV3)

    val other: Boolean
        get() = bits == 0

    init {
        div1 = isDiv1
        div2 = isDiv2
        div3 = isDiv3
    }

    override fun toString() = "div1:$div1, div2:$div2, div3:$div3, other:$other"
    override fun hashCode() = bits
    override fun equals(other: Any?) = if (other is ContestType){
        bits == other.bits
    }else{
        false
    }
}

data class Contest(val id: Int, val name: String, val contestType: ContestType, val phase: Phase,
                   val durationSeconds: Long, val startTimeSeconds: Long?){
    companion object{
        fun makeContest(id: Int, name: String, phase: Phase, durationSeconds: Long, startTimeSeconds: Long?) : Contest{
            val contestType = ContestType()
            if (name.contains("Div. 1", true))
                contestType.div1 = true
            if (name.contains("Div. 2", true))
                contestType.div2 = true
            if (name.contains("Div. 3", true))
                contestType.div3 = true


            return Contest(id, name, contestType, phase, durationSeconds, startTimeSeconds)
        }
    }
}

fun Contest.getUrl() = "https://codeforces.com/contests/$id"