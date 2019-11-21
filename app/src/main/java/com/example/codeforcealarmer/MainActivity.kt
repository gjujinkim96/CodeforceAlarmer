package com.example.codeforcealarmer

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<Contest>> {
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
        recyclerAdapter = ContestRecyclerAdapter(this, EnumSet.range(Phase.BEFORE, Phase.CODING),
            ContestType(true, true),data)

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
}
