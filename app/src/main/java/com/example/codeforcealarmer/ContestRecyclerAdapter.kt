package com.example.codeforcealarmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contest_recycler_item.*
import java.util.*
import kotlin.collections.ArrayList

class ContestRecyclerAdapter(private val context: Context, private var phaseFilter: EnumSet<Phase>,
                             private var divFilter: ContestType, private var data: ArrayList<Contest>?)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {
    private var phaseFilteredData: ArrayList<Contest>
    private var showingData: ArrayList<Contest>
    init {
        phaseFilteredData = filterPhaseData(data)
        showingData = filterDivData(phaseFilteredData)
    }

    private fun filterPhaseData(rawData: ArrayList<Contest>?) : ArrayList<Contest>{
        val ret = arrayListOf<Contest>()
        rawData?.forEach {
            if (phaseFilter.contains(it.phase))
                ret.add(it)
        }

        return ret
    }

    private fun filterDivData(rawData: ArrayList<Contest>?) : ArrayList<Contest>{
        val ret = arrayListOf<Contest>()
        rawData?.forEach {
            if (divFilter.contains(it.contestType))
                ret.add(it)
        }

        return ret
    }

    fun changeDivFilter(newValue: Boolean, toChange: ContestType.Type){
        if (divFilter.setType(newValue, toChange)){
            showingData = filterDivData(phaseFilteredData)
            notifyDataSetChanged()
        }
    }

    fun updateData(newData : ArrayList<Contest>?){
        data = newData
        phaseFilteredData = filterPhaseData(data)
        showingData = filterDivData(phaseFilteredData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false)
        return ContestViewHolder(newView)
    }

    override fun getItemCount() = showingData.size

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        holder.bind(showingData[position])
    }

    class ContestViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), LayoutContainer{
        override val containerView: View?
            get() = itemView


        fun bind(contest: Contest){
            contest_id.text = "ID: " + contest.id.toString()
            contestname.text = "NAME: " + contest.name
            contest_phase.text = "PHASE: " + contest.phase.toString()
            contest_duration.text = "DURATION: " + FormatHelper.formatSeconds(contest.durationSeconds)
            contest_start_time.text = "START_TIME: " + FormatHelper.formatTime(contest.startTimeSeconds)
            contest_web_url.text = contest.getUrl()
            main_div1.isChecked = contest.contestType.div1
            contest_div2.isChecked = contest.contestType.div2
            contest_div3.isChecked = contest.contestType.div3
            contest_other.isChecked = contest.contestType.other

        }
    }
}