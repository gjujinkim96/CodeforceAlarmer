package com.example.codeforcealarmer.datalayer.repo

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.codeforcealarmer.datalayer.dataholder.ContestFilter
import com.example.codeforcealarmer.datalayer.dataholder.ContestType
import com.example.codeforcealarmer.R
import org.threeten.bp.LocalTime

class ContestFilterRepo(val context: Context) {
    val contestFilterLiveData: MutableLiveData<ContestFilter> = MutableLiveData()

    fun change(newFilter: ContestFilter){
        Log.v("FILTER_DEBUG", "repo: change: new filter: $newFilter")
        contestFilterLiveData.value = newFilter
    }

    fun save(){
        val contestFilter = contestFilterLiveData.value ?: return
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_key),
            Context.MODE_PRIVATE)
        val startTime = contestFilter.startTime
        val startHour = startTime.hour
        val startMin = startTime.minute

        val endTime = contestFilter.endTime
        val endHour = endTime.hour
        val endMin = endTime.minute

        val divFilter = contestFilter.divFilter

        val timeEnabled = contestFilter.timeEnabled

        sharedPreferences.edit().apply{
            putInt(context.getString(R.string.saved_start_hour), startHour)
            putInt(context.getString(R.string.saved_start_min), startMin)
            putInt(context.getString(R.string.saved_end_hour), endHour)
            putInt(context.getString(R.string.saved_end_min), endMin)
            putBoolean(context.getString(R.string.saved_is_div1), divFilter.div1)
            putBoolean(context.getString(R.string.saved_is_div2), divFilter.div2)
            putBoolean(context.getString(R.string.saved_is_div3), divFilter.div3)
            putBoolean(context.getString(R.string.saved_is_other), divFilter.other)
            putBoolean(context.getString(R.string.saved_time_enabled), timeEnabled)
            apply()
        }
    }

    fun load(){
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt(context.getString(R.string.saved_start_hour), 10)
        val startMin = sharedPreferences.getInt(context.getString(R.string.saved_start_min), 0)
        val endHour = sharedPreferences.getInt(context.getString(R.string.saved_end_hour), 22)
        val endMin = sharedPreferences.getInt(context.getString(R.string.saved_end_min), 0)
        val startLocalTime = LocalTime.of(startHour, startMin)
        val endLocalTime = LocalTime.of(endHour, endMin)
        val timeEnabled = sharedPreferences.getBoolean(context.getString(R.string.saved_time_enabled), false)


        val isDiv1Checked = sharedPreferences.getBoolean(context.getString(R.string.saved_is_div1), true)
        val isDiv2Checked = sharedPreferences.getBoolean(context.getString(R.string.saved_is_div2), true)
        val isDiv3Checked = sharedPreferences.getBoolean(context.getString(R.string.saved_is_div3), true)
        val isOtherChecked = sharedPreferences.getBoolean(context.getString(R.string.saved_is_other), true)

        val newContestType =
            ContestType(
                isDiv1Checked,
                isDiv2Checked,
                isDiv3Checked,
                isOtherChecked
            )

        contestFilterLiveData.value =
            ContestFilter(
                newContestType,
                startLocalTime,
                endLocalTime,
                timeEnabled
            )
    }
}