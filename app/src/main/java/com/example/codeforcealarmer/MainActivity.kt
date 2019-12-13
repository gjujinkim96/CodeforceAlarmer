package com.example.codeforcealarmer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var fragmentPagerAdapter: MyFragmentPagerAdapter
    lateinit var beforeContestFragment: BeforeContestFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        fragmentPagerAdapter = MyFragmentPagerAdapter(this, supportFragmentManager)
        viewpager.adapter = fragmentPagerAdapter
        tabLayout.setupWithViewPager(viewpager)

        fragmentPagerAdapter.apply{
            startUpdate(viewpager)
            beforeContestFragment = instantiateItem(viewpager, 0) as? BeforeContestFragment ?:
                    throw IllegalArgumentException("current fragment must be BeforeContestFragment")
            instantiateItem(viewpager, 1)
            finishUpdate(viewpager)
        }

        val viewModel: ContestViewModel by viewModels()

        viewModel.isRefreshing.observe(this, Observer {
            Log.v("VIEWMODEL_TEST", "observed isRefreshing")
            if (it == false)
                main_swipelayout.isRefreshing = false
        })

        main_swipelayout.setOnRefreshListener {
            viewModel.isRefreshing.value = true
            viewModel.loadData()
        }
    }
}
