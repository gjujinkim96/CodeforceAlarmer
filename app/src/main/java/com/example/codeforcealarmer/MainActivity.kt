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
import org.threeten.bp.Instant

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<Contest>> {
    val CONTEST_LOADER = 1

    lateinit var recyclerAdapter: ContestRecyclerAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        val data = arrayListOf<Contest>()
        recyclerAdapter = ContestRecyclerAdapter(this, data)

        recyclerView = findViewById(R.id.contest_recycler_view)

        recyclerView.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }

        val startTime: Long = 1576328700
        val ins = Instant.ofEpochSecond(startTime)
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = startTime.toLong()
        Toast.makeText(this, ins.toString(), Toast.LENGTH_LONG).show()
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
