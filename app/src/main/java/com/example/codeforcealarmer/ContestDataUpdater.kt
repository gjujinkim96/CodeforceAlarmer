package com.example.codeforcealarmer

interface ContestDataUpdater {
    fun onLoadingStart()
    fun onLoadingEnd()
    fun update(newData: MutableList<Contest>)
}