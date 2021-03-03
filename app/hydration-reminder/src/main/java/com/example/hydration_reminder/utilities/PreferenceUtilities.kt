package com.example.hydration_reminder.utilities

import android.content.Context
import android.preference.PreferenceManager


/**
 * This class contains utility methods which update water and charging counts in SharedPreferences
 */
object PreferenceUtilities {
    const val KEY_WATER_COUNT = "water-count"
    const val KEY_CHARGING_REMINDER_COUNT = "charging-reminder-count"
    private const val DEFAULT_COUNT = 0

    @Synchronized
    private fun setWaterCount(context: Context?, glassesOfWater: Int) {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putInt(KEY_WATER_COUNT, glassesOfWater)
        editor.apply()
    }

    fun getWaterCount(context: Context?): Int {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(KEY_WATER_COUNT, DEFAULT_COUNT)
    }

    @Synchronized
    fun incrementWaterCount(context: Context?) {
        var waterCount = getWaterCount(context)
        setWaterCount(context, ++waterCount)
    }

    @Synchronized
    fun incrementChargingReminderCount(context: Context?) {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        var chargingReminders = prefs.getInt(
            KEY_CHARGING_REMINDER_COUNT,
            DEFAULT_COUNT
        )
        val editor = prefs.edit()
        editor.putInt(KEY_CHARGING_REMINDER_COUNT, ++chargingReminders)
        editor.apply()
    }

    fun getChargingReminderCount(context: Context?): Int {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(
            KEY_CHARGING_REMINDER_COUNT,
            DEFAULT_COUNT
        )
    }
}