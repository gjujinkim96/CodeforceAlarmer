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
import kotlinx.coroutines.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class ContestRecyclerAdapter(private val context: Context, private var data: MutableList<Contest>, private val hasAlarm: Boolean)
    : RecyclerView.Adapter<ContestRecyclerAdapter.ContestViewHolder>() {

    fun updateData(newData : MutableList<Contest>){
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        val newView = LayoutInflater.from(context).inflate(R.layout.contest_recycler_item, parent, false).apply{
            findViewById<Button>(R.id.recycler_alarm_button).visibility = if (hasAlarm) View.VISIBLE else View.GONE
        }

        return ContestViewHolder(newView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        holder.bind(data[position])
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