package com.example.visualizerpreferences

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.example.com.visualizerpreferences.R
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import audioVisuals.AudioInputReader
import audioVisuals.VisualizerView


class VisualizerActivity : AppCompatActivity(),
    OnSharedPreferenceChangeListener {
    private var mVisualizerView: VisualizerView? = null
    private var mAudioInputReader: AudioInputReader? = null
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizer)
        mVisualizerView = findViewById(R.id.activity_visualizer)
        setupSharedPreferences()
        setupPermissions()
    }
    private fun setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        // COMPLETED (4) Use resources here instead of the hard coded string and boolean
        mVisualizerView!!.setShowBass(
            sharedPreferences.getBoolean(
                getString(R.string.pref_show_bass_key),
                resources.getBoolean(R.bool.pref_show_bass_default)
            )
        )
        mVisualizerView!!.setShowMid(
            sharedPreferences.getBoolean(
                getString(R.string.pref_show_mid_range_key),
                resources.getBoolean(R.bool.pref_show_mid_range_default)
            )
        )
        mVisualizerView!!.setShowTreble(sharedPreferences.getBoolean(
            getString(R.string.pref_show_treble_key),
            resources.getBoolean(R.bool.pref_show_treble_default)
            )
        )
        mVisualizerView!!.setMinSizeScale(1)
        loadColorFromPreferences(sharedPreferences)

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun loadColorFromPreferences(sharedPreferences: SharedPreferences) {
        mVisualizerView!!.setColor(
            sharedPreferences.getString(
                getString(R.string.pref_color_key),
                getString(R.string.pref_color_red_value)
            )!!
        )
    }

    /**
     * Methods for setting up the menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        val inflater: MenuInflater = menuInflater
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */inflater.inflate(
            R.menu.visualizer_menu,
            menu
        )
        /* Return true so that the visualizer_menu is displayed in the Toolbar */return true
    }

    // COMPLETED (2) Override the onSharedPreferenceChanged method and update the show bass preference
    // Updates the screen if the shared preferences change. This method is required when you make a
    // class implement OnSharedPreferenceChangedListener
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        when (key) {
            getString(R.string.pref_show_bass_key) -> {
                mVisualizerView!!.setShowBass(
                    sharedPreferences.getBoolean(
                        key,
                        resources.getBoolean(R.bool.pref_show_bass_default)
                    )
                )
            }
            getString(R.string.pref_show_mid_range_key) -> {
                mVisualizerView!!.setShowMid(
                    sharedPreferences.getBoolean(
                        key,
                        resources.getBoolean(R.bool.pref_show_mid_range_default)
                    )
                )
            }
            getString(R.string.pref_show_treble_key) -> {
                mVisualizerView!!.setShowTreble(
                    sharedPreferences.getBoolean(
                        key,
                        resources.getBoolean(R.bool.pref_show_treble_default)
                    )
                )
            }
            getString(R.string.pref_color_key) -> {
                loadColorFromPreferences(sharedPreferences)
            }
        }
    }

    // COMPLETED (4) Override onDestroy and unregister the listener
    override fun onDestroy() {
        super.onDestroy()
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val startSettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(startSettingsActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     */
    /**
     * onPause Cleanup audio stream
     */
     override fun onPause() {
        super.onPause()
        mAudioInputReader?.shutdown(isFinishing)
    }

     override fun onResume() {
        super.onResume()
         mAudioInputReader?.restart()
    }

    /**
     * App Permissions for Audio
     */
    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                val permissionsWeNeed =
                    arrayOf(Manifest.permission.RECORD_AUDIO)
                requestPermissions(
                    permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE
                )
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = mVisualizerView?.let { AudioInputReader(it, this) }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = mVisualizerView?.let { AudioInputReader(it, this) }
                } else {
                    Toast.makeText(
                        this,
                        "Permission for audio not granted. Visualizer can't run.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
        }
    }

    companion object {
        private const val MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88
    }
}