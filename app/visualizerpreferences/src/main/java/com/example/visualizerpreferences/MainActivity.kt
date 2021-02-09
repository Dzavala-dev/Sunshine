package com.example.visualizerpreferences

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
import audioVisuals.AudioInputReader
import audioVisuals.VisualizerView


@SuppressLint("Registered")
@Suppress("DEPRECATED_IDENTITY_EQUALS")
open class MainActivity : AppCompatActivity() {
    private var mVisualizerView: VisualizerView? = null
    private var mAudioInputReader: AudioInputReader? = null
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizer)
        mVisualizerView = findViewById(R.id.activity_visualizer)
        defaultSetup()
         setupPermissions()
    }

    private fun defaultSetup() {
        mVisualizerView?.setShowBass(true)
        mVisualizerView?.setShowMid(true)
        mVisualizerView?.setShowTreble(true)
        mVisualizerView?.setMinSizeScale(1)
        mVisualizerView?.setColor(getString(R.string.pref_color_red_value))
    }

    /**
     * Methods for setting up the menu
     */
    // COMPLETED (5) Add the menu to the menu bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        val inflater: MenuInflater = menuInflater
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */inflater.inflate(
            R.menu.visualizer_menu,
            menu
        )
        /* Return true so that the visualizer_menu is displayed in the Toolbar */return true
    }

    // COMPLETED (6) When the "Settings" menu item is pressed, open com.example.visualizerpreferences.SettingsActivity
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
                    permissionsWeNeed,
                    MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE
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
