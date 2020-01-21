package com.example.codeforcealarmer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforcealarmer.datalayer.repo.ContestRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class MainActivityViewModel(val contestRepo: ContestRepo) : ViewModel() {
    init {
        viewModelScope.launch {
            contestRepo.updateCodingPhase()
            contestRepo.updateFinishedPhase()
        }
    }

    val isLoading: MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

    val isThereNetwork: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun load(){
        Log.v("LOADING_PB", "loading")
        viewModelScope.launch(Dispatchers.Main) {
            isLoading.value = true
            val loadResult = contestRepo.load()
            isLoading.value = false

            // handle no network
        }
    }


}