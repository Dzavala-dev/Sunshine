package com.example.visual_polish

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * We couldn't come up with a good name for this class. Then, we realized
 * that this lesson is about RecyclerView.
 *
 * RecyclerView... Recycling... Saving the planet? Being green? Anyone?
 * #crickets
 *
 * Avoid unnecessary garbage collection by using RecyclerView and ViewHolders.
 *
 * If you don't like our puns, we named this Adapter GreenAdapter because its
 * contents are green.
 */
abstract class SelectorItemsAdapter
/**
 * Constructor for Adapter; includes a list item click listener
 *
 * @param listener Listener for list item clicks
 */(/*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private val mOnClickListener: ListItemClickListener
) :
    RecyclerView.Adapter<SelectorItemsAdapter.ArticleViewHolder?>() {

    /**
     * The interface that receives onClick messages.
     */
    interface ListItemClickListener {
        fun onListItemClick(clickedItemIndex: Int)
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     * can use this viewType integer to provide a different layout. See
     * {android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     * for more details.
     * @return A new ArticleViewHolder that holds the View for each list item
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.selector_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false
        val view =
            inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        return ArticleViewHolder(view)
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    fun onBindViewHolder(
        holder: ArticleViewHolder?,
        position: Int
    ) {
        Log.d(TAG, "#$position")

        //Set values if given from database - for now they are placeholders in the list_item xml,
        // so this won't be implemented
    }

    /**
     * This method simply returns 10 items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    override fun getItemCount(): Int {
        return 10
    }

    /**
     * Cache of the children views for a list item.
     */
    inner class ArticleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var icon: ImageView
        var firstName: TextView
        var lastName: TextView

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        override fun onClick(v: View) {
            val clickedPosition = adapterPosition
            mOnClickListener.onListItemClick(clickedPosition)
        }

        init {
            icon =
                itemView.findViewById<View>(R.id.personIcon) as ImageView
            firstName = itemView.findViewById<View>(R.id.firstName) as TextView
            lastName = itemView.findViewById<View>(R.id.lastName) as TextView
            itemView.setOnClickListener(this)
        }
    }

    companion object {
        private val TAG = SelectorItemsAdapter::class.java.simpleName
    }

}
