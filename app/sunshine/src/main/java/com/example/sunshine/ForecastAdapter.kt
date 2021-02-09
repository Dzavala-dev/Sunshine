package com.example.sunshine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * [ForecastAdapter] exposes a list of weather forecasts to a
 * [android.support.v7.widget.RecyclerView]
 */
class ForecastAdapter
/**
 * Creates a ForecastAdapter.
 *
 * @param clickHandler The on-click handler for this adapter. This single handler is called
 * when an item is clicked.
 */(/*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private val mClickHandler: ForecastAdapterOnClickHandler
) :
    RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder?>() {
    private var mWeatherData: Array<String>? = null

    /**
     * The interface that receives onClick messages.
     */
    interface ForecastAdapterOnClickHandler {
        fun onClick(weatherForDay: String?)
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    inner class ForecastAdapterViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val mWeatherTextView: TextView

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        override fun onClick(v: View) {
            val adapterPosition: Int = getAdapterPosition()
            val weatherForDay = mWeatherData!![adapterPosition]
            mClickHandler.onClick(weatherForDay)
        }

        init {
            mWeatherTextView =
                view.findViewById<View>(R.id.tv_weather_data) as TextView
            view.setOnClickListener(this)
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     * can use this viewType integer to provide a different layout. See
     * [android.support.v7.widget.RecyclerView.Adapter.getItemViewType]
     * for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ForecastAdapterViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem: Int = R.layout.forecast_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false
        val view =
            inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        return ForecastAdapterViewHolder(view)
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
     * contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    fun onBindViewHolder(
        forecastAdapterViewHolder: ForecastAdapterViewHolder,
        position: Int
    ) {
        val weatherForThisDay = mWeatherData!![position]
        forecastAdapterViewHolder.mWeatherTextView.text = weatherForThisDay
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    val itemCount: Int
        get() = if (null == mWeatherData) 0 else mWeatherData!!.size

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param weatherData The new weather data to be displayed.
     */
    fun setWeatherData(weatherData: Array<String>?) {
        mWeatherData = weatherData
        notifyDataSetChanged()
    }

}