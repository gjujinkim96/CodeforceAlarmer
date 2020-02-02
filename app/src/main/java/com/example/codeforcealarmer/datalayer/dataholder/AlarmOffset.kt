package com.example.codeforcealarmer.datalayer.dataholder

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.TypeConverter

@Entity(
    primaryKeys = ["id", "data"],
    foreignKeys = [
        ForeignKey(entity = Contest::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AlarmOffset(
    var id: Int,
    var data: AlarmData
)


enum class AlarmData{
    HOUR, FIFTEEN, FIVE, ZERO;

    companion object{
        fun getOffsetInMinutes(data: AlarmData) = when (data){
            HOUR -> 60
            FIFTEEN -> 15
            FIVE -> 5
            ZERO -> 0
        }

        fun getOffsetInMilli(data: AlarmData) = getOffsetInMinutes(data) * 60 * 1000
    }
}

class AlarmDataConverters{
    @TypeConverter
    fun intToAlarmData(v: Int): AlarmData? =
        when (v) {
            AlarmData.HOUR.ordinal -> AlarmData.HOUR
            AlarmData.FIFTEEN.ordinal -> AlarmData.FIFTEEN
            AlarmData.FIVE.ordinal -> AlarmData.FIVE
            AlarmData.ZERO.ordinal -> AlarmData.ZERO
            else -> null
        }

    @TypeConverter
    fun alarmDataToInt(alarmData: AlarmData?): Int? = alarmData?.ordinal
}

