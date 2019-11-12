package com.example.codeforcealarmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContestRecyclerAdapter(val context: Context, var data: ArrayList<Contest>)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false)
        return ContestViewHolder(newView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val curContest = data[position]

        holder.name.text = curContest.name
    }

    class ContestViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.name)
    }
}