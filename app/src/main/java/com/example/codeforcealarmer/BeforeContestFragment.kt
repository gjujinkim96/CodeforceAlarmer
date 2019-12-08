package com.example.codeforcealarmer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.before_contest_fragment.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class BeforeContestFragment : Fragment(), ContestDataUpdater, View.OnClickListener {
    lateinit var recyclerAdapter: ContestRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("ORDER", "BeforeContestFragment:onCreate")

        val data = mutableListOf<Contest>()

        val startLocalTime = LocalTime.of(0, 0)
        val endLocalTime = LocalTime.of(23, 59)

        recyclerAdapter = ContestRecyclerAdapter(requireContext(), ContestType(true, true),
            startLocalTime, endLocalTime, Sorting.OLDEST, data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.before_contest_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        changeTime(1, 0, 0)
        changeTime(2, 23, 59)

        contest_recycler_view.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            loadingView = loadingIcon
            emptyView = empty_group
        }

        main_div1.setOnCheckedChangeListener{
                _, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.DIV1)
        }

        main_div2.setOnCheckedChangeListener{
                _, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.DIV2)
        }

        main_div3.setOnCheckedChangeListener{
                _, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.DIV3)
        }

        main_other.setOnCheckedChangeListener{
                _, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.OTHER)
        }

        before_time_button.setOnClickListener(this)
        after_time_button.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt(getString(R.string.saved_start_hour), 0)
        val startMin = sharedPreferences.getInt(getString(R.string.saved_start_min), 0)
        val endHour = sharedPreferences.getInt(getString(R.string.saved_end_hour), 23)
        val endMin = sharedPreferences.getInt(getString(R.string.saved_end_min), 59)
        val startLocalTime = LocalTime.of(startHour, startMin)
        val endLocalTime = LocalTime.of(endHour, endMin)

        changeTime(1, startHour, startMin)
        changeTime(2, endHour, endMin)

        val isDiv1Checked = sharedPreferences.getBoolean(getString(R.string.saved_is_div1), true)
        val isDiv2Checked = sharedPreferences.getBoolean(getString(R.string.saved_is_div2), true)
        val isDiv3Checked = sharedPreferences.getBoolean(getString(R.string.saved_is_div3), true)
        val isOtherChecked = sharedPreferences.getBoolean(getString(R.string.saved_is_other), true)

        main_div1.isChecked = isDiv1Checked
        main_div2.isChecked = isDiv2Checked
        main_div3.isChecked = isDiv3Checked
        main_other.isChecked = isOtherChecked

        recyclerAdapter.apply{
            changeStartTime(startLocalTime)
            changeEndTime(endLocalTime)
            changeDivFilter(isDiv1Checked, ContestType.Type.DIV1)
            changeDivFilter(isDiv2Checked, ContestType.Type.DIV2)
            changeDivFilter(isDiv3Checked, ContestType.Type.DIV3)
            changeDivFilter(isOtherChecked, ContestType.Type.OTHER)
        }
    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
        val startHour = recyclerAdapter.getStartHour()
        val startMin = recyclerAdapter.getStartMin()
        val endHour = recyclerAdapter.getEndHour()
        val endMin = recyclerAdapter.getEndMin()

        sharedPreferences.edit().apply{
            putInt(getString(R.string.saved_start_hour), startHour)
            putInt(getString(R.string.saved_start_min), startMin)
            putInt(getString(R.string.saved_end_hour), endHour)
            putInt(getString(R.string.saved_end_min), endMin)
            putBoolean(getString(R.string.saved_is_div1), recyclerAdapter.isDiv1())
            putBoolean(getString(R.string.saved_is_div2), recyclerAdapter.isDiv2())
            putBoolean(getString(R.string.saved_is_div3), recyclerAdapter.isDiv3())
            putBoolean(getString(R.string.saved_is_other), recyclerAdapter.isOther())
            apply()
        }
    }

    fun changeTime(clickedId: Int, hourOfDay: Int, minute: Int) {
        val localTime = LocalTime.of(hourOfDay, minute)
        val format = DateTimeFormatter.ofPattern("HH:mm")

        val view = when (clickedId){
            1 -> {
                recyclerAdapter.changeStartTime(localTime)
                before_time_button
            }
            2 -> {
                recyclerAdapter.changeEndTime(localTime)
                after_time_button
            }
            else -> return
        }

        view.text = localTime.format(format)
    }

    override fun onLoadingStart() {
        contest_recycler_view.loading = true
    }

    override fun onLoadingEnd() {
        contest_recycler_view.loading = false
    }

    override fun update(newData: MutableList<Contest>) {
        recyclerAdapter.updateData(newData)
    }


    override fun onClick(view: View?){
        val button = view as? Button ?: return

        val id = when (button.id){
            R.id.before_time_button -> 1
            R.id.after_time_button -> 2
            else -> 0
        }

        val (hour, min) = button.text.split(":")
        val fm = fragmentManager ?: throw NullPointerException("failed to get framgentManger")
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag("timePicker")
        if (prev != null)
            ft.remove(prev)
        ft.addToBackStack(null)

        val bundle = Bundle().apply {
            putInt("hour", hour.toInt())
            putInt("min", min.toInt())
        }

        val dialogFragment = TimePickerDialogFragment(id).apply {
            arguments = bundle
        }
        dialogFragment.show(ft, "timePicker")

    }
}