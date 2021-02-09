package com.example.sunshine

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.app.LoaderManager.LoaderCallbacks
import androidx.core.content.AsyncTaskLoader
import androidx.core.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.sunshine.R
import com.example.android.sunshine.data.SunshinePreferences
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import data.SunshinePreferences
import java.net.URL


class MainActivity() : AppCompatActivity(), ForecastAdapter.ForecastAdapterOnClickHandler,
    LoaderManager.LoaderCallbacks<Array<String?>?>, OnSharedPreferenceChangeListener, Parcelable {
    private var mRecyclerView: RecyclerView? = null
    private var mForecastAdapter: ForecastAdapter? = null
    private var mErrorMessageDisplay: TextView? = null
    private var mLoadingIndicator: ProgressBar? = null

    constructor(parcel: Parcel) : this() {
        mForecastAdapter = parcel.readParcelable(ForecastAdapter::class.java.classLoader)
    }

    @SuppressLint("WrongConstant")
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */mRecyclerView = findViewById<RecyclerView>(R.id.recyclerview_forecast)

        /* This TextView is used to display errors and will be hidden if there are no errors */mErrorMessageDisplay =
            findViewById<TextView>(
                R.id.tv_error_message_display
            )

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * LinearLayoutManager class for vertical lists, LinearLayoutManager.VERTICAL.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        val recyclerViewOrientation: Int = LinearLayoutManager.VERTICAL

        /*
         *  This value should be true if you want to reverse your layout. Generally, this is only
         *  true with horizontal lists that need to support a right-to-left layout.
         */
        val shouldReverseLayout = false
        val layoutManager =
            LinearLayoutManager(this, recyclerViewOrientation, shouldReverseLayout)
        mRecyclerView.setLayoutManager(layoutManager)

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */mRecyclerView.setHasFixedSize(true)

        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */mForecastAdapter = ForecastAdapter(this)

        /* Setting the adapter attaches it to the RecyclerView in our layout. */mRecyclerView.setAdapter(
            mForecastAdapter
        )

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */mLoadingIndicator = findViewById(R.id.pb_loading_indicator) as ProgressBar?

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        val loaderId = FORECAST_LOADER_ID

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        val callback: LoaderCallbacks<Array<String>> = this@MainActivity

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        val bundleForLoader: Bundle? = null

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback)
        Log.d(
            TAG,
            "onCreate: registering preference changed listener"
        )

        // COMPLETED (6) Register MainActivity as a OnSharedPreferenceChangedListener in onCreate
        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    fun onCreateLoader(id: Int, loaderArgs: Bundle?): Loader<Array<String>> {
        return object : AsyncTaskLoader<Array<String?>?>(this) {
            /* This String array will hold and help cache our weather data */
            var mWeatherData: Array<String?>? = null

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            protected fun onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData)
                } else {
                    mLoadingIndicator!!.visibility = View.VISIBLE
                    forceLoad()
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             * null if an error occurs
             */
            fun loadInBackground(): Array<String>? {
                val locationQuery: String = SunshinePreferences
                    .getPreferredWeatherLocation(this@MainActivity)
                val weatherRequestUrl: URL = NetworkUtils.buildUrl(locationQuery)
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

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            fun deliverResult(data: Array<String?>?) {
                mWeatherData = data
                super.deliverResult(data)
            }
        }
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    fun onLoadFinished(
        loader: Loader<Array<String?>?>?,
        data: Array<String?>?
    ) {
        mLoadingIndicator!!.visibility = View.INVISIBLE
        mForecastAdapter!!.setWeatherData(data)
        if (null == data) {
            showErrorMessage()
        } else {
            showWeatherDataView()
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    fun onLoaderReset(loader: Loader<Array<String?>?>?) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private fun invalidateData() {
        mForecastAdapter!!.setWeatherData(null)
    }

    /**
     * This method uses the URI scheme for showing a location found on a map in conjunction with
     * an implicit Intent. This super-handy intent is detailed in the "Common Intents" page of
     * Android's developer site:
     *
     * @see "http://developer.android.com/guide/components/intents-common.html.Maps"
     *
     *
     * Protip: Hold Command on Mac or Control on Windows and click that link to automagically
     * open the Common Intents page
     */
    private fun openLocationInMap() {
        // COMPLETED (9) Use preferred location rather than a default location to display in the map
        val addressString: String = SunshinePreferences.getPreferredWeatherLocation(this)
        val geoLocation = Uri.parse("geo:0,0?q=$addressString")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = geoLocation
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        } else {
            Log.d(
                TAG,
                "Couldn't call $geoLocation, no receiving apps installed!"
            )
        }
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param weatherForDay String describing weather details for a particular day
     */
    fun onClick(weatherForDay: String?) {
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
        /* Then, make sure the weather data is visible */mRecyclerView.setVisibility(View.VISIBLE)
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
        mRecyclerView.setVisibility(View.INVISIBLE)
        /* Then, show the error */mErrorMessageDisplay!!.visibility = View.VISIBLE
    }
    // COMPLETED (7) In onStart, if preferences have been changed, refresh the data and set the flag to false
    /**
     * OnStart is called when the Activity is coming into view. This happens when the Activity is
     * first created, but also happens when the Activity is returned to from another Activity. We
     * are going to use the fact that onStart is called when the user returns to this Activity to
     * check if the location setting or the preferred units setting has changed. If it has changed,
     * we are going to perform a new query.
     */
    protected override fun onStart() {
        super.onStart()

        /*
         * If the preferences for location or units have changed since the user was last in
         * MainActivity, perform another query and set the flag to false.
         *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
         * job done for now. Later in this course, we are going to show you more elegant ways to
         * handle converting the units from celsius to fahrenheit and back without hitting the
         * network again by keeping a copy of the data in a manageable format.
         */if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated")
            getSupportLoaderManager().restartLoader(
                FORECAST_LOADER_ID,
                null,
                this
            )
            PREFERENCES_HAVE_BEEN_UPDATED = false
        }
    }

    // COMPLETED (8) Override onDestroy and unregister MainActivity as a SharedPreferenceChangedListener
    protected override fun onDestroy() {
        super.onDestroy()

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */PreferenceManager.getDefaultSharedPreferences(
            this
        )
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        val inflater: MenuInflater = getMenuInflater()
        /* Use the inflater's inflate method to inflate our menu layout to this menu */inflater.inflate(
            R.menu.forecast, menu
        )
        /* Return true so that the menu is displayed in the Toolbar */return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_refresh) {
            invalidateData()
            getSupportLoaderManager().restartLoader(
                FORECAST_LOADER_ID,
                null,
                this
            )
            return true
        }
        if (id == R.id.action_map) {
            openLocationInMap()
            return true
        }
        if (id == R.id.action_settings) {
            val startSettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(startSettingsActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // COMPLETED (5) Override onSharedPreferenceChanged to set the preferences flag to true
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        s: String
    ) {
        /*
         * Set this flag to true so that when control returns to MainActivity, it can refresh the
         * data.
         *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
         * job done for now. Later in this course, we are going to show you more elegant ways to
         * handle converting the units from celsius to fahrenheit and back without hitting the
         * network again by keeping a copy of the data in a manageable format.
         */
        PREFERENCES_HAVE_BEEN_UPDATED = true
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val FORECAST_LOADER_ID = 0

        // COMPLETED (4) Add a private static boolean flag for preference updates and initialize it to false
        private var PREFERENCES_HAVE_BEEN_UPDATED = false
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(mForecastAdapter, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

    override fun onLoadFinished(loader: Loader<Array<String?>?>, data: Array<String?>?) {
        TODO("Not yet implemented")
    }

    override fun onLoaderReset(loader: Loader<Array<String?>?>) {
        TODO("Not yet implemented")
    }
}