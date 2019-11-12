package com.example.codeforcealarmer

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader

class ContestLoader(context: Context, val url : String) : AsyncTaskLoader<ArrayList<Contest>>(context) {
    var cache : ArrayList<Contest>? = null
    override fun onStartLoading() {
        if (cache == null)
            forceLoad()
        else
            deliverResult(cache)
    }

    override fun deliverResult(data: ArrayList<Contest>?) {
        cache = data
        super.deliverResult(data)
    }

    override fun loadInBackground(): ArrayList<Contest>? {
        val jsonString = HttpHandler.fetchFromUrl(url)
        if (jsonString == null){
            Log.e(this::class.java.simpleName, "failed to read jsonString")
            return null
        }else{
            val jsonResult =  JSONContestParser(jsonString)
            if (jsonResult.status == JSONContestParser.STATUS.OK) {
                return jsonResult.contests
            }else{
                return null
            }
        }
    }
}