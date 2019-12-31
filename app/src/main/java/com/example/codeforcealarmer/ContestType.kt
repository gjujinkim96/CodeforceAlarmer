package com.example.codeforcealarmer

enum class Type{
    DIV1, DIV2, DIV3, OTHER
}

data class ContestType(var div1: Boolean, var div2: Boolean, var div3: Boolean, var other: Boolean){
    companion object{
        fun makeAllTrue() = ContestType(true, true, true, true)
        fun makeAllFalse() = ContestType(false, false, false, false)
        fun makeTypeTrue(type: Type) : ContestType {
            val ret = makeAllFalse()
            ret.setType(true, type)
            return ret
        }
    }

    fun getType(type: Type) = when (type){
        Type.DIV1 -> div1
        Type.DIV2 -> div2
        Type.DIV3 -> div3
        Type.OTHER -> other
    }

    fun setType(value: Boolean, type: Type) {
        when (type) {
            Type.DIV1 -> div1 = value
            Type.DIV2 -> div2 = value
            Type.DIV3 -> div3 = value
            Type.OTHER -> other = value
        }
    }

    fun isDiv() = div1 || div2 || div3

    fun contains(other: ContestType) =  (div1 && other.div1) || (div2 && other.div2) || (div3 && other.div3) || (this.other && other.other)
}

// check contest entity is good enough
// contest type and phase
// whether emb and typeconverter make sense
// then make repo and dao and db