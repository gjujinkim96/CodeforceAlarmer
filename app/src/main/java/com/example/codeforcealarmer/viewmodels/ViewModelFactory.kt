package com.example.codeforcealarmer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.codeforcealarmer.datalayer.repo.AlarmOffsetRepo
import com.example.codeforcealarmer.datalayer.repo.ContestFilterRepo
import com.example.codeforcealarmer.datalayer.repo.ContestRepo
import com.example.codeforcealarmer.datalayer.repo.ContestWithAlarmRepo

class ContestRepoViewModelFactory(private val arg: ContestRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ContestRepo::class.java).newInstance(arg)
    }
}

class ContestRepoAndFilterViewModelFactory(private val arg1: ContestRepo, private val arg2: ContestFilterRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ContestRepo::class.java, ContestFilterRepo::class.java).newInstance(arg1, arg2)
    }
}

class BeforeViewModelFactory(
    private val arg1: ContestWithAlarmRepo,
    private val arg2: ContestFilterRepo,
    private val arg3: AlarmOffsetRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            ContestWithAlarmRepo::class.java,
            ContestFilterRepo::class.java,
            AlarmOffsetRepo::class.java).newInstance(arg1, arg2, arg3)
    }
}