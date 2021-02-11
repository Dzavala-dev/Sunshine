package com.example.sunshine

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceScreen
import android.provider.Settings.Global.getString
import data.SunshinePreferences
import data.WeatherContract


/**
 * The SettingsFragment serves as the display for all of the user's settings. In Sunshine, the
 * user will be able to change their preference for units of measurement from metric to imperial,
 * set their preferred weather location, and indicate whether or not they'd like to see
 * notifications.
 *
 * Please note: If you are using our dummy weather services, the location returned will always be
 * Mountain View, California.
 */
class SettingsFragment : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {
    private fun setPreferenceSummary(preference: Preference, value: Any?) {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            val listPreference: ListPreference = preference as ListPreference
            val prefIndex: Int = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries().get(prefIndex))
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue)
        }
    }

    fun onCreatePreferences(bundle: Bundle?, s: String?) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general)
        val sharedPreferences: SharedPreferences = getPreferenceScreen().getSharedPreferences()
        val prefScreen: PreferenceScreen = getPreferenceScreen()
        val count: Int = prefScreen.getPreferenceCount()
        for (i in 0 until count) {
            val p: Preference = prefScreen.getPreference(i)
            if (p !is CheckBoxPreference) {
                val value = sharedPreferences.getString(p.getKey(), "")
                setPreferenceSummary(p, value)
            }
        }
    }

    fun onStop() {
        super.onStop()
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this)
    }
@Override
    fun onStart() {
        super.onStart()
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        val activity: Activity = getActivity()
        if (key == getString(R.string.pref_location_key)) {
            // we've changed the location
            // Wipe out any potential PlacePicker latlng values so that we can use this text entry.
            SunshinePreferences.resetLocationCoordinates(activity)
        } else if (key == getString(R.string.pref_units_key)) {
            // units have changed. update lists of weather entries accordingly
            activity.contentResolver
                .notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null)
        }
        val preference: Preference = findPreference(key)
        if (null != preference) {
            if (preference !is CheckBoxPreference) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""))
            }
        }
    }
}
