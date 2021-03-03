package com.example.hydration_reminder.sync

import android.app.IntentService
import android.content.Intent


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class WaterReminderIntentService : IntentService("WaterReminderIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        ReminderTasks.executeTask(this, action!!)
    }
}