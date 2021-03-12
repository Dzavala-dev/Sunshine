package com.example.sunshine.sync

import android.app.IntentService
import android.content.Intent


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class SunshineSyncIntentService : IntentService("SunshineSyncIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        SunshineSyncTask.syncWeather(this)
    }
}