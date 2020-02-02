package com.example.codeforcealarmer.ui.activity_fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.broadcast.AlarmReceiver
import com.example.codeforcealarmer.datalayer.dataholder.AlarmData
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import com.example.codeforcealarmer.ui.adapters.ContestWithAlarmRecyclerAdapter
import com.example.codeforcealarmer.ui.adapters.disableAlarmButton
import com.example.codeforcealarmer.viewmodels.BeforeContestViewModel
import com.example.codeforcealarmer.viewmodels.BeforeViewModelFactory
import kotlinx.android.synthetic.main.before_contest_fragment.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class BeforeContestFragment : Fragment(), View.OnClickListener, ContestWithAlarmRecyclerAdapter.OnCheckedAlarmButton {
    private lateinit var recyclerAdapter: ContestWithAlarmRecyclerAdapter
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: BeforeContestViewModel by viewModels { viewModelFactory }

    var cnt = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModelFactory =
            BeforeViewModelFactory(
                (activity?.application as MyApplication).appContainer.contestWithAlarmRepo,
                (activity?.application as MyApplication).appContainer.contestFilterRepo,
                (activity?.application as MyApplication).appContainer.alarmOffsetRepo
            )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.before_contest_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerAdapter =
            ContestWithAlarmRecyclerAdapter(
                requireContext(),
                mutableListOf(),
                this
            )
        before_contest_recycler_view.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            loadingView = before_loadingIcon
            emptyView = before_empty_group
            emptyText = before_empty_text
            emptyIcon = before_empty_image
        }

        viewModel.filteredData.observe(viewLifecycleOwner, Observer {
            Log.v("VIEWMODEL_TEST", "observed beforeContest")
            recyclerAdapter.updateData(it)
        })

        viewModel.filterLiveData.observe(viewLifecycleOwner, Observer {
            Log.v("FILTER_DEBUG", "before_frag: observed filter: $it")
            main_div1.isChecked = it.divFilter.div1
            main_div2.isChecked = it.divFilter.div2
            main_div3.isChecked = it.divFilter.div3
            main_other.isChecked = it.divFilter.other

            // change click id to constant
            updateTimeView(1, it.startTime.hour, it.startTime.minute)
            updateTimeView(2, it.endTime.hour, it.endTime.minute)
        })

        main_div1.setOnCheckedChangeListener{
                _, isChecked ->
            viewModel.changeSetting(newDiv1 = isChecked)
        }

        main_div2.setOnCheckedChangeListener{
                _, isChecked ->
            viewModel.changeSetting(newDiv2 = isChecked)
        }

        main_div3.setOnCheckedChangeListener{
                _, isChecked ->
            Log.v("FILTER_DEBUG", "changed div3")
            viewModel.changeSetting(newDiv3 = isChecked)
        }

        main_other.setOnCheckedChangeListener{
                _, isChecked ->
            Log.v("FILTER_DEBUG", "changed other")
            viewModel.changeSetting(newOther = isChecked)
        }

        before_time_button.setOnClickListener(this)
        after_time_button.setOnClickListener(this)
    }

    override fun onChecked(toggleButton: ToggleButton, id: Int, startTime: Long, isChecked: Boolean, alarmData: AlarmData){
        val curTime = System.currentTimeMillis()
        if (curTime + AlarmData.getOffsetInMilli(alarmData) >= startTime * 1000){
            disableAlarmButton(toggleButton)
            return
        }

        val alarmMgr: AlarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?:
        throw Exception("Expected AlarmManger")

        val alarmSetData =
            AlarmOffsetWithStartTime(
                id,
                startTime,
                alarmData
            )

        Log.v("SERVICE_TEST", "alarmdata $alarmSetData")

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra(requireContext().getString(R.string.intent_alarm_data), ParcelConverter.marshall(alarmSetData))

        val alarmIntent = PendingIntent.getBroadcast(requireContext(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT)


        if (isChecked){
            Log.v("ALARM_TEST", "set alarm")
            if (alarmData != AlarmData.ZERO){
                alarmMgr.set(AlarmManager.RTC, startTime * 1000 - AlarmData.getOffsetInMilli(alarmData), alarmIntent)
                //alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AlarmData.getOffsetInMinutes(alarmData) * 100, alarmIntent)
            }
            else{
                alarmMgr.setExact(AlarmManager.RTC, startTime * 1000, alarmIntent)
                //alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), alarmIntent)
            }


            Log.v("ALARM_SET", "    at ${System.currentTimeMillis()  + 30 * 1000}")
            Log.v("ALARM_SET", "set at ${System.currentTimeMillis() }")
            viewModel.addAlarm(id, alarmData)
        }else{
            Log.v("ALARM_TEST", "cancel alarm")
            alarmMgr.cancel(alarmIntent)
            viewModel.delAlarm(id, alarmData)
        }
    }

    fun updateTimeView(clickedId: Int, hourOfDay: Int, minute: Int){
        val localTime = LocalTime.of(hourOfDay, minute)
        val format = DateTimeFormatter.ofPattern("HH:mm")

        val view = when (clickedId){
            1 -> {
                before_time_button
            }
            2 -> {
                after_time_button
            }
            else -> return
        }

        view.text = localTime.format(format)
    }

    fun changeTime(clickedId: Int, hourOfDay: Int, minute: Int) {
        val localTime = LocalTime.of(hourOfDay, minute)
        val view = when (clickedId){
            1 -> {
                viewModel.changeSetting(newStartTime = localTime)
            }
            2 -> {
                viewModel.changeSetting(newEndTime = localTime)
            }
            else -> return
        }
    }

    override fun onClick(view: View?){
        val button = view as? Button ?: return

        val id = when (button.id){
            R.id.before_time_button -> 1
            R.id.after_time_button -> 2
            else -> 0
        }

        val (hour, min) = button.text.split(":")
        val fm = activity?.supportFragmentManager ?: throw NullPointerException("failed to get framgentManger")
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag("timePicker")
        if (prev != null)
            ft.remove(prev)
        ft.addToBackStack(null)

        val bundle = Bundle().apply {
            putInt("hour", hour.toInt())
            putInt("min", min.toInt())
            putInt("clicked_id", id)
        }


        val dialogFragment = TimePickerDialogFragment()
            .apply {
            arguments = bundle
            setTargetFragment(this@BeforeContestFragment, 0)
        }

        dialogFragment.show(ft, "timePicker")

    }
}