package com.example.codeforcealarmer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.before_contest_fragment.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class BeforeContestFragment : Fragment(), View.OnClickListener {
    private lateinit var recyclerAdapter: ContestRecyclerAdapter
    private val viewModel: ContestViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.before_contest_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerAdapter = ContestRecyclerAdapter(requireContext(), mutableListOf(), true)
        before_contest_recycler_view.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            loadingView = before_loadingIcon
            emptyView = before_empty_group
            emptyText = before_empty_text
            emptyIcon = before_empty_image
        }

        viewModel.apply{
            isLoading.observe(viewLifecycleOwner, Observer{
                before_contest_recycler_view.loading = it
            })

            beforeContests.observe(viewLifecycleOwner, Observer {
                Log.v("VIEWMODEL_TEST", "observed beforeContest")
                recyclerAdapter.updateData(it)
            })

            isInternetConnection.observe(viewLifecycleOwner, Observer {
                before_contest_recycler_view.isInternetConnection = it
            })
        }

        main_div1.setOnCheckedChangeListener{
                _, isChecked ->
            viewModel.changeBeforeSetting(newIsDiv1Checked = isChecked)
        }

        main_div2.setOnCheckedChangeListener{
                _, isChecked ->
            viewModel.changeBeforeSetting(newIsDiv2Checked = isChecked)
        }

        main_div3.setOnCheckedChangeListener{
                _, isChecked ->
            viewModel.changeBeforeSetting(newIsDiv3Checked = isChecked)
        }

        main_other.setOnCheckedChangeListener{
                _, isChecked ->
            viewModel.changeBeforeSetting(newIsOtherChecked = isChecked)
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

        viewModel.changeBeforeSetting(startLocalTime, endLocalTime, isDiv1Checked, isDiv2Checked, isDiv3Checked, isOtherChecked)
    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
        val startTime = viewModel.startTime
        val startHour = startTime.hour
        val startMin = startTime.minute

        val endTime = viewModel.endTime
        val endHour = endTime.hour
        val endMin = endTime.minute

        val divFilter = viewModel.divFilter

        sharedPreferences.edit().apply{
            putInt(getString(R.string.saved_start_hour), startHour)
            putInt(getString(R.string.saved_start_min), startMin)
            putInt(getString(R.string.saved_end_hour), endHour)
            putInt(getString(R.string.saved_end_min), endMin)
            putBoolean(getString(R.string.saved_is_div1), divFilter.div1)
            putBoolean(getString(R.string.saved_is_div2), divFilter.div2)
            putBoolean(getString(R.string.saved_is_div3), divFilter.div3)
            putBoolean(getString(R.string.saved_is_other), divFilter.other)
            apply()
        }
    }

    fun changeTime(clickedId: Int, hourOfDay: Int, minute: Int) {
        val localTime = LocalTime.of(hourOfDay, minute)
        val format = DateTimeFormatter.ofPattern("HH:mm")

        val view = when (clickedId){
            1 -> {
                viewModel.changeBeforeSetting(newStartTime = localTime)
                before_time_button
            }
            2 -> {
                viewModel.changeBeforeSetting(newEndTime = localTime)
                after_time_button
            }
            else -> return
        }

        view.text = localTime.format(format)
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
            putInt("clicked_id", id)
        }


        val dialogFragment = TimePickerDialogFragment().apply {
            arguments = bundle
            setTargetFragment(this@BeforeContestFragment, 0)
        }

        dialogFragment.show(ft, "timePicker")

    }
}