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
import com.example.codeforcealarmer.broadcast.AlarmReceiver
import com.example.codeforcealarmer.format.FormatHelper
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.getUrl
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contest_recycler_item.*

open class ContestRecyclerAdapter(private val context: Context, protected open var data: List<Contest>)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {

    fun updateData(newData : List<Contest>){
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false).apply{
            findViewById<Button>(R.id.recycler_alarm_button).visibility = View.GONE
        }

        return ContestViewHolder(newView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        holder.bind(data, position)
    }

    inner class ContestViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), LayoutContainer{
        override val containerView: View?
            get() = itemView


        fun bind(contests: List<Contest>, pos: Int) {
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
        }
    }
}