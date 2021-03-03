package com.example.sunshine.sync

import android.app.IntentService
import android.content.Intent

// COMPLETED (5) Create a new class called SunshineSyncIntentService that extends IntentService
/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class SunshineSyncIntentService  //  COMPLETED (6) Create a constructor that calls super and passes the name of this class
    : IntentService("SunshineSyncIntentService") {
    //  COMPLETED (7) Override onHandleIntent, and within it, call SunshineSyncTask.syncWeather
    override fun onHandleIntent(intent: Intent?) {
        SunshineSyncTask.syncWeather(this)
    }
}