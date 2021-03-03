import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.hydration_reminder.MainActivity
import com.example.hydration_reminder.R
import com.example.hydration_reminder.sync.ReminderTasks
import com.example.hydration_reminder.sync.WaterReminderIntentService

/**
 * Utility class for creating hydration notifications
 */
object NotificationUtils {
    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private const val WATER_REMINDER_NOTIFICATION_ID = 1138

    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private const val WATER_REMINDER_PENDING_INTENT_ID = 3417

    /**
     * This notification channel id is used to link notifications to this channel
     */
    private const val WATER_REMINDER_NOTIFICATION_CHANNEL_ID =
        "reminder_notification_channel"
    private const val ACTION_DRINK_PENDING_INTENT_ID = 1
    private const val ACTION_IGNORE_PENDING_INTENT_ID = 14
    fun clearAllNotifications(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    fun remindUserBecauseCharging(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                WATER_REMINDER_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.main_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationBuilder: Notification.Builder =
            Notification.Builder(context, WATER_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(
                    Notification.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)
                    )
                )
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true)
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(
            WATER_REMINDER_NOTIFICATION_ID,
            notificationBuilder.build()
        )
    }

    private fun ignoreReminderAction(context: Context): Notification.Action {
        val ignoreReminderIntent = Intent(context, WaterReminderIntentService::class.java)
        ignoreReminderIntent.action = ReminderTasks.ACTION_DISMISS_NOTIFICATION
        val ignoreReminderPendingIntent = PendingIntent.getService(
            context,
            ACTION_IGNORE_PENDING_INTENT_ID,
            ignoreReminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return Notification.Action(
            R.drawable.ic_cancel_black_24px,
            "No, thanks.",
            ignoreReminderPendingIntent
        )
    }

    private fun drinkWaterAction(context: Context): Notification.Action {
        val incrementWaterCountIntent =
            Intent(context, WaterReminderIntentService::class.java)
        incrementWaterCountIntent.action = ReminderTasks.ACTION_INCREMENT_WATER_COUNT
        val incrementWaterPendingIntent = PendingIntent.getService(
            context,
            ACTION_DRINK_PENDING_INTENT_ID,
            incrementWaterCountIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        return Notification.Action(
            R.drawable.ic_local_drink_black_24px,
            "I did it!",
            incrementWaterPendingIntent
        )
    }

    private fun contentIntent(context: Context): PendingIntent {
        val startActivityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            WATER_REMINDER_PENDING_INTENT_ID,
            startActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun largeIcon(context: Context): Bitmap {
        val res = context.resources
        return BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px)
    }
}