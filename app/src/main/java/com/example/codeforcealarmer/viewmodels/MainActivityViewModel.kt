package com.example.codeforcealarmer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforcealarmer.datalayer.repo.ContestRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.Toast
import com.example.codeforcealarmer.datalayer.dataholder.LoadContestResult

class MainActivityViewModel(val contestRepo: ContestRepo) : ViewModel() {
    init {
        viewModelScope.launch {
            val updatedCodingPhase = contestRepo.updateCodingPhase()
            val updatedFinishedPhase = contestRepo.updateFinishedPhase()
            //Log.v("CALLED_THREE", "coding: $updatedCodingPhase,  finished: $updatedFinishedPhase")
        }
    }

    val isLoading: MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

    val loadingState: MutableLiveData<LoadContestResult> by lazy {
        MutableLiveData<LoadContestResult>()
    }

    fun load(){
        Log.v("LOADING_PB", "loading")
        viewModelScope.launch(Dispatchers.Main) {
            isLoading.value = true
            val loadResult = contestRepo.load()
            isLoading.value = false

            // handle no network
            loadingState.value = loadResult
        }
    }


}