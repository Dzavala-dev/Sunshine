package com.example.sunshine

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import data.WeatherContract
import utilities.SunshineDateUtils
import utilities.SunshineWeatherUtils



class DetailActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor?> {
    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private var mForecastSummary: String? = null

    //  COMPLETED (15) Declare a private Uri field called mUri
    /* The URI that is used to access the chosen day's weather details */
    private var mUri: Uri? = null

    //  COMPLETED (10) Remove the mWeatherDisplay TextView declaration
    //  COMPLETED (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    private var mDateView: TextView? = null
    private var mDescriptionView: TextView? = null
    private var mHighTemperatureView: TextView? = null
    private var mLowTemperatureView: TextView? = null
    private var mHumidityView: TextView? = null
    private var mWindView: TextView? = null
    private var mPressureView: TextView? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        //      COMPLETED (12) Remove mWeatherDisplay TextView
//      COMPLETED (13) Find each of the TextViews by ID
        mDateView = findViewById(R.id.date)
        mDescriptionView = findViewById(R.id.weather_description)
        mHighTemperatureView = findViewById(R.id.high_temperature)
        mLowTemperatureView = findViewById(R.id.low_temperature)
        mHumidityView = findViewById(R.id.humidity)
        mWindView = findViewById(R.id.wind)
        mPressureView = findViewById(R.id.pressure)

//      COMPLETED (14) Remove the code that checks for extra text

//      COMPLETED (16) Use getData to get a reference to the URI passed with this Activity's Intent
        mUri = intent.data
        //      COMPLETED (17) Throw a NullPointerException if that URI is null
        if (mUri == null) throw NullPointerException("URI for DetailActivity cannot be null")

//      COMPLETED (35) Initialize the loader for DetailActivity
        /* This connects our Activity into the loader lifecycle. */getSupportLoaderManager().initLoader(
            ID_DETAIL_LOADER,
            null,
            this
        )
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
            R.menu.detail,
            menu
        )
        /* Return true so that the menu is displayed in the Toolbar */return true
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Get the ID of the clicked item */
        val id = item.itemId

        /* Settings menu item clicked */if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }

        /* Share menu item clicked */if (id == R.id.action_share) {
            val shareIntent = createShareForecastIntent()
            startActivity(shareIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private fun createShareForecastIntent(): Intent {
        val shareIntent: Intent = ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
            .intent
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        return shareIntent
    }
    //  COMPLETED (22) Override onCreateLoader
    /**
     * Creates and returns a CursorLoader that loads the data for our URI and stores it in a Cursor.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param loaderArgs Any arguments supplied by the caller
     *
     * @return A new Loader instance that is ready to start loading.
     */
    override fun onCreateLoader(loaderId: Int, loaderArgs: Bundle?): CursorLoader? {
        return when (loaderId) {
            ID_DETAIL_LOADER -> mUri?.let {
                CursorLoader(
                    this,
                    it,
                    WEATHER_DETAIL_PROJECTION,
                    null,
                    null,
                    null
                )
            }
            else -> throw RuntimeException("Loader Not Implemented: $loaderId")
        }
    }
    //  COMPLETED (24) Override onLoadFinished
    /**
     * Runs on the main thread when a load is complete. If initLoader is called (we call it from
     * onCreate in DetailActivity) and the LoaderManager already has completed a previous load
     * for this Loader, onLoadFinished will be called immediately. Within onLoadFinished, we bind
     * the data to our views so the user can see the details of the weather on the date they
     * selected from the forecast.
     *
     * @param loader The cursor loader that finished.
     * @param data   The cursor that is being returned.
     */
    fun onLoadFinished(
        loader: Loader<Cursor?>?,
        data: Cursor?
    ) {

//      COMPLETED (25) Check before doing anything that the Cursor has valid data
        /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        var cursorHasValidData = false
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true
        }
        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return
        }

//      COMPLETED (26) Display a readable data string
        /****************
         * Weather Date *
         */
        /*
         * Read the date from the cursor. It is important to note that the date from the cursor
         * is the same date from the weather SQL table. The date that is stored is a GMT
         * representation at midnight of the date when the weather information was loaded for.
         *
         * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         * SunshineDateUtils#getFriendlyDateString takes care of this for us.
         */  val localDateMidnightGmt =
            data!!.getLong(INDEX_WEATHER_DATE)
        val dateText: String =
            SunshineDateUtils.getFriendlyDateString(this, localDateMidnightGmt, true)
        mDateView!!.text = dateText

//      COMPLETED (27) Display the weather description (using SunshineWeatherUtils)
        /***********************
         * Weather Description *
         */
        /* Read weather condition ID from the cursor (ID provided by Open Weather Map) */  val weatherId =
            data.getInt(INDEX_WEATHER_CONDITION_ID)
        /* Use the weatherId to obtain the proper description */
        val description: String =
            SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId)

        /* Set the text */mDescriptionView!!.text = description

