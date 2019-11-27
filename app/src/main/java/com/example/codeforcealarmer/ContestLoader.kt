package com.example.codeforcealarmer

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader

class ContestLoader(context: Context, val url : String) : AsyncTaskLoader<MutableList<Contest>>(context) {
    var cache : MutableList<Contest>? = null
    override fun onStartLoading() {
        if (cache == null)
            forceLoad()
        else
            deliverResult(cache)
    }

    override fun deliverResult(data: MutableList<Contest>?) {
        cache = data
        super.deliverResult(data)
    }

    override fun loadInBackground(): MutableList<Contest>? {
        val jsonString = HttpHandler.fetchFromUrl(url)
        if (jsonString == null){
            Log.e(this::class.java.simpleName, "failed to read jsonString")
            return null
        }else{
            val jsonResult =  JsonContestParser(jsonString)
            if (jsonResult.status == JsonContestParser.Status.OK) {
                return jsonResult.contests
            }else{
                return null
            }
        }
    }
}