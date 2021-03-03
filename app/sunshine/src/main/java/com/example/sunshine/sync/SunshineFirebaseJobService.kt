package com.example.sunshine.sync

import android.content.Context
import android.os.AsyncTask


// COMPLETED (2) Make sure you've imported the jobdispatcher.JobService, not job.JobService

// COMPLETED (2) Make sure you've imported the jobdispatcher.JobService, not job.JobService
// COMPLETED (3) Add a class called SunshineFirebaseJobService that extends jobdispatcher.JobService
class SunshineFirebaseJobService : com.firebase.jobdispatcher.JobService() {
    //  COMPLETED (4) Declare an ASyncTask field called mFetchWeatherTask
    private var mFetchWeatherTask: AsyncTask<Void?, Void?, Void?>? =
        null
    //  COMPLETED (5) Override onStartJob and within it, spawn off a separate ASyncTask to sync weather data
    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    override fun onStartJob(jobParameters: com.firebase.jobdispatcher.JobParameters): Boolean {
        mFetchWeatherTask =
            object : AsyncTask<Void?, Void?, Void?>() {
                protected override fun doInBackground(vararg voids: Void): Void? {
                    val context: Context = getApplicationContext()
                    SunshineSyncTask.syncWeather(context)
                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    //  COMPLETED (6) Once the weather data is sync'd, call jobFinished with the appropriate arguements
                    jobFinished(jobParameters, false)
                }
            }
        mFetchWeatherTask.execute()
        return true
    }
    //  COMPLETED (7) Override onStopJob, cancel the ASyncTask if it's not null and return true
    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder.setRetryStrategy
     * @see RetryStrategy
     */
    override fun onStopJob(jobParameters: com.firebase.jobdispatcher.JobParameters): Boolean {
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask!!.cancel(true)
        }
        return true
    }
}