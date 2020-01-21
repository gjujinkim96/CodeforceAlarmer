package com.example.codeforcealarmer.ui.activity_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codeforcealarmer.viewmodels.ContestRepoViewModelFactory
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.ui.adapters.ContestRecyclerAdapter
import com.example.codeforcealarmer.viewmodels.AfterContestViewModel
import kotlinx.android.synthetic.main.after_contest_fragment.*
import kotlinx.android.synthetic.main.after_contest_fragment.after_contest_recycler_view

class FinishedContestFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.after_contest_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerAdapter =
            ContestRecyclerAdapter(
                requireContext(),
                mutableListOf()
            )
        after_contest_recycler_view.apply{
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            emptyView = after_empty_group
            loadingView = after_loadingIcon
            emptyText = after_empty_text
            emptyIcon = after_empty_image
        }

        val viewModelFactory =
            ContestRepoViewModelFactory((activity?.application as MyApplication).appContainer.contestRepo)
        val viewModel: AfterContestViewModel by viewModels { viewModelFactory }
        viewModel.contestsData.observe(viewLifecycleOwner, Observer {
                recyclerAdapter.updateData(it)
            })

    }
}
