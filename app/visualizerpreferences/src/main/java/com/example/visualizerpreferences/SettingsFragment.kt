package com.example.visualizerpreferences

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.example.com.visualizerpreferences.R
import android.os.Bundle
import androidx.preference.*


class SettingsFragment : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(bundle: Bundle?, s: String?) {

        // Add visualizer preferences, defined in the XML file in res->xml->pref_visualizer
        addPreferencesFromResource(R.xml.pref_visualizer)
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
                p.setPreferenceSummary(value)
            }
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        // Figure out which preference was changed
        val preference: Preference = findPreference(key)
        if (preference !is CheckBoxPreference) {
            val value = sharedPreferences.getString(preference.key, "")
            preference.setPreferenceSummary(value)
        }
    }

    /**
     * Updates the summary for the preference
     *
     * @param this@setPreferenceSummary The preference to be updated
     * @param value      The value that the preference was updated to
     */
    private fun Preference.setPreferenceSummary(value: String?) {
        // COMPLETED (3) Don't forget to add code here to properly set the summary for an EditTextPreference
        if (this is ListPreference) {
            // For list preferences, figure out the label of the selected value
            val listPreference: ListPreference = this
            val prefIndex: Int = listPreference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.summary = listPreference.entries[prefIndex]
            }
        } else if (this is EditTextPreference) {
            // For EditTextPreferences, set the summary to the value's simple string representation.
            setSummary(value)
        }
    }

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
