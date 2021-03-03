package com.example.hydration_reminder

import android.annotation.SuppressLint
import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hydration_reminder.sync.ReminderTasks
import com.example.hydration_reminder.sync.ReminderUtilities
import com.example.hydration_reminder.sync.WaterReminderIntentService
import com.example.hydration_reminder.utilities.PreferenceUtilities


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private var mWaterCountDisplay: TextView? = null
    private var mChargingCountDisplay: TextView? = null
    private var mChargingImageView: ImageView? = null
    private var mToast: Toast? = null
    var mChargingReceiver: ChargingBroadcastReceiver? = null
    var mChargingIntentFilter: IntentFilter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /** Get the views  */
        mWaterCountDisplay = findViewById(R.id.tv_water_count)
        mChargingCountDisplay = findViewById(R.id.tv_charging_reminder_count)
        mChargingImageView = findViewById(R.id.iv_power_increment)
        /** Set the original values in the UI  */
        updateWaterCount()
        updateChargingReminderCount()
        ReminderUtilities.scheduleChargingReminder(this)
        /** Setup the shared preference listener  */
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)

        /*
         * Setup and register the broadcast receiver
         */mChargingIntentFilter = IntentFilter()
        mChargingReceiver = ChargingBroadcastReceiver()
        mChargingIntentFilter!!.addAction(Intent.ACTION_POWER_CONNECTED)
        mChargingIntentFilter!!.addAction(Intent.ACTION_POWER_DISCONNECTED)
    }

    override fun onResume() {
        super.onResume()
        /** Determine the current charging state  */
        // COMPLETED (1) Check if you are on Android M or later, if so...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // COMPLETED (2) Get a BatteryManager instance using getSystemService()
            val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
            // COMPLETED (3) Call isCharging on the battery manager and pass the result on to your show
            // charging method
            showCharging(batteryManager.isCharging)
        } else {
            // COMPLETED (4) If your user is not on M+, then...

            // COMPLETED (5) Create a new intent filter with the action ACTION_BATTERY_CHANGED. This is a
            // sticky broadcast that contains a lot of information about the battery state.
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            // COMPLETED (6) Set a new Intent object equal to what is returned by registerReceiver, passing in null
            // for the receiver. Pass in your intent filter as well. Passing in null means that you're
            // getting the current state of a sticky broadcast - the intent returned will contain the
            // battery information you need.
            val currentBatteryStatusIntent = registerReceiver(null, ifilter)
            // COMPLETED (7) Get the integer extra BatteryManager.EXTRA_STATUS. Check if it matches
            // BatteryManager.BATTERY_STATUS_CHARGING or BatteryManager.BATTERY_STATUS_FULL. This means
            // the battery is currently charging.
            val batteryStatus =
                currentBatteryStatusIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging =
                batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                        batteryStatus == BatteryManager.BATTERY_STATUS_FULL
            // COMPLETED (8) Update the UI using your showCharging method
            showCharging(isCharging)
        }
        /** Register the receiver for future state changes  */
        registerReceiver(mChargingReceiver, mChargingIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mChargingReceiver)
    }

    /**
     * Updates the TextView to display the new water count from SharedPreferences
     */
    @SuppressLint("SetTextI18n")
    private fun updateWaterCount() {
        val waterCount = PreferenceUtilities.getWaterCount(this)
        mWaterCountDisplay!!.text = waterCount.toString() + ""
    }

    /**
     * Updates the TextView to display the new charging reminder count from SharedPreferences
     */
    private fun updateChargingReminderCount() {
        val chargingReminders = PreferenceUtilities.getChargingReminderCount(this)
        val formattedChargingReminders = resources.getQuantityString(
            R.plurals.charge_notification_count, chargingReminders, chargingReminders
        )
        mChargingCountDisplay!!.text = formattedChargingReminders
    }

    /**
     * Adds one to the water count and shows a toast
     */
    @SuppressLint("ShowToast")
    fun incrementWater(view: View?) {
        if (mToast != null) mToast!!.cancel()
        mToast = Toast.makeText(this, R.string.water_chug_toast, Toast.LENGTH_SHORT)

        val incrementWaterCountIntent = Intent(this, WaterReminderIntentService::class.java)
        incrementWaterCountIntent.action = ReminderTasks.ACTION_INCREMENT_WATER_COUNT
        startService(incrementWaterCountIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        /** Cleanup the shared preference listener  */
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(this)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (PreferenceUtilities.KEY_WATER_COUNT.equals(key)) {
            updateWaterCount()
        } else if (PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT.equals(key)) {
            updateChargingReminderCount()
        }
    }

    private fun showCharging(isCharging: Boolean) {
        if (isCharging) {
            mChargingImageView!!.setImageResource(R.drawable.ic_power_pink_80px)
        } else {
            mChargingImageView!!.setImageResource(R.drawable.ic_power_grey_80px)
        }
    }

    inner class ChargingBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            val isCharging = action == Intent.ACTION_POWER_CONNECTED
            showCharging(isCharging)
        }
    }
}