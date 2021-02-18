package com.example.sunshine

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.android.sunshine.R


/**
 * SettingsActivity is responsible for displaying the [SettingsFragment]. It is also
 * responsible for orchestrating proper navigation when the up button is clicked. When the up
 * button is clicked from the SettingsActivity, we want to navigate to the Activity that the user
 * came from to get to the SettingsActivity.
 *
 *
 * For example, when the user is in the DetailActivity and clicks the settings option in the menu,
 * and then clicks the up button, we want to navigate back to the DetailActivity. If the user
 * navigates to the SettingsActivity from the MainActivity, then we want to navigate back to the
 * MainActivity when the user clicks the up button from the SettingsActivity.
 */
class SettingsActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_settings)
         this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}