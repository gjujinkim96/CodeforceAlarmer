package com.example.codeforcealarmer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contest_recycler_item.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

enum class Sorting{
    LATEST, OLDEST
}

class ContestRecyclerAdapter(private val context: Context, private var divFilter: ContestType, private var startTime: LocalTime,
                             private var endTime: LocalTime, private var sortingBy: Sorting, private var data: MutableList<Contest>,
                             private val hasAlarm: Boolean)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {
    private var showingData: MutableList<Contest> = mutableListOf()

    constructor(context: Context, sortingBy: Sorting, hasAlarm: Boolean) : this(context, ContestType(),
        LocalTime.of(0, 0), LocalTime.of(23, 59), sortingBy, mutableListOf(), hasAlarm)

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

        notifyDataSetChanged()
    }

    fun changeSetting(newStartTime: LocalTime? = null,
                      newEndTime: LocalTime? = null,
                      newIsDiv1Checked: Boolean? = null,
                      newIsDiv2Checked: Boolean? = null,
                      newIsDiv3Checked: Boolean? = null,
                      newIsOtherChecked: Boolean? = null){
        var hasDataSetChanged = false
        if (newStartTime != null && startTime != newStartTime){
            startTime = newStartTime
            hasDataSetChanged = true
        }

        if (newEndTime != null && endTime != newEndTime){
            endTime = newEndTime
            hasDataSetChanged = true
        }

        if (newIsDiv1Checked != null && divFilter.setType(newIsDiv1Checked, ContestType.Type.DIV1))
            hasDataSetChanged = true

        if (newIsDiv2Checked != null && divFilter.setType(newIsDiv2Checked, ContestType.Type.DIV2))
            hasDataSetChanged = true

        if (newIsDiv3Checked != null && divFilter.setType(newIsDiv3Checked, ContestType.Type.DIV3))
            hasDataSetChanged = true

        if (newIsOtherChecked != null && divFilter.setType(newIsOtherChecked, ContestType.Type.OTHER))
            hasDataSetChanged = true

        if (hasDataSetChanged){
            makeShowingData()
        }
    }

    fun updateData(newData : MutableList<Contest>){
        data = newData
        makeShowingData()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false).apply{
            findViewById<Button>(R.id.recycler_alarm_button).visibility = if (hasAlarm) View.VISIBLE else View.GONE
        }

        return ContestViewHolder(newView)
    }

    override fun getItemCount() = showingData.size

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        holder.bind(showingData[position])
    }

    inner class ContestViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), LayoutContainer{
        override val containerView: View?
            get() = itemView


        fun bind(contest: Contest){
            contestname.text = contest.name
            contest_phase.text = contest.phase.toString()
            contest_duration.text = FormatHelper.formatSeconds(contest.durationSeconds)
            contest_start_time.text = FormatHelper.formatTime(contest.startTimeSeconds)

            containerView?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(contest.getUrl())
                }

                context.startActivity(intent)
            }

            val startTime: Long? = contest.startTimeSeconds
            if (startTime == null){
                recycler_alarm_button.text = "X"
            }else{
                recycler_alarm_button.setOnCheckedChangeListener{ _, isChecked ->
                        val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?:
                        throw Exception("Expected AlarmManger")

                        val alarmIntent: PendingIntent = Intent(context, AlarmReceiver::class.java).let {
                            PendingIntent.getBroadcast(context, 0, it, 0)
                        }


                        if (isChecked){
                            Log.v("ALARM_TEST", "set alarm")
                            alarmMgr.set(AlarmManager.RTC, System.currentTimeMillis() + (5* 1000), alarmIntent)
                            //alarmMgr.set(AlarmManager.RTC, startTime - 60 * 60 * 1000, alarmIntent)
                        }else{
                            Log.v("ALARM_TEST", "cancel alarm")
                            alarmMgr.cancel(alarmIntent)
                        }
                }
            }
        }
    }
}