package utilities

import android.content.ContentValues
import android.content.Context
import java.util.*
import java.util.concurrent.TimeUnit
import data.WeatherContract
import data.WeatherContract.WeatherEntry



object FakeDataUtils {
    private val weatherIDs = intArrayOf(200, 300, 500, 711, 900, 962)

    /**
     * Creates a single ContentValues object with random weather data for the provided date
     * @param date a normalized date
     * @return ContentValues object filled with random weather data
     */
    private fun createTestWeatherContentValues(date: Long): ContentValues {
        val testWeatherValues = ContentValues()
        testWeatherValues.put(WeatherEntry.COLUMN_DATE, date)
        testWeatherValues.put(WeatherEntry.COLUMN_DEGREES, Math.random() * 2)
        testWeatherValues.put(WeatherEntry.COLUMN_HUMIDITY, Math.random() * 100)
        testWeatherValues.put(WeatherEntry.COLUMN_PRESSURE, 870 + Math.random() * 100)
        val maxTemp = (Math.random() * 100).toInt()
        testWeatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, maxTemp)
        testWeatherValues.put(
            WeatherEntry.COLUMN_MIN_TEMP,
            maxTemp - (Math.random() * 10).toInt()
        )
        testWeatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, Math.random() * 10)
        testWeatherValues.put(
            WeatherEntry.COLUMN_WEATHER_ID,
            FakeDataUtils.weatherIDs.get((Math.random() * 10).toInt() % 5)
        )
        return testWeatherValues
    }

    /**
     * Creates random weather data for 7 days starting today
     * @param context
     */
    fun insertFakeData(context: Context) {
        //Get today's normalized date
        val today: Long =
            SunshineDateUtils.normalizeDate(System.currentTimeMillis())
        val fakeValues: MutableList<ContentValues> =
            ArrayList()
        //loop over 7 days starting today onwards
        for (i in 0..6) {
            fakeValues.add(
                FakeDataUtils.createTestWeatherContentValues(
                    today + TimeUnit.DAYS.toMillis(
                        i.toLong()
                    )
                )
            )
        }
        // Bulk Insert our new weather data into Sunshine's Database
        context.contentResolver.bulkInsert(
            WeatherContract.WeatherEntry.CONTENT_URI,
            fakeValues.toTypedArray()
        )
    }
}
