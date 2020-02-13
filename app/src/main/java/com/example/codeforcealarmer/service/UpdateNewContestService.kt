package com.example.codeforcealarmer.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.LoadContestResult
import com.example.codeforcealarmer.datalayer.repo.ContestRepo
import com.example.codeforcealarmer.network.DownloadEstimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UpdateNewContestService : JobService() {
    companion object{
        const val JOB_ID = 12
    }

    private val contestRepo: ContestRepo by lazy {
        (application as MyApplication).appContainer.contestRepo
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.v("UPDATE_PERIODICALLY", "onStartJob called : ${System.currentTimeMillis()}")
        scope.launch {
            val loadResult = contestRepo.load()
            jobFinished(params, loadResult != LoadContestResult.OKAY)
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        scope.cancel()
        return true
    }
}

fun schedulePeriodicUpdate(context: Context){
    // period three days
    val builder = JobInfo.Builder(UpdateNewContestService.JOB_ID, ComponentName(context, UpdateNewContestService::class.java))
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
        .setPeriodic(3 * 24 * 60 * 60 * 1000)
        .setPersisted(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
        builder.setEstimatedNetworkBytes(DownloadEstimator.getEstimate(context), 0)
    }

    val jobInfo = builder.build()

    val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        if (jobScheduler.getPendingJob(jobInfo.id) == null)
            jobScheduler.schedule(jobInfo)
    }else{
        var isScheduled = false
        for (pendingJob in jobScheduler.allPendingJobs){
            if (pendingJob.id == jobInfo.id)
                isScheduled = true
        }

        if (!isScheduled)
            jobScheduler.schedule(jobInfo)
    }
}
