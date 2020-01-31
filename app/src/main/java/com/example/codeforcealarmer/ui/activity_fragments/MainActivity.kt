package com.example.codeforcealarmer.ui.activity_fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.codeforcealarmer.viewmodels.ContestRepoViewModelFactory
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.ui.adapters.MyFragmentPagerAdapter
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.datalayer.dataholder.LoadContestResult
import com.example.codeforcealarmer.viewmodels.MainActivityViewModel
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var fragmentPagerAdapter: MyFragmentPagerAdapter
    lateinit var beforeContestFragment: BeforeContestFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        fragmentPagerAdapter =
            MyFragmentPagerAdapter(
                this,
                supportFragmentManager
            )
        viewpager.adapter = fragmentPagerAdapter
        tabLayout.setupWithViewPager(viewpager)

        fragmentPagerAdapter.apply{
            startUpdate(viewpager)
            beforeContestFragment = instantiateItem(viewpager, 0) as? BeforeContestFragment
                ?:
                    throw IllegalArgumentException("current fragment must be BeforeContestFragment")
            instantiateItem(viewpager, 1)
            finishUpdate(viewpager)
        }

        val viewModelFactory: ViewModelProvider.Factory =
            ContestRepoViewModelFactory((application as MyApplication).appContainer.contestRepo)
        val viewModel: MainActivityViewModel by viewModels { viewModelFactory }

        viewModel.isLoading.observe(this, Observer {
          if (it == false)
              main_swipelayout.isRefreshing = false
        })

        viewModel.loadingState.observe(this, Observer {
            when (it){
                LoadContestResult.NETWORK_ERROR -> Snackbar.make(main_swipelayout, R.string.snackbar_no_internet_text, Snackbar.LENGTH_LONG).show()
                LoadContestResult.OTHER_ERROR -> Snackbar.make(main_swipelayout, R.string.snackbar_other_error, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.load()

        main_swipelayout.setOnRefreshListener {
            viewModel.isLoading.value = true
            viewModel.load()
        }
    }
}
