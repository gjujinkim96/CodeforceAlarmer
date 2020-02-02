package com.example.codeforcealarmer.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.datalayer.dataholder.*
import com.example.codeforcealarmer.format.FormatHelper
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.before_contest_recycler_item.*

class ContestWithAlarmRecyclerAdapter(val context: Context, var data: List<ContestWithAlarm>, var onAlarmChecked: OnCheckedAlarmButton)
    : RecyclerView.Adapter<ContestWithAlarmRecyclerAdapter.CAViewHolder>()
{
    inner class CAViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(contests: List<ContestWithAlarm>, pos: Int) {
            val contest = contests[pos]
            before_contestname.text = contest.name
            before_contest_phase.text = contest.phase.toString()
            before_contest_duration.text =
                FormatHelper.formatSeconds(
                    contest.durationSeconds
                )
            before_contest_start_time.text =
                FormatHelper.formatTime(
                    contest.startTimeSeconds
                )

            containerView?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(contest.getUrl())
                }

                context.startActivity(intent)
            }

            val toggleButtons = listOf(before_hour_alarm, before_15_alarm, before_5_alarm, before_0_alarm)
            val texts = listOf("1 HOUR", "15 MIN", "5 MIN", "0 MIN")
            val alarmDatas = listOf(AlarmData.HOUR, AlarmData.FIFTEEN, AlarmData.FIVE, AlarmData.ZERO)

            val startTime: Long? = contest.startTimeSeconds
            if (startTime == null) {
                toggleButtons.forEach {
                    disableAlarmButton(it)
                }
            }else {
                val curTime = System.currentTimeMillis()
                for ((index, button) in toggleButtons.withIndex()){
                    val curAlarmData = alarmDatas[index]
                    if (curTime + AlarmData.getOffsetInMilli(curAlarmData) < startTime * 1000){
                        enableAlarmButton(button)
                        button.textOff = texts[index]
                        button.textOn = texts[index]
                        button.text = texts[index]


                        val alarmDataIndex = AlarmDataConverters().alarmDataToInt(curAlarmData) ?: throw IllegalArgumentException()
                        button.isChecked = contest.alarmsSet[alarmDataIndex]
                        button.setOnCheckedChangeListener{ _, isChecked ->
                            onAlarmChecked.onChecked(button, contest.id, startTime, isChecked, curAlarmData)
                        }
                    }else{
                        disableAlarmButton(button)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CAViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.before_contest_recycler_item, parent, false)

        return CAViewHolder(inflatedView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CAViewHolder, position: Int) {
        holder.bind(data, position)
    }

    fun updateData(newData : List<ContestWithAlarm>){
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(data, newData), false)
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnCheckedAlarmButton {
        fun onChecked(toggleButton: ToggleButton, id: Int, startTime: Long, isChecked: Boolean, alarmData: AlarmData)
    }

    inner class DiffUtilCallback(val oldData: List<ContestWithAlarm>, val newData: List<ContestWithAlarm>) : DiffUtil.Callback(){
        override fun getOldListSize() = oldData.size

        override fun getNewListSize() = newData.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldData[oldItemPosition].id == newData[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldData[oldItemPosition] == newData[newItemPosition]
    }
}

fun disableAlarmButton(toggleButton: ToggleButton){
    toggleButton.apply{
        text = "X"
        isEnabled = false
        setOnCheckedChangeListener(null)
    }
}

fun enableAlarmButton(toggleButton: ToggleButton){
    toggleButton.apply{
        isEnabled = true
        setOnCheckedChangeListener(null)
    }
}
