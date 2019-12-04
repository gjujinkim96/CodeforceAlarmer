package com.example.codeforcealarmer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Pair<MutableList<Contest>, MutableList<Contest>>> ,
    TimePickerDialogFragment.ChangeTimeListener{

    companion object {
        const val CONTEST_LOADER = 1
    }

    lateinit var hasUpdatedFragment: MutableList<Boolean>
    lateinit var fragmentPagerAdapter: MyFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        fragmentPagerAdapter = MyFragmentPagerAdapter(this, supportFragmentManager)
        fragmentPagerAdapter.count
        viewpager.adapter = fragmentPagerAdapter
        tabLayout.setupWithViewPager(viewpager)

        hasUpdatedFragment = MutableList(fragmentPagerAdapter.count) { false }
    }

    override fun onResume() {
        super.onResume()
        Log.v("ORDER", "MainActivity:onResume")
        LoaderManager.getInstance(this).initLoader(CONTEST_LOADER, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Pair<MutableList<Contest>, MutableList<Contest>>> {
        fragmentPagerAdapter.startLoading()
        val url = "https://codeforces.com/api/contest.list"
        return ContestLoader(this, url)
    }

    override fun onLoadFinished(loader: Loader<Pair<MutableList<Contest>, MutableList<Contest>>>, data: Pair<MutableList<Contest>, MutableList<Contest>>?) {
        fragmentPagerAdapter.setUpdateBuffer(data?.first, data?.second)
        fragmentPagerAdapter.finishedLoading()
    }

    override fun onLoaderReset(loader: Loader<Pair<MutableList<Contest>, MutableList<Contest>>>) {
        fragmentPagerAdapter.setUpdateBuffer(mutableListOf(), mutableListOf())
    }

    override fun onChangedTime(clickedId: Int, hourOfDay: Int, minute: Int) {
        val beforeContestFragment = fragmentPagerAdapter.currentFragment as? BeforeContestFragment ?:
            throw IllegalArgumentException("current fragment must be BeforeContestFragment")

        beforeContestFragment.changeTime(clickedId, hourOfDay, minute)
    }
}
