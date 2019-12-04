package com.example.codeforcealarmer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.after_contest_fragment.*
import kotlinx.android.synthetic.main.after_contest_fragment.contest_recycler_view
import org.threeten.bp.LocalTime

class FinishedContestFragment : Fragment(), ContestRecyclerAdapter.EmptyStateListener, ContestDataUpdater {
    lateinit var recyclerAdapter: ContestRecyclerAdapter
    private var isLoading = false
    private var hasCreatedView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = mutableListOf<Contest>()

        val startLocalTime = LocalTime.of(0, 0)
        val endLocalTime = LocalTime.of(23, 59)

        recyclerAdapter = ContestRecyclerAdapter(requireContext(), ContestType(true, true, true, true),
            startLocalTime, endLocalTime, Sorting.LATEST, data)
        recyclerAdapter.emptyStateListener = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.after_contest_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hasCreatedView = true
        if (isLoading){
            showLoading()
        }else{
            endLoading()
        }

        contest_recycler_view.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    override fun onEmptyStateEnter() {
        if (!isLoading) {
            empty_group.visibility = View.VISIBLE
            contest_recycler_view.visibility = View.GONE
        }
    }

    override fun onEmptyStateExit() {
        if (!isLoading) {
            empty_group.visibility = View.GONE
            contest_recycler_view.visibility = View.VISIBLE
        }
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
        empty_group.visibility = View.GONE
        contest_recycler_view.visibility = View.GONE
    }

    private fun endLoading() {
        loading.visibility = View.GONE
    }

    override fun onLoadingStart() {
        isLoading = true
        if (hasCreatedView)
            showLoading()
    }

    override fun onLoadingEnd() {
        isLoading = false
        if (hasCreatedView)
            endLoading()
    }

    override fun update(newData: MutableList<Contest>) {
        recyclerAdapter.updateData(newData)
    }

}
