package com.example.hydration_reminder.sync

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hydration_reminder.utilities.PreferenceUtilities


object ReminderTasks {
    const val ACTION_INCREMENT_WATER_COUNT = "increment-water-count"
    const val ACTION_DISMISS_NOTIFICATION = "dismiss-notification"
    const val ACTION_CHARGING_REMINDER = "charging-reminder"
    @RequiresApi(Build.VERSION_CODES.O)
    fun executeTask(context: Context, action: String) {
        if (ACTION_INCREMENT_WATER_COUNT == action) {
            incrementWaterCount(context)
        } else if (ACTION_DISMISS_NOTIFICATION == action) {
            NotificationUtils.clearAllNotifications(context)
        } else if (ACTION_CHARGING_REMINDER == action) {
            issueChargingReminder(context)
        }
    }

    private fun incrementWaterCount(context: Context) {
        PreferenceUtilities.incrementWaterCount(context)
        NotificationUtils.clearAllNotifications(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun issueChargingReminder(context: Context) {
        PreferenceUtilities.incrementChargingReminderCount(context)
        NotificationUtils.remindUserBecauseCharging(context)
    }
}