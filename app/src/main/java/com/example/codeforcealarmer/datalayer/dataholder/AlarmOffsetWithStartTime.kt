package com.example.codeforcealarmer.datalayer.dataholder

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlarmOffsetWithStartTime(
    var id: Int,
    var startTime: Long?,
    var alarmData: AlarmData) : Parcelable {

    companion object{
        val creator by lazy {
            parcelableCreator<AlarmOffsetWithStartTime>()
        }
    }
}

inline fun <reified T : Parcelable> parcelableCreator(): Parcelable.Creator<T>{
    val creator = T::class.java.getField("CREATOR").get(null)
    @Suppress("UNCHECKED_CAST")
    return creator as Parcelable.Creator<T>
}

class ParcelConverter{
    companion object{
        fun marshall(parcelable: Parcelable) : ByteArray {
            val parcel = Parcel.obtain()
            parcelable.writeToParcel(parcel, 0)
            val ret = parcel.marshall()
            parcel.recycle()

            return ret
        }

        fun unmarshall(bytes : ByteArray) = Parcel.obtain().apply{
            unmarshall(bytes, 0, bytes.size)
            setDataPosition(0)
        }


        fun <T> unmarshall(bytes : ByteArray, creator: Parcelable.Creator<T>) : T{
            val parcel = unmarshall(bytes)
            val ret = creator.createFromParcel(parcel)
            parcel.recycle()
            return ret
        }
    }
}