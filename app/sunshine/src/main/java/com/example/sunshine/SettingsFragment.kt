package com.example.sunshine

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceScreen
import com.example.android.sunshine.R


/**
 * The SettingsFragment serves as the display for all of the user's settings. In Sunshine, the
 * user will be able to change their preference for units of measurement from metric to imperial,
 * set their preferred weather location, and indicate whether or not they'd like to see
 * notifications.
 *
 * Please note: If you are using our dummy weather services, the location returned will always be
 * Mountain View, California.
 */
// COMPLETED (4) Create SettingsFragment and extend PreferenceFragmentCompat
@Suppress("DEPRECATION")
class SettingsFragment : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {
    // COMPLETED (8) Create a method called setPreferenceSummary that accepts a Preference and an Object and sets the summary of the preference
    private fun setPreferenceSummary(preference: Preference, value: Any?) {
        val stringValue = value.toString()
        val key: String = preference.key
        if (preference is ListPreference) {
            /* For list preferences, look up the correct display value in */
            /* the preference's 'entries' list (since they have separate labels/values). */
            val listPreference: ListPreference = preference as ListPreference
            val prefIndex: Int = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.entries[prefIndex])
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.summary = stringValue
        }
    }

    // COMPLETED (5) Override onCreatePreferences and add the preference xml file using addPreferencesFromResource
    fun onCreatePreferences(bundle: Bundle?, s: String?) {
        /* Add 'general' preferences, defined in the XML file */
        addPreferencesFromResource(R.xml.pref_general)

        // COMPLETED (9) Set the preference summary on each preference that isn't a CheckBoxPreference
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

    // COMPLETED (13) Unregister SettingsFragment (this) as a SharedPreferenceChangedListener in onStop
    fun onStop() {
        super.onStop()
        /* Unregister the preference change listener */getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    // COMPLETED (12) Register SettingsFragment (this) as a SharedPreferenceChangedListener in onStart
    fun onStart() {
        super.onStart()
        /* Register the preference change listener */getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this)
    }

    // COMPLETED (11) Override onSharedPreferenceChanged to update non CheckBoxPreferences when they are changed
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        val preference: Preference = findPreference(key)
        if (preference !is CheckBoxPreference) {
            setPreferenceSummary(preference, sharedPreferences.getString(key, ""))
        }
    }
}