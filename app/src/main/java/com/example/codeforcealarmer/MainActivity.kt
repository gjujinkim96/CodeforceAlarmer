package com.example.codeforcealarmer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<MutableList<Contest>>, TimePickerDialogFragment.ChangeTimeListener{
    companion object {
        const val CONTEST_LOADER = 1
    }

    lateinit var recyclerAdapter: ContestRecyclerAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)
        val data = arrayListOf<Contest>()

        val startLocalTime = LocalTime.of(0, 0)
        val endLocalTime = LocalTime.of(23, 59)

        recyclerAdapter = ContestRecyclerAdapter(this, ContestType(true, true),
            startLocalTime, endLocalTime, Sorting.OLDEST, data)
        onChangedTime(1, 0, 0)
        onChangedTime(2, 23, 59)

        recyclerView = contest_recycler_view

        recyclerView.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
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
    }

    override fun onResume() {
        super.onResume()

        LoaderManager.getInstance(this).initLoader(CONTEST_LOADER, null, this)

        val sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt(getString(R.string.saved_start_hour), 0)
        val startMin = sharedPreferences.getInt(getString(R.string.saved_start_min), 0)
        val endHour = sharedPreferences.getInt(getString(R.string.saved_end_hour), 23)
        val endMin = sharedPreferences.getInt(getString(R.string.saved_end_min), 59)
        val startLocalTime = LocalTime.of(startHour, startMin)
        val endLocalTime = LocalTime.of(endHour, endMin)

        onChangedTime(1, startHour, startMin)
        onChangedTime(2, endHour, endMin)

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

        val sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<MutableList<Contest>> {
        val url = "https://codeforces.com/api/contest.list"
        return ContestLoader(this, url)
    }

    override fun onLoadFinished(loader: Loader<MutableList<Contest>>, data: MutableList<Contest>?) {
        val filteredData = mutableListOf<Contest>()
        val phaseFilter = EnumSet.range(Phase.BEFORE, Phase.CODING)
        data?.forEach {
            if (phaseFilter.contains(it.phase))
                filteredData.add(it)
        }

        recyclerAdapter.updateData(filteredData)
    }

    override fun onLoaderReset(loader: Loader<MutableList<Contest>>) {
        recyclerAdapter.updateData(mutableListOf())
    }

    fun showTimePicker(view: View){
        val button = view as? Button ?: return

        val id = when (button.id){
            R.id.button1 -> 1
            R.id.button2 -> 2
            else -> 0
        }

        val (hour, min) = button.text.split(":")
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("timePicker")
        if (prev != null)
            ft.remove(prev)
        ft.addToBackStack(null)

        val bundle = Bundle().apply{
            putInt("hour", hour.toInt())
            putInt("min", min.toInt())
        }

        val dialogFragment = TimePickerDialogFragment(id).apply{
            arguments = bundle
        }
        dialogFragment.show(ft, "timePicker")
    }

    override fun onChangedTime(clickedId: Int, hourOfDay: Int, minute: Int) {
        val localTime = LocalTime.of(hourOfDay, minute)
        val format = DateTimeFormatter.ofPattern("HH:mm")

        val view = when (clickedId){
            1 -> {
                recyclerAdapter.changeStartTime(localTime)
                button1
            }
            2 -> {
                recyclerAdapter.changeEndTime(localTime)
                button2
            }
            else -> return
        }

        view.text = localTime.format(format)
    }
}
