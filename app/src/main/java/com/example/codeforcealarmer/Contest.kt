package com.example.codeforcealarmer

import java.util.*

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


class ContestType(isDiv1: Boolean = true, isDiv2: Boolean = true, isDiv3: Boolean = true, isOther: Boolean = true){
    enum class Type{
        DIV1, DIV2, DIV3, OTHER
    }

    private var bits = EnumSet.noneOf(Type::class.java)
    fun setType(value: Boolean, type: ContestType.Type) : Boolean{
        val before = bits.contains(type)
        if (value)
            bits.add(type)
        else
            bits.remove(type)

        return before != value
    }

    var div1: Boolean
        get() = bits.contains(Type.DIV1)
        set(value){
            setType(value, Type.DIV1)
        }

    var div2: Boolean
        get() = bits.contains(Type.DIV2)
        set(value){
            setType(value, Type.DIV2)
        }

    var div3: Boolean
        get() = bits.contains(Type.DIV3)
        set(value){
            setType(value, Type.DIV3)
        }

    var other: Boolean
        get() = bits.contains(Type.OTHER)
        set(value){
            setType(value, Type.OTHER)
        }

    init {
        div1 = isDiv1
        div2 = isDiv2
        div3 = isDiv3
        other = isOther
    }

    fun isDiv() = div1 || div2 || div3

    fun contains(other: ContestType) =  (div1 && other.div1) || (div2 && other.div2) || (div3 && other.div3) || (this.other && other.other)

    override fun toString() = "div1:$div1, div2:$div2, div3:$div3, other:$other"
    override fun hashCode() = bits.hashCode()
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
            val contestType = ContestType(false, false, false, false)
            if (name.contains("Div. 1", true))
                contestType.div1 = true
            if (name.contains("Div. 2", true))
                contestType.div2 = true
            if (name.contains("Div. 3", true))
                contestType.div3 = true
            if (!contestType.isDiv())
                contestType.other = true


            return Contest(id, name, contestType, phase, durationSeconds, startTimeSeconds)
        }
    }
}

fun Contest.getUrl() = "https://codeforces.com/contests/$id"