package com.example.sunshine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.SunshinePreferences
import utilities.NetworkUtils
import utilities.OpenWeatherJsonUtils

// COMPLETED (8) Implement ForecastAdapterOnClickHandler from the MainActivity
class MainActivity : AppCompatActivity(), com.example.sunshine.ForecastAdapter.ForecastAdapterOnClickHandler {
    private var mRecyclerView: RecyclerView? = null
    private var mForecastAdapter: com.example.sunshine.ForecastAdapter? = null
    private var mErrorMessageDisplay: TextView? = null
    private var mLoadingIndicator: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */mRecyclerView = findViewById(R.id.recyclerview_forecast) as RecyclerView

        /* This TextView is used to display errors and will be hidden if there are no errors */mErrorMessageDisplay =
            findViewById(R.id.tv_error_message_display) as TextView

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.layoutManager = layoutManager

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */mRecyclerView!!.setHasFixedSize(true)

        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */mForecastAdapter = com.example.sunshine.ForecastAdapter(this)

        /* Setting the adapter attaches it to the RecyclerView in our layout. */mRecyclerView!!.adapter =
            mForecastAdapter

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */mLoadingIndicator = findViewById(R.id.pb_loading_indicator) as ProgressBar

        /* Once all of our views are setup, we can load the weather data. */loadWeatherData()
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private fun loadWeatherData() {
        showWeatherDataView()
        val location: String = SunshinePreferences.getPreferredWeatherLocation(this)
        FetchWeatherTask().execute(location)
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param weatherForDay The weather for the day that was clicked
     */
    override fun onClick(weatherForDay: String?) {
        val context: Context = this
        val destinationClass: Class<*> = DetailActivity::class.java
        val intentToStartDetailActivity = Intent(context, destinationClass)
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay)
        startActivity(intentToStartDetailActivity)
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private fun showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay!!.visibility = View.INVISIBLE
        /* Then, make sure the weather data is visible */
        mRecyclerView!!.visibility = View.VISIBLE
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private fun showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView!!.visibility = View.INVISIBLE
        /* Then, show the error */
        mErrorMessageDisplay!!.visibility = View.VISIBLE
    }

    inner class FetchWeatherTask :
        AsyncTask<String?, Void?, Array<String>?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            mLoadingIndicator!!.visibility = View.VISIBLE
        }

        protected override fun doInBackground(vararg p0: String?): Array<String>? {

            /* If there's no zip code, there's nothing to look up. */
            if (params.size == 0) {
                return null
            }
            val location = params[0]
            val weatherRequestUrl = NetworkUtils.buildUrl(location)
            return try {
                val jsonWeatherResponse: String = NetworkUtils
                    .getResponseFromHttpUrl(weatherRequestUrl)
                OpenWeatherJsonUtils
                    .getSimpleWeatherStringsFromJson(this@MainActivity, jsonWeatherResponse)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(weatherData: Array<String>?) {
            mLoadingIndicator!!.visibility = View.INVISIBLE
            if (weatherData != null) {
                showWeatherDataView()
                mForecastAdapter!!.setWeatherData(weatherData)
            } else {
                showErrorMessage()
            }
        }
    }

    /**
     * This method uses the URI scheme for showing a location found on a
     * map. This super-handy intent is detailed in the "Common Intents"
     * page of Android's developer site:
     *
     * @see <a></a>"http://developer.android.com/guide/components/intents-common.html.Maps">
     *
     * Hint: Hold Command on Mac or Control on Windows and click that link
     * to automagically open the Common Intents page
     */
    private fun openLocationInMap() {
        val addressString = "1600 Ampitheatre Parkway, CA"
        val geoLocation = Uri.parse("geo:0,0?q=$addressString")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = geoLocation
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.d(
                TAG, "Couldn't call " + geoLocation.toString()
                        + ", no receiving apps installed!"
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        val inflater = menuInflater
        /* Use the inflater's inflate method to inflate our menu layout to this menu */inflater.inflate(
            R.menu.forecast,
            menu
        )
        /* Return true so that the menu is displayed in the Toolbar */return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_refresh) {
            mForecastAdapter!!.setWeatherData(null)
            loadWeatherData()
            return true
        }

        // COMPLETED (2) Launch the map when the map menu item is clicked
        if (id == R.id.action_map) {
            openLocationInMap()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}