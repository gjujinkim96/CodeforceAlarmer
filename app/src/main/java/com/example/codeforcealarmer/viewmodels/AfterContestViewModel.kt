package com.example.codeforcealarmer.viewmodels

import androidx.lifecycle.ViewModel
import com.example.codeforcealarmer.datalayer.repo.ContestRepo

class AfterContestViewModel(private val contestRepo: ContestRepo) : ViewModel() {
    val contestsData = contestRepo.getAfterContests()
}