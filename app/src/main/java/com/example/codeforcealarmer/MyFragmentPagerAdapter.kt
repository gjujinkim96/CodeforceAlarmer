package com.example.codeforcealarmer

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyFragmentPagerAdapter(private val context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object{
        const val MAX_FRAG = 2
    }
    var currentFragment: Fragment? = null
        private set

    override fun getItem(position: Int): Fragment{
        Log.v("ORDER", "MyFragmentPagerAdapter:getItem pos:$position")
        return when (position){
            0 -> BeforeContestFragment()
            1 -> FinishedContestFragment()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getCount(): Int = MAX_FRAG

    override fun getPageTitle(position: Int): CharSequence? = when(position){
        0 -> context.getString(R.string.tab_before)
        1 -> context.getString(R.string.tab_finished)
        else -> throw IllegalArgumentException()
    }
}