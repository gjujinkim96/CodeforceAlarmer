package com.example.codeforcealarmer.ui.adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.broadcast.AlarmReceiver
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.ContestWithAlarm
import com.example.codeforcealarmer.datalayer.dataholder.getUrl
import com.example.codeforcealarmer.format.FormatHelper
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contest_recycler_item.*

class ContestWithAlarmRecyclerAdapter(val context: Context, var data: List<ContestWithAlarm>, var onAlarmChecked: OnCheckedAlarmButton)
    : RecyclerView.Adapter<ContestWithAlarmRecyclerAdapter.CAViewHolder>()
{
    inner class CAViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(contests: List<ContestWithAlarm>, pos: Int) {
            val contest = contests[pos]
            contestname.text = contest.name
            contest_phase.text = contest.phase.toString()
            contest_duration.text =
                FormatHelper.formatSeconds(
                    contest.durationSeconds
                )
            contest_start_time.text =
                FormatHelper.formatTime(
                    contest.startTimeSeconds
                )

            containerView?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(contest.getUrl())
                }

                context.startActivity(intent)
            }

            val startTime: Long? = contest.startTimeSeconds
            if (startTime == null) {
                recycler_alarm_button.text = "X"
                recycler_alarm_button.isEnabled = false
                recycler_alarm_button.setOnCheckedChangeListener(null)
            }else {
                recycler_alarm_button.isEnabled = true
                recycler_alarm_button.setOnCheckedChangeListener(null)
                recycler_alarm_button.isChecked = contest.offset != null
                recycler_alarm_button.setOnCheckedChangeListener{ _, isChecked ->
                    onAlarmChecked.OnChecked(contest.id, startTime, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CAViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false).apply {
            findViewById<Button>(R.id.recycler_alarm_button).visibility = View.VISIBLE
        }
        return CAViewHolder(inflatedView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CAViewHolder, position: Int) {
        holder.bind(data, position)
    }

    fun updateData(newData : List<ContestWithAlarm>){
        data = newData
        notifyDataSetChanged()
    }

    interface OnCheckedAlarmButton {
        fun OnChecked(id: Int, startTime: Long, isChecked: Boolean)
    }
}
