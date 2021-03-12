package com.example.hydration_reminder.sync

import android.app.IntentService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class WaterReminderIntentService : IntentService("WaterReminderIntentService") {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        ReminderTasks.executeTask(this, action!!)
    }
}