//      COMPLETED (28) Display the high temperature
        /**************************
         * High (max) temperature *
         */
        /* Read high temperature from the cursor (in degrees celsius) */  val highInCelsius =
            data.getDouble(INDEX_WEATHER_MAX_TEMP)
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val highString: String = SunshineWeatherUtils.formatTemperature(this, highInCelsius)

        /* Set the text */mHighTemperatureView!!.text = highString

//      COMPLETED (29) Display the low temperature
        /*************************
         * Low (min) temperature *
         */
        /* Read low temperature from the cursor (in degrees celsius) */  val lowInCelsius =
            data.getDouble(INDEX_WEATHER_MIN_TEMP)
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val lowString: String = SunshineWeatherUtils.formatTemperature(this, lowInCelsius)

        /* Set the text */mLowTemperatureView!!.text = lowString

//      COMPLETED (30) Display the humidity
        /************
         * Humidity *
         */
        /* Read humidity from the cursor */  val humidity =
            data.getFloat(INDEX_WEATHER_HUMIDITY)
        val humidityString: String = getString(R.string.format_humidity, humidity)

        /* Set the text */mHumidityView!!.text = humidityString

//      COMPLETED (31) Display the wind speed and direction
        /****************************
         * Wind speed and direction *
         */
        /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */  val windSpeed =
            data.getFloat(INDEX_WEATHER_WIND_SPEED)
        val windDirection =
            data.getFloat(INDEX_WEATHER_DEGREES)
        val windString: String =
            SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection)

        /* Set the text */mWindView!!.text = windString

//      COMPLETED (32) Display the pressure
        /************
         * Pressure *
         */
        /* Read pressure from the cursor */  val pressure =
            data.getFloat(INDEX_WEATHER_PRESSURE)

        /*
         * Format the pressure text using string resources. The reason we directly access
         * resources using getString rather than using a method from SunshineWeatherUtils as
         * we have for other data displayed in this Activity is because there is no
         * additional logic that needs to be considered in order to properly display the
         * pressure.
         */
        val pressureString: String = getString(R.string.format_pressure, pressure)

        /* Set the text */mPressureView!!.text = pressureString

//      COMPLETED (33) Store a forecast summary in mForecastSummary
        /* Store the forecast summary String in our forecast summary field to share later */mForecastSummary =
            String.format(
                "%s - %s - %s/%s",
                dateText, description, highString, lowString
            )
    }
    //  COMPLETED (34) Override onLoaderReset, but don't do anything in it yet
    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * Since we don't store any of this cursor's data, there are no references we need to remove.
     *
     * @param loader The Loader that is being reset.
     */
    fun onLoaderReset(loader: Loader<Cursor?>?) {}

    companion object {
        /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
        private const val FORECAST_SHARE_HASHTAG = " #SunshineApp"

        //  COMPLETED (18) Create a String array containing the names of the desired data columns from our ContentProvider
        /*
     * The columns of data that we are interested in displaying within our DetailActivity's
     * weather display.
     */
        val WEATHER_DETAIL_PROJECTION = arrayOf<String>(
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        )

        //  COMPLETED (19) Create constant int values representing each column name's position above
        /*
     * We store the indices of the values in the array of Strings above to more quickly be able
     * to access the data from our query. If the order of the Strings above changes, these
     * indices must be adjusted to match the order of the Strings.
     */
        const val INDEX_WEATHER_DATE = 0
        const val INDEX_WEATHER_MAX_TEMP = 1
        const val INDEX_WEATHER_MIN_TEMP = 2
        const val INDEX_WEATHER_HUMIDITY = 3
        const val INDEX_WEATHER_PRESSURE = 4
        const val INDEX_WEATHER_WIND_SPEED = 5
        const val INDEX_WEATHER_DEGREES = 6
        const val INDEX_WEATHER_CONDITION_ID = 7

        //  COMPLETED (20) Create a constant int to identify our loader used in DetailActivity
        /*
     * This ID will be used to identify the Loader responsible for loading the weather details
     * for a particular day. In some cases, one Activity can deal with many Loaders. However, in
     * our case, there is only one. We will still use this ID to initialize the loader and create
     * the loader for best practice. Please note that 353 was chosen arbitrarily. You can use
     * whatever number you like, so long as it is unique and consistent.
     */
        private const val ID_DETAIL_LOADER = 353
    }
}