package data

import android.content.Context
import android.preference.PreferenceManager
import com.example.sunshine.R


object SunshinePreferences {
    /*
     * In order to uniquely pinpoint the location on the map when we launch the map intent, we
     * store the latitude and longitude. We will also use the latitude and longitude to create
     * queries for the weather.
     */
    private const val PREF_COORD_LAT = "coord_lat"
    private const val PREF_COORD_LONG = "coord_long"

    /**
     * Helper method to handle setting location details in Preferences (city name, latitude,
     * longitude)
     *
     *
     * When the location details are updated, the database should to be cleared.
     *
     * @param context  Context used to get the SharedPreferences
     * @param lat      the latitude of the city
     * @param lon      the longitude of the city
     */
    fun setLocationDetails(
        context: Context?,
        lat: Double,
        lon: Double
    ) {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.putLong(
            PREF_COORD_LAT,
            java.lang.Double.doubleToRawLongBits(lat)
        )
        editor.putLong(
            PREF_COORD_LONG,
            java.lang.Double.doubleToRawLongBits(lon)
        )
        editor.apply()
    }

    /**
     * Resets the location coordinates stores in SharedPreferences.
     *
     * @param context Context used to get the SharedPreferences
     */
    fun resetLocationCoordinates(context: Context?) {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.remove(PREF_COORD_LAT)
        editor.remove(PREF_COORD_LONG)
        editor.apply()
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "94043,USA", which is Mountain View, California. Mountain View is the
     * home of the headquarters of the Googleplex!
     *
     * @param context Context used to access SharedPreferences
     * @return Location The current user has set in SharedPreferences. Will default to
     * "94043,USA" if SharedPreferences have not been implemented yet.
     */
    fun getPreferredWeatherLocation(context: Context): String? {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val keyForLocation = context.getString(R.string.pref_location_key)
        val defaultLocation = context.getString(R.string.pref_location_default)
        return sp.getString(keyForLocation, defaultLocation)
    }

    /**
     * Returns true if the user has selected metric temperature display.
     *
     * @param context Context used to get the SharedPreferences
     * @return true if metric display should be used, false if imperial display should be used
     */
    fun isMetric(context: Context): Boolean {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val keyForUnits = context.getString(R.string.pref_units_key)
        val defaultUnits = context.getString(R.string.pref_units_metric)
        val preferredUnits = sp.getString(keyForUnits, defaultUnits)
        val metric = context.getString(R.string.pref_units_metric)
        var userPrefersMetric = false
        if (metric == preferredUnits) {
            userPrefersMetric = true
        }
        return userPrefersMetric
    }

    /**
     * Returns the location coordinates associated with the location. Note that there is a
     * possibility that these coordinates may not be set, which results in (0,0) being returned.
     * Interestingly, (0,0) is in the middle of the ocean off the west coast of Africa.
     *
     * @param context used to access SharedPreferences
     * @return an array containing the two coordinate values for the user's preferred location
     */
    fun getLocationCoordinates(context: Context?): DoubleArray {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val preferredCoordinates = DoubleArray(2)

        /*
         * This is a hack we have to resort to since you can't store doubles in SharedPreferences.
         *
         * Double.doubleToLongBits returns an integer corresponding to the bits of the given
         * IEEE 754 double precision value.
         *
         * Double.longBitsToDouble does the opposite, converting a long (that represents a double)
         * into the double itself.
         */preferredCoordinates[0] = java.lang.Double
            .longBitsToDouble(
                sp.getLong(
                    PREF_COORD_LAT,
                    java.lang.Double.doubleToRawLongBits(0.0)
                )
            )
        preferredCoordinates[1] = java.lang.Double
            .longBitsToDouble(
                sp.getLong(
                    PREF_COORD_LONG,
                    java.lang.Double.doubleToRawLongBits(0.0)
                )
            )
        return preferredCoordinates
    }

    /**
     * Returns true if the latitude and longitude values are available. The latitude and
     * longitude will not be available until the lesson where the PlacePicker API is taught.
     *
     * @param context used to get the SharedPreferences
     * @return true if lat/long are saved in SharedPreferences
     */
    fun isLocationLatLonAvailable(context: Context?): Boolean {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val spContainLatitude = sp.contains(PREF_COORD_LAT)
        val spContainLongitude = sp.contains(PREF_COORD_LONG)
        var spContainBothLatitudeAndLongitude = false
        if (spContainLatitude && spContainLongitude) {
            spContainBothLatitudeAndLongitude = true
        }
        return spContainBothLatitudeAndLongitude
    }

    /**
     * Returns the last time that a notification was shown (in UNIX time)
     *
     * @param context Used to access SharedPreferences
     * @return UNIX time of when the last notification was shown
     */
    private fun getLastNotificationTimeInMillis(context: Context): Long {
        /* Key for accessing the time at which Sunshine last displayed a notification */
        val lastNotificationKey = context.getString(R.string.pref_last_notification)

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)

        /*
         * Here, we retrieve the time in milliseconds when the last notification was shown. If
         * SharedPreferences doesn't have a value for lastNotificationKey, we return 0. The reason
         * we return 0 is because we compare the value returned from this method to the current
         * system time. If the difference between the last notification time and the current time
         * is greater than one day, we will show a notification again. When we compare the two
         * values, we subtract the last notification time from the current system time. If the
         * time of the last notification was 0, the difference will always be greater than the
         * number of milliseconds in a day and we will show another notification.
         */
        return sp.getLong(lastNotificationKey, 0)
    }

    /**
     * Returns the elapsed time in milliseconds since the last notification was shown. This is used
     * as part of our check to see if we should show another notification when the weather is
     * updated.
     *
     * @param context Used to access SharedPreferences as well as use other utility methods
     * @return Elapsed time in milliseconds since the last notification was shown
     */
    fun getEllapsedTimeSinceLastNotification(context: Context): Long {
        val lastNotificationTimeMillis =
            getLastNotificationTimeInMillis(context)
        return System.currentTimeMillis() - lastNotificationTimeMillis
    }

    /**
     * Saves the time that a notification is shown. This will be used to get the ellapsed time
     * since a notification was shown.
     *
     * @param context Used to access SharedPreferences
     * @param timeOfNotification Time of last notification to save (in UNIX time)
     */
    fun saveLastNotificationTime(
        context: Context,
        timeOfNotification: Long
    ) {
        val sp =
            PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        val lastNotificationKey = context.getString(R.string.pref_last_notification)
        editor.putLong(lastNotificationKey, timeOfNotification)
        editor.apply()
    }
}