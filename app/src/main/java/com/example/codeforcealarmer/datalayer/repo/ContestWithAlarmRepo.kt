package com.example.codeforcealarmer.datalayer.repo

import androidx.lifecycle.MediatorLiveData
import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetDao
import com.example.codeforcealarmer.datalayer.dao.ContestDao
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.ContestWithAlarm
import com.example.codeforcealarmer.datalayer.dataholder.Phase

class ContestWithAlarmRepo(val contestDao: ContestDao, val alarmDao: AlarmOffsetDao) {
    fun getContests() : MediatorLiveData<List<ContestWithAlarm>> {
        val ret = MediatorLiveData<List<ContestWithAlarm>>()

        val beforeContests = contestDao.getBetweenPhases(Phase.BEFORE, Phase.CODING, true)
        val alarms = alarmDao.getAll()

        ret.addSource(beforeContests){
            ret.value = makeContestsWithAlarm(beforeContests.value, alarms.value)
        }

        ret.addSource(alarms){
            ret.value = makeContestsWithAlarm(beforeContests.value, alarms.value)
        }

        return ret
    }

    private fun makeContestsWithAlarm(contests: List<Contest>?, alarms: List<AlarmOffset>?) : List<ContestWithAlarm>{
        if (contests == null)
            return listOf()

        if (alarms == null)
            return contests.map {
                ContestWithAlarm.make(it, null)
            }


        val alarmsMap = alarms.groupBy({it.id}, {it.data})
        return contests.map {
            ContestWithAlarm.make(it, alarmsMap.get(it.id))
        }
    }
}