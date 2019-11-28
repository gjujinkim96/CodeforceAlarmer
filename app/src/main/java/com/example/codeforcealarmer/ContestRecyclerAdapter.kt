package com.example.codeforcealarmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contest_recycler_item.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

enum class Sorting{
    LATEST, OLDEST
}

class ContestRecyclerAdapter(private val context: Context, private var divFilter: ContestType, private var startTime: LocalTime,
                             private var endTime: LocalTime, private var sortingBy: Sorting, private var data: MutableList<Contest>)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {
    var emptyStateListener: EmptyStateListener? = null
    private var showingData: MutableList<Contest> = mutableListOf()
    init {
        makeShowingData()
    }

    fun getStartHour() = startTime.hour
    fun getStartMin() = startTime.minute
    fun getEndHour() = endTime.hour
    fun getEndMin() = endTime.minute

    fun isDiv1() = divFilter.div1
    fun isDiv2() = divFilter.div2
    fun isDiv3() = divFilter.div3
    fun isOther() = divFilter.other

    private fun filter(rawData: MutableList<Contest>?) : MutableList<Contest>{
        val ret = arrayListOf<Contest>()

        if (rawData == null)
            return ret

        rawData.forEach{
            if (it.startTimeSeconds != null){
                val instant = Instant.ofEpochSecond(it.startTimeSeconds)
                val zoneId = ZoneId.systemDefault()
                val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
                val localTime = zonedDateTime.toLocalTime()
                if (divFilter.contains(it.contestType)){
                    if (startTime <= endTime){
                        if (startTime <= localTime && localTime <= endTime)
                            ret.add(it)
                    } else if (startTime <= localTime || localTime <= endTime)
                        ret.add(it)
                }
            }
        }

        return ret
    }

    private fun makeShowingData(){
        showingData = filter(data)
        when (sortingBy){
            Sorting.LATEST->{
                showingData.sortByDescending { it.startTimeSeconds }
            }
            Sorting.OLDEST->{
                showingData.sortBy { it.startTimeSeconds }
            }
        }

        val curEmptyStateListener = emptyStateListener

        if (curEmptyStateListener != null){
            if (showingData.isEmpty())
                curEmptyStateListener.onEmptyStateEnter()
            else
                curEmptyStateListener.onEmptyStateExit()
        }
    }

    fun changeDivFilter(newValue: Boolean, toChange: ContestType.Type){
        if (divFilter.setType(newValue, toChange)){
            makeShowingData()
            notifyDataSetChanged()
        }
    }

    fun changeStartTime(newStartTime: LocalTime){
        if (startTime != newStartTime){
            startTime = newStartTime
            makeShowingData()
            notifyDataSetChanged()
        }
    }

    fun changeEndTime(newEndTime: LocalTime){
        if (endTime != newEndTime){
            endTime = newEndTime
            makeShowingData()
            notifyDataSetChanged()
        }
    }

    fun updateData(newData : MutableList<Contest>){
        data = newData
        makeShowingData()
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

    interface EmptyStateListener{
        fun onEmptyStateEnter()
        fun onEmptyStateExit()
    }
}