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

    private var isLoading = true
    private val updateBuffer: Array<MutableList<Contest>?> = arrayOf(null, null)

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

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
        Log.v("ORDER", "MyFragmentPagerAdapter:setPrimaryItem pos:$position")
        if (currentFragment != obj){
            currentFragment = obj as? Fragment
        }

        val updater = currentFragment as? ContestDataUpdater ?: throw ClassCastException("must implement ContestDataUpdater")

        if (isLoading)
            updater.onLoadingStart()
        else
            updater.onLoadingEnd()

        val updateData = updateBuffer[position]
        if (updateData!= null){
            updater.update(updateData)
            updateBuffer[position] = null
        }

        super.setPrimaryItem(container, position, obj)
    }

    override fun finishUpdate(container: ViewGroup) {

        Log.v("ORDER", "MyFragmentPagerAdapter:finishUpdate")
        super.finishUpdate(container)
    }

    fun setUpdateBuffer(beforeData: MutableList<Contest>?, afterData: MutableList<Contest>?){
        updateBuffer[0] = beforeData
        updateBuffer[1] = afterData
    }

    fun startLoading(){
        isLoading = true
        if (currentFragment != null) {
            val updater = currentFragment as? ContestDataUpdater
                ?: throw ClassCastException("must implement ContestDataUpdater")
            updater.onLoadingStart()
        }
    }

    fun finishedLoading(){
        isLoading = false
        if (currentFragment != null) {
            val updater = currentFragment as? ContestDataUpdater
                ?: throw ClassCastException("must implement ContestDataUpdater")
            updater.onLoadingEnd()
        }
    }
}