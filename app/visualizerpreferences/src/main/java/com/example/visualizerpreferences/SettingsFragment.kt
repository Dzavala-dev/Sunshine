package com.example.visualizerpreferences

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.example.com.visualizerpreferences.R
import android.os.Bundle
import androidx.preference.*


// COMPLETED (1) Implement OnSharedPreferenceChangeListener
class SettingsFragment : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(bundle: Bundle?, s: String?) {

        // Add visualizer preferences, defined in the XML file in res->xml->pref_visualizer
        addPreferencesFromResource(R.xml.pref_visualizer)

        // COMPLETED (3) Get the preference screen, get the number of preferences and iterate through
        // all of the preferences if it is not a checkbox preference, call the setSummary method
        // passing in a preference and the value of the preference
        val sharedPreferences: SharedPreferences = preferenceScreen.sharedPreferences
        val prefScreen: PreferenceScreen = preferenceScreen
        val count: Int = prefScreen.preferenceCount

        // Go through all of the preferences, and set up their preference summary.
        for (i in 0 until count) {
            val p: Preference = prefScreen.getPreference(i)
            // You don't need to set up preference summaries for checkbox preferences because
            // they are already set up in xml using summaryOff and summary On
            if (p !is CheckBoxPreference) {
                val value = sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value)
            }
        }
    }

    // COMPLETED (4) Override onSharedPreferenceChanged and, if it is not a checkbox preference,
    // call setPreferenceSummary on the changed preference
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        // Figure out which preference was changed
        val preference: Preference = findPreference(key)
        if (preference !is CheckBoxPreference) {
            val value = sharedPreferences.getString(preference.key, "")
            setPreferenceSummary(preference, value)
        }
    }
    // COMPLETED (2) Create a setPreferenceSummary which takes a Preference and String value as parameters.
    // This method should check if the preference is a ListPreference and, if so, find the label
    // associated with the value. You can do this by using the findIndexOfValue and getEntries methods
    // of Preference.
    /**
     * Updates the summary for the preference
     *
     * @param preference The preference to be updated
     * @param value      The value that the preference was updated to
     */
    private fun setPreferenceSummary(preference: Preference, value: String?) {
        if (preference is ListPreference) {
            // For list preferences, figure out the label of the selected value
            val listPreference: ListPreference = preference
            val prefIndex: Int = listPreference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.summary = listPreference.entries[prefIndex]
            }
        }
    }

    // COMPLETED (5) Register and unregister the OnSharedPreferenceChange listener (this class) in
    // onCreate and onDestroy respectively.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}
