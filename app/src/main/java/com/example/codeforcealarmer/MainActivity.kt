package com.example.codeforcealarmer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.before_contest_fragment.*
import java.util.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Pair<MutableList<Contest>, MutableList<Contest>>> ,
    TimePickerDialogFragment.ChangeTimeListener{

    companion object {
        const val CONTEST_LOADER = 1
    }

    lateinit var fragmentPagerAdapter: MyFragmentPagerAdapter

    lateinit var beforeUpdater: ContestDataUpdater
    lateinit var afterUpdater: ContestDataUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        fragmentPagerAdapter = MyFragmentPagerAdapter(this, supportFragmentManager)
        viewpager.adapter = fragmentPagerAdapter
        tabLayout.setupWithViewPager(viewpager)

        fragmentPagerAdapter.apply{
            startUpdate(viewpager)
            beforeUpdater = instantiateItem(viewpager, 0) as? ContestDataUpdater ?: throw ClassCastException()
            afterUpdater = instantiateItem(viewpager, 1) as? ContestDataUpdater ?: throw ClassCastException()
            finishUpdate(viewpager)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v("ORDER", "MainActivity:onResume")
        LoaderManager.getInstance(this).initLoader(CONTEST_LOADER, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Pair<MutableList<Contest>, MutableList<Contest>>> {
        beforeUpdater.onLoadingStart()
        afterUpdater.onLoadingStart()

        val url = "https://codeforces.com/api/contest.list"
        Log.v("Attach", "onCreateLoader")
        return ContestLoader(this, url)
    }

    override fun onLoadFinished(loader: Loader<Pair<MutableList<Contest>, MutableList<Contest>>>, data: Pair<MutableList<Contest>, MutableList<Contest>>?) {
        beforeUpdater.onLoadingEnd()
        if (data?.first != null)
            beforeUpdater.update(data.first)

        afterUpdater.onLoadingEnd()
        if (data?.second != null)
            afterUpdater.update(data.second)
    }

    override fun onLoaderReset(loader: Loader<Pair<MutableList<Contest>, MutableList<Contest>>>) {
        beforeUpdater.update(mutableListOf())
        afterUpdater.update(mutableListOf())
    }

    override fun onChangedTime(clickedId: Int, hourOfDay: Int, minute: Int) {
        val beforeContestFragment = fragmentPagerAdapter.currentFragment as? BeforeContestFragment ?:
            throw IllegalArgumentException("current fragment must be BeforeContestFragment")

        beforeContestFragment.changeTime(clickedId, hourOfDay, minute)
    }
}
