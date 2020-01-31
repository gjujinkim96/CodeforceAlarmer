package com.example.codeforcealarmer.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.codeforcealarmer.format.FormatHelper
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.getUrl
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.before_contest_recycler_item.*

open class ContestRecyclerAdapter(private val context: Context, protected open var data: List<Contest>)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun updateData(newData : List<Contest>){
        differ.submitList(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.before_contest_recycler_item, parent, false)

        return ContestViewHolder(newView)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        holder.bind(differ.currentList, position)
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

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contest>() {
            override fun areItemsTheSame(oldItem: Contest, newItem: Contest) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Contest, newItem: Contest) =
                oldItem == newItem
        }
    }
}