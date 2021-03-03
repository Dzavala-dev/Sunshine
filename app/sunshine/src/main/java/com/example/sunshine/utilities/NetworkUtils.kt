package com.example.sunshine.utilities

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


/**
 * These utilities will be used to communicate with the weather servers.
 */
object NetworkUtils {
    private val TAG = NetworkUtils::class.java.simpleName

    /*
     * Sunshine was originally built to use OpenWeatherMap's API. However, we wanted to provide
     * a way to much more easily test the app and provide more varied weather data. After all, in
     * Mountain View (Google's HQ), it gets very boring looking at a forecast of perfectly clear
     * skies at 75°F every day... (UGH!) The solution we came up with was to host our own fake
     * weather server. With this server, there are two URL's you can use. The first (and default)
     * URL will return dynamic weather data. Each time the app refreshes, you will get different,
     * completely random weather data. This is incredibly useful for testing the robustness of your
     * application, as different weather JSON will provide edge cases for some of your methods.
     *
     * If you'd prefer to test with the weather data that you will see in the videos on Udacity,
     * you can do so by setting the FORECAST_BASE_URL to STATIC_WEATHER_URL below.
     */
    private const val DYNAMIC_WEATHER_URL = "https://andfun-weather.udacity.com/weather"
    private const val STATIC_WEATHER_URL =
        "https://andfun-weather.udacity.com/staticweather"
    private const val FORECAST_BASE_URL = STATIC_WEATHER_URL

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */
    /* The format we want our API to return */
    private const val format = "json"

    /* The units we want our API to return */
    private const val units = "metric"

    /* The number of days we want our API to return */
    private const val numDays = 14

    /* The query parameter allows us to provide a location string to the API */
    private const val QUERY_PARAM = "q"
    private const val LAT_PARAM = "lat"
    private const val LON_PARAM = "lon"

    /* The format parameter allows us to designate whether we want JSON or XML from our API */
    private const val FORMAT_PARAM = "mode"

    /* The units parameter allows us to designate whether we want metric units or imperial units */
    private const val UNITS_PARAM = "units"

    /* The days parameter allows us to designate how many days of weather data we want */
    private const val DAYS_PARAM = "cnt"

    /**
     * Retrieves the proper URL to query for the weather data. The reason for both this method as
     * well as [.buildUrlWithLocationQuery] is two fold.
     *
     *
     * 1) You should be able to just use one method when you need to create the URL within the
     * app instead of calling both methods.
     * 2) Later in Sunshine, you are going to add an alternate method of allowing the user
     * to select their preferred location. Once you do so, there will be another way to form
     * the URL using a latitude and longitude rather than just a location String. This method
     * will "decide" which URL to build and return it.
     *
     * @param context used to access other Utility methods
     * @return URL to query weather service
     */
    fun getUrl(context: Context?): URL? {
        return if (SunshinePreferences.isLocationLatLonAvailable(context)) {
            val preferredCoordinates: DoubleArray =
                SunshinePreferences.getLocationCoordinates(context)
            val latitude = preferredCoordinates[0]
            val longitude = preferredCoordinates[1]
            buildUrlWithLatitudeLongitude(latitude, longitude)
        } else {
            val locationQuery: String =
                SunshinePreferences.getPreferredWeatherLocation(context)
            buildUrlWithLocationQuery(locationQuery)
        }
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param latitude  The latitude of the location
     * @param longitude The longitude of the location
     * @return The Url to use to query the weather server.
     */
    private fun buildUrlWithLatitudeLongitude(
        latitude: Double,
        longitude: Double
    ): URL? {
        val weatherQueryUri =
            Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(LAT_PARAM, latitude.toString())
                .appendQueryParameter(LON_PARAM, longitude.toString())
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(
                    DAYS_PARAM,
                    Integer.toString(numDays)
                )
                .build()
        return try {
            val weatherQueryUrl = URL(weatherQueryUri.toString())
            Log.v(TAG, "URL: $weatherQueryUrl")
            weatherQueryUrl
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    private fun buildUrlWithLocationQuery(locationQuery: String): URL? {
        val weatherQueryUri =
            Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(
                    DAYS_PARAM,
                    Integer.toString(numDays)
                )
                .build()
        return try {
            val weatherQueryUrl = URL(weatherQueryUri.toString())
            Log.v(TAG, "URL: $weatherQueryUrl")
            weatherQueryUrl
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection =
            url.openConnection() as HttpURLConnection
        return try {
            val `in` = urlConnection.inputStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            var response: String? = null
            if (hasInput) {
                response = scanner.next()
            }
            scanner.close()
            response
        } finally {
            urlConnection.disconnect()
        }
    }
}