package com.example.codeforcealarmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contest_recycler_item.*
import kotlinx.android.synthetic.main.contest_recycler_item.view.*

class ContestRecyclerAdapter(private val context: Context, var data: ArrayList<Contest>?)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {
    fun updateData(newData : ArrayList<Contest>?){
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false)
        return ContestViewHolder(newView)
    }

    override fun getItemCount() = data?.size ?: 0

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val tmpData = data
        if (tmpData != null)
            holder.bind(tmpData[position])
    }

    class ContestViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), LayoutContainer{
        override val containerView: View?
            get() = itemView


        fun bind(contest: Contest){
            contest_id.text = "ID: " + contest.id.toString()
            name.text = "NAME: " + contest.name
            phase.text = "PHASE: " + contest.phase.toString()
            length.text = "DURATION: " + FormatHelper.formatSeconds(contest.durationSeconds)
            start_time.text = "START_TIME: " + FormatHelper.formatTime(contest.startTimeSeconds)
            web_url.text = contest.getUrl()
            div1.isChecked = contest.contestType.div1
            div2.isChecked = contest.contestType.div2
            div3.isChecked = contest.contestType.div3
            other.isChecked = contest.contestType.other

        }
    }
}