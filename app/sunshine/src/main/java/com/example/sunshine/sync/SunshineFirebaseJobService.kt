package com.example.sunshine.sync

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService


class SunshineFirebaseJobService : JobService() {
    private var mFetchWeatherTask: AsyncTask<Void?, Void?, Void?>? =
        null

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
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        mFetchWeatherTask =
            @SuppressLint("StaticFieldLeak")
            object : AsyncTask<Void?, Void?, Void?>() {
                protected override fun doInBackground(vararg p0: Void?): Void? {
                    val context = applicationContext
                    SunshineSyncTask.syncWeather(context)
                    jobFinished(jobParameters, false)
                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    jobFinished(jobParameters, false)
                }
            }
        (mFetchWeatherTask as AsyncTask<Void?, Void?, Void?>).execute()
        return true
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder.setRetryStrategy
     * @see RetryStrategy
     */
    override fun onStopJob(jobParameters: JobParameters): Boolean {
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask!!.cancel(true)
        }
        return true
    }
}