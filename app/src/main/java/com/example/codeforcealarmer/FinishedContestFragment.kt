package com.example.codeforcealarmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.after_contest_fragment.*
import kotlinx.android.synthetic.main.after_contest_fragment.after_contest_recycler_view

class FinishedContestFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.after_contest_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerAdapter = ContestRecyclerAdapter(requireContext(), Sorting.LATEST)
        after_contest_recycler_view.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            emptyView = after_empty_group
            loadingView = after_loadingIcon
            emptyText = after_empty_text
            emptyIcon = after_empty_image
        }


        val viewModel: ContestViewModel by activityViewModels()
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            after_contest_recycler_view.loading = it
        })

        viewModel.afterContests.observe(viewLifecycleOwner, Observer {
            recyclerAdapter.updateData(it)
        })

        viewModel.isInternetConnection.observe(viewLifecycleOwner, Observer {
            after_contest_recycler_view.isInternetConnection = it
        })
    }
}
