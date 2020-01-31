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
import com.example.codeforcealarmer.datalayer.dataholder.ContestWithAlarm
import com.example.codeforcealarmer.datalayer.dataholder.getOffsetTime
import com.example.codeforcealarmer.datalayer.dataholder.getUrl
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
            val minutesOffsets = listOf(60, 15, 5, 0)

            val startTime: Long? = contest.startTimeSeconds
            if (startTime == null) {
                toggleButtons.forEach {
                    disableAlarmButton(it)
                }
            }else {
                val curTime = System.currentTimeMillis()
                for ((index, button) in toggleButtons.withIndex()){
                    if (curTime + minutesOffsets[index] * 60 * 1000 < startTime){
                        enableAlarmButton(button)
                        button.textOff = texts[index]

                        // TODO
//                        button.isChecked =
//                        button.setOnCheckedChangeListener{
//
//                        }
                    }else{
                        disableAlarmButton(button)
                    }
                }
                recycler_alarm_button.isEnabled = true
                recycler_alarm_button.setOnCheckedChangeListener(null)
                recycler_alarm_button.isChecked = contest.offsetTime != null
                val offset = contest.getOffsetTime()
                if (recycler_alarm_button.isChecked){

                }else{

                }

                recycler_alarm_button.setOnCheckedChangeListener{ _, isChecked ->
                    onAlarmChecked.onChecked(contest.id, startTime, isChecked, contest.getOffsetTime())
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
        fun onChecked(id: Int, startTime: Long, isChecked: Boolean, offsetTime: Long?)
    }

    inner class DiffUtilCallback(val oldData: List<ContestWithAlarm>, val newData: List<ContestWithAlarm>) : DiffUtil.Callback(){
        override fun getOldListSize() = oldData.size

        override fun getNewListSize() = newData.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldData[oldItemPosition].id == newData[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldData[oldItemPosition] == newData[newItemPosition]
    }

    private fun disableAlarmButton(toggleButton: ToggleButton){
        toggleButton.apply{
            text = "X"
            isEnabled = false
            setOnCheckedChangeListener(null)
        }
    }

    private fun enableAlarmButton(toggleButton: ToggleButton){
        toggleButton.apply{
            isEnabled = true
            setOnCheckedChangeListener(null)
        }
    }
}
