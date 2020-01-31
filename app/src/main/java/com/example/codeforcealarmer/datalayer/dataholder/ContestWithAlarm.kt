package com.example.codeforcealarmer.datalayer.dataholder

import androidx.room.Embedded

data class ContestWithAlarm(
    var id: Int,
    var name: String,
    @Embedded var contestType: ContestType,
    var phase: Phase,
    var durationSeconds: Long,
    var startTimeSeconds: Long?,
    var alarmsSet: BooleanArray
) {
    companion object {
        // alarms = alarms with contest id
        fun make(contest: Contest, alarms: List<AlarmData>?): ContestWithAlarm {
            val ret = contest.run{
                ContestWithAlarm(id, name, contestType, phase, durationSeconds, startTimeSeconds,
                    BooleanArray(AlarmData.values().size){ false })
            }

            alarms?.forEach {
                val idx = AlarmDataConverters.alarmDataToInt(it) ?: throw IllegalArgumentException()
                ret.alarmsSet[idx] = true
            }

            return ret
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContestWithAlarm

        if (id != other.id) return false
        if (name != other.name) return false
        if (contestType != other.contestType) return false
        if (phase != other.phase) return false
        if (durationSeconds != other.durationSeconds) return false
        if (startTimeSeconds != other.startTimeSeconds) return false
        if (!alarmsSet.contentEquals(other.alarmsSet)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + contestType.hashCode()
        result = 31 * result + phase.hashCode()
        result = 31 * result + durationSeconds.hashCode()
        result = 31 * result + (startTimeSeconds?.hashCode() ?: 0)
        result = 31 * result + alarmsSet.contentHashCode()
        return result
    }
}

fun ContestWithAlarm.getUrl() = "https://codeforces.com/contests/$id"

