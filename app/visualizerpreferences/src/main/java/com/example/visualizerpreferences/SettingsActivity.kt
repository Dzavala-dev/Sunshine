package com.example.visualizerpreferences

import android.annotation.SuppressLint
import android.example.com.visualizerpreferences.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils


@SuppressLint("Registered")
class SettingsActivity : AppCompatActivity() {
    // COMPLETED (1) Create a new Empty Activity named com.example.visualizerpreferences.SettingsActivity; make sure to generate the
    // activity_settings.xml layout file as well and add the activity to the manifest
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set the action bar back button to look like an up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
        }
        return super.onOptionsItemSelected(item)
    }
}