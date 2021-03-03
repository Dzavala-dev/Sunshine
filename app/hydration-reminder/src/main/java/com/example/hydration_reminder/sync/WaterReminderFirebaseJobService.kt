package com.example.hydration_reminder.sync

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.firebase.jobdispatcher.Job
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.firebase.jobdispatcher.RetryStrategy

class WaterReminderFirebaseJobService : JobService() {
    private var mBackgroundTask: AsyncTask<*, *, *>? = null

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
        mBackgroundTask = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Any?, Any?, Any?>() {
            override fun doInBackground(params: Array<Any?>): Any? {
                val context: Context = this@WaterReminderFirebaseJobService
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_CHARGING_REMINDER)
                return null
            }

            override fun onPostExecute(o: Any?) {
                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParamters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */
                jobFinished(jobParameters, false)
            }
        }
        (mBackgroundTask as AsyncTask<*, *, *>).execute()
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
        if (mBackgroundTask != null) mBackgroundTask!!.cancel(true)
        return true
    }
}