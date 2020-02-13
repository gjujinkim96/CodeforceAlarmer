package com.example.codeforcealarmer.network

import android.content.Context
import android.preference.PreferenceManager

class DownloadEstimator {
    companion object{
        const val DOWNLOAD_ESTIMATE_KEY = "download_estimate"

        fun getEstimate(context: Context): Long =
            PreferenceManager.getDefaultSharedPreferences(context).getLong(DOWNLOAD_ESTIMATE_KEY, 234000)
        fun setEstimate(context: Context, newEstimate: Long) =
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(DOWNLOAD_ESTIMATE_KEY, newEstimate).commit()
    }
}