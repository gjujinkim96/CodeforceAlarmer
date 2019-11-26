package com.example.codeforcealarmer

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.FragmentManager
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
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<Contest>>, TimePickerDialogFragment.ChangeTimeListener{
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
            startLocalTime, endLocalTime, data)
        onChangedTime(1, 0, 0)
        onChangedTime(2, 23, 59)

        recyclerView = contest_recycler_view

        recyclerView.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }

        main_div1.setOnCheckedChangeListener{
                compoundButton, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.DIV1)
        }

        main_div2.setOnCheckedChangeListener{
                compoundButton, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.DIV2)
        }

        main_div3.setOnCheckedChangeListener{
                compoundButton, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.DIV3)
        }

        main_other.setOnCheckedChangeListener{
                compoundButton, isChecked ->
            recyclerAdapter.changeDivFilter(isChecked, ContestType.Type.OTHER)
        }
    }

    override fun onResume() {
        super.onResume()

        LoaderManager.getInstance(this).initLoader(CONTEST_LOADER, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<ArrayList<Contest>> {
        var url = "https://codeforces.com/api/contest.list"
        return ContestLoader(this, url)
    }

    override fun onLoadFinished(loader: Loader<ArrayList<Contest>>, data: ArrayList<Contest>?) {
        recyclerAdapter.updateData(data)
    }

    override fun onLoaderReset(loader: Loader<ArrayList<Contest>>) {
        recyclerAdapter.updateData(null)
    }

    fun showTimePicker(view: View){
        val button = view as? Button ?: return

        var id = when (button.id){
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
