package com.example.codeforcealarmer.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.ContestFilter
import com.example.codeforcealarmer.datalayer.dataholder.ContestWithAlarm
import com.example.codeforcealarmer.datalayer.repo.AlarmOffsetRepo
import com.example.codeforcealarmer.datalayer.repo.ContestFilterRepo
import com.example.codeforcealarmer.datalayer.repo.ContestRepo
import com.example.codeforcealarmer.datalayer.repo.ContestWithAlarmRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime

class BeforeContestViewModel(
    private val contestWithAlarmRepo: ContestWithAlarmRepo,
    private val contestFilterRepo: ContestFilterRepo,
    private val alarmRepo: AlarmOffsetRepo
)
    : ViewModel() {
    val contestsData = contestWithAlarmRepo.getBeforeContests()
    val filterLiveData: LiveData<ContestFilter> = contestFilterRepo.contestFilterLiveData
    val filteredData: MediatorLiveData<List<ContestWithAlarm>> = MediatorLiveData()

    init {
        filteredData.apply{
            addSource(contestsData){

                filteredData.value = filter(contestsData, filterLiveData)
            }
            addSource(filterLiveData){
                Log.v("CHANGED_DATA", "filter")
                filteredData.value = filter(contestsData, filterLiveData)
            }
        }


        contestFilterRepo.load()
    }

    fun changeSetting(
        newStartTime: LocalTime? = null,
        newEndTime: LocalTime? = null,
        newDiv1: Boolean? = null,
        newDiv2: Boolean? = null,
        newDiv3: Boolean? = null,
        newOther: Boolean? = null) {
        val newFilter = filterLiveData.value?.copyWithNull(newStartTime, newEndTime, newDiv1, newDiv2, newDiv3, newOther)
        if (newFilter != null && newFilter != filterLiveData.value){
            Log.v("FILTER_DEBUG", "ViewModel: changedSetting: new filter is not same as old")
            contestFilterRepo.change(newFilter)
        }
    }

    private fun filter(
        contestLiveData: LiveData<List<ContestWithAlarm>>,
        contestFilterLivedata: LiveData<ContestFilter>) : List<ContestWithAlarm> {
        Log.v("FILTER_DEBUG", "ViewModel: filter")
        val contestData = contestLiveData.value
        val contestFilter = contestFilterLivedata.value

        if (contestData == null)
            return mutableListOf()

        if (contestFilter == null)
            return contestData

        val ret = mutableListOf<ContestWithAlarm>()
        contestData.forEach{
            if (contestFilter.contains(it))
                ret.add(it)
        }

        return ret
    }

    fun addAlarm(id: Int, offset: Long){
        Log.v("ALARM_INPUT", "addAlarm")
        viewModelScope.launch(Dispatchers.IO) {
            Log.v("ALARM_INPUT", "addAlarm:ViewScope")
            alarmRepo.insert(AlarmOffset(id, offset))
            Log.v("ALARM_INPUT", "some + " + alarmRepo.getAll().toString())
        }
    }

    fun delAlarm(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            Log.v("ALARM_INPUT", "delete from view model")
            alarmRepo.delete(id)
        }
    }

    override fun onCleared() {
        super.onCleared()

        contestFilterRepo.save()
    }
}