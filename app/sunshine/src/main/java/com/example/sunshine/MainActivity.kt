package com.example.sunshine

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.SunshinePreferences
import data.WeatherContract
import utilities.FakeDataUtils


class MainActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor?>, ForecastAdapter.ForecastAdapterOnClickHandler {
    private val TAG = MainActivity::class.java.simpleName
    private var mForecastAdapter: ForecastAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mPosition: Int = RecyclerView.NO_POSITION
    private var mLoadingIndicator: ProgressBar? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        supportActionBar?.elevation = 0f
        FakeDataUtils.insertFakeData(this)

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */mRecyclerView = findViewById(R.id.recyclerview_forecast)

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */mLoadingIndicator = findViewById(R.id.pb_loading_indicator)

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * LinearLayoutManager class for vertical lists, LinearLayoutManager.VERTICAL.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         *
         * The third parameter (shouldReverseLayout) should be true if you want to reverse your
         * layout. Generally, this is only true with horizontal lists that need to support a
         * right-to-left layout.
         */
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(
            layoutManager
        )

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true)

        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         *
         * Although passing in "this" twice may seem strange, it is actually a sign of separation
         * of concerns, which is best programming practice. The ForecastAdapter requires an
         * Android Context (which all Activities are) as well as an onClickHandler. Since our
         * MainActivity implements the ForecastAdapter ForecastOnClickHandler interface, "this"
         * is also an instance of that type of handler.
         */mForecastAdapter = ForecastAdapter(this, this)

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter!!)
        showLoading()

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */getSupportLoaderManager().initLoader(
            ID_FORECAST_LOADER,
            null,
            this
        )
    }

    /**
     * Uses the URI scheme for showing a location found on a map in conjunction with
     * an implicit Intent. This super-handy Intent is detailed in the "Common Intents" page of
     * Android's developer site:
     *
     * @see "http://developer.android.com/guide/components/intents-common.html.Maps"
     *
     *
     * Protip: Hold Command on Mac or Control on Windows and click that link to automagically
     * open the Common Intents page
     */
    private fun openPreferredLocationInMap() {
        val coords: DoubleArray = SunshinePreferences.getLocationCoordinates(this)
        val posLat = coords[0].toString()
        val posLong = coords[1].toString()
        val geoLocation = Uri.parse("geo:$posLat,$posLong")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = geoLocation
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.d(
                TAG,
                "Couldn't call $geoLocation, no receiving apps installed!"
            )
        }
    }

    /**
     * Called by the [android.support.v4.app.LoaderManagerImpl] when a new Loader needs to be
     * created. This Activity only uses one loader, so we don't necessarily NEED to check the
     * loaderId, but this is certainly best practice.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    override fun onCreateLoader(loaderId: Int, bundle: Bundle?): Loader<Cursor?> {
        return when (loaderId) {
            ID_FORECAST_LOADER -> {
                /* URI for all rows of weather data in our weather table */
                val forecastQueryUri: Uri = WeatherContract.WeatherEntry.CONTENT_URI
                /* Sort order: Ascending by date */
                val sortOrder: String =
                    WeatherContract.WeatherEntry.COLUMN_DATE.toString() + " ASC"
                /*
                     * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                     * want all weather data from today onwards that is stored in our weather table.
                     * We created a handy method to do that in our WeatherEntry class.
                     */
                val selection: String =
                    WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards()
                CursorLoader(
                    this,
                    forecastQueryUri,
                    MAIN_FORECAST_PROJECTION,
                    selection,
                    null,
                    sortOrder
                )
            }
            else -> throw RuntimeException("Loader Not Implemented: $loaderId")
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
     * initial load being performed with no access to internet, the loading indicator will show
     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
     * future version of the course.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    fun onLoadFinished(
        loader: Loader<Cursor?>?,
        data: Cursor
    ) {
        mForecastAdapter!!.swapCursor(data)
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0
        mRecyclerView?.smoothScrollToPosition(mPosition)
        if (data.count != 0) showWeatherDataView()
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    fun onLoaderReset(loader: Loader<Cursor?>?) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mForecastAdapter!!.swapCursor(null)
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param date Normalized UTC time that represents the local date of the weather in GMT time.
     * @see WeatherContract.WeatherEntry.COLUMN_DATE
     */
    //  COMPLETED (38) Refactor onClick to accept a long instead of a String as its parameter
    override fun onClick(date: Long) {
        val weatherDetailIntent = Intent(this@MainActivity, DetailActivity::class.java)
        //      COMPLETED (39) Refactor onClick to pass the URI for the clicked date with the Intent
        val uriForDateClicked: Uri =
            WeatherContract.WeatherEntry.buildWeatherUriWithDate(date)
        weatherDetailIntent.data = uriForDateClicked
        startActivity(weatherDetailIntent)
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private fun showWeatherDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator!!.visibility = View.INVISIBLE
        /* Finally, make sure the weather data is visible */mRecyclerView?.visibility = View.VISIBLE
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private fun showLoading() {
        /* Then, hide the weather data */
        mRecyclerView?.visibility = View.INVISIBLE
        /* Finally, show the loading indicator */mLoadingIndicator!!.visibility = View.VISIBLE
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        val inflater: MenuInflater = menuInflater
        /* Use the inflater's inflate method to inflate our menu layout to this menu */inflater.inflate(
            R.menu.forecast,
            menu
        )
        /* Return true so that the menu is displayed in the Toolbar */return true
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
        val MAIN_FORECAST_PROJECTION = arrayOf<String>(
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        )

        /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
        const val INDEX_WEATHER_DATE = 0
        const val INDEX_WEATHER_MAX_TEMP = 1
        const val INDEX_WEATHER_MIN_TEMP = 2
        const val INDEX_WEATHER_CONDITION_ID = 3

        /*
     * This ID will be used to identify the Loader responsible for loading our weather forecast. In
     * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
     * We will still use this ID to initialize the loader and create the loader for best practice.
     * Please note that 44 was chosen arbitrarily. You can use whatever number you like, so long as
     * it is unique and consistent.
     */
        private const val ID_FORECAST_LOADER = 44
    }
}

private fun RecyclerView.setAdapter(mForecastAdapter: ForecastAdapter) {

}
