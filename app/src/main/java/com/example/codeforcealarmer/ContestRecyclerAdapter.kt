package com.example.codeforcealarmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        val curContest = data?.get(position)

        if (curContest != null) {
            holder.id.text = curContest.id.toString()
            holder.name.text = curContest.name
            holder.length.text = FormatHelper.formatSeconds(curContest.durationSeconds)
            holder.startTime.text = FormatHelper.formatTime(curContest.startTimeSeconds)
        }
    }

    class ContestViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val id : TextView = view.findViewById(R.id.id)
        val name : TextView = view.findViewById(R.id.name)
        val length : TextView = view.findViewById(R.id.length)
        val startTime : TextView = view.findViewById(R.id.start_time)
    }
}