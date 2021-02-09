package com.example.sunshine

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailActivity : AppCompatActivity() {
    private var mForecast: String? = null
    private var mWeatherDisplay: TextView? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mWeatherDisplay = findViewById(R.id.tv_display_weather) as TextView?
        val intentThatStartedThisActivity: Intent = getIntent()
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mForecast = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT)
                mWeatherDisplay!!.text = mForecast
            }
        }
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing. We set the
     * type of content that we are sharing (just regular text), the text itself, and we return the
     * newly created Intent.
     *
     * @return The Intent to use to start our share.
     */
    private fun createShareForecastIntent(): Intent {
        return ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText(mForecast + FORECAST_SHARE_HASHTAG)
            .getIntent()
    }

    fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.detail, menu)
        val menuItem = menu.findItem(R.id.action_share)
        menuItem.intent = createShareForecastIntent()
        return true
    }

    companion object {
        private const val FORECAST_SHARE_HASHTAG = " #SunshineApp"
    }
}