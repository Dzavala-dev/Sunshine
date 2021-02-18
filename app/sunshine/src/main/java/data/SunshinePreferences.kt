package data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.android.sunshine.R


class SunshinePreferences {
    /*
     * Human readable location string, provided by the API.  Because for styling,
     * "Mountain View" is more recognizable than 94043.
     */
     val PREF_CITY_NAME = "city_name"

    /*
     * In order to uniquely pinpoint the location on the map when we launch the
     * map intent, we store the latitude and longitude.
     */
     val PREF_COORD_LAT = "coord_lat"
     val PREF_COORD_LONG = "coord_long"

    /** This will be implemented in a future lesson  */
    /*
         * Before you implement methods to return your REAL preference for location,
         * we provide some default values to work with.
         */
    private  val defaultWeatherLocation = "94043,USA"

    /** This will be implemented in a future lesson  */
    val defaultWeatherCoordinates = doubleArrayOf(37.4284, 122.0724)
    private  val DEFAULT_MAP_LOCATION =
        "1600 Amphitheatre Parkway, Mountain View, CA 94043"

    /**
     * Helper method to handle setting location details in Preferences (City Name, Latitude,
     * Longitude)
     *
     * @param c        Context used to get the SharedPreferences
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat      The latitude of the city
     * @param lon      The longitude of the city
     */
    override fun setLocationDetails(
        c: Context?,
        cityName: String?,
        lat: Double,
        lon: Double
    ) {
        /** This will be implemented in a future lesson  */
    }

    /**
     * Helper method to handle setting a new location in preferences.  When this happens
     * the database may need to be cleared.
     *
     * @param c               Context used to get the SharedPreferences
     * @param locationSetting The location string used to request updates from the server.
     * @param lat             The latitude of the city
     * @param lon             The longitude of the city
     */
    override fun setLocation(
        c: Context?,
        locationSetting: String?,
        lat: Double,
        lon: Double
    ) {
        /** This will be implemented in a future lesson  */
    }

    /**
     * Resets the stored location coordinates.
     *
     * @param c Context used to get the SharedPreferences
     */
    fun resetLocationCoordinates(c: Context?) {
        /** This will be implemented in a future lesson  */
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "94043,USA", which is Mountain View, California. Mountain View is the
     * home of the headquarters of the Googleplex!
     *
     * @param context Context used to get the SharedPreferences
     * @return Location The current user has set in SharedPreferences. Will default to
     * "94043,USA" if SharedPreferences have not been implemented yet.
     */
    override fun getPreferredWeatherLocation(context: Context): String? {
        // COMPLETED (1) Return the user's preferred location
        val prefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        val keyForLocation = context.getString(R.string.pref_location_key)
        val defaultLocation = context.getString(R.string.pref_location_default)
        return prefs.getString(keyForLocation, defaultLocation)
    }

    /**
     * Returns true if the user has selected metric temperature display.
     *
     * @param context Context used to get the SharedPreferences
     *
     * @return true If metric display should be used
     */
    override fun isMetric(context: Context): Boolean {
        // COMPLETED (2) Return true if the user's preference for units is metric, false otherwise
        val prefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        val keyForUnits = context.getString(R.string.pref_units_key)
        val defaultUnits = context.getString(R.string.pref_units_metric)
        val preferredUnits = prefs.getString(keyForUnits, defaultUnits)
        val metric = context.getString(R.string.pref_units_metric)
        val userPrefersMetric: Boolean
        userPrefersMetric = metric == preferredUnits
        return userPrefersMetric
    }

    /**
     * Returns the location coordinates associated with the location.  Note that these coordinates
     * may not be set, which results in (0,0) being returned. (conveniently, 0,0 is in the middle
     * of the ocean off the west coast of Africa)
     *
     * @param context Used to get the SharedPreferences
     * @return An array containing the two coordinate values.
     */
    override fun getLocationCoordinates(context: Context?): DoubleArray {
        return defaultWeatherCoordinates
    }

    /**
     * Returns true if the latitude and longitude values are available. The latitude and
     * longitude will not be available until the lesson where the PlacePicker API is taught.
     *
     * @param context used to get the SharedPreferences
     * @return true if lat/long are set
     */
    override fun isLocationLatLonAvailable(context: Context?): Boolean {
        /** This will be implemented in a future lesson  */
        return false
    }

}