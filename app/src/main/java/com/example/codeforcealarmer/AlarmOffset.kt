package com.example.codeforcealarmer

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Contest::class,
    parentColumns = ["id"],
    childColumns = ["id"],
    onDelete = ForeignKey.CASCADE)])
data class AlarmOffset(
    @PrimaryKey var id: Int,
    var offset: Long
)