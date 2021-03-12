package com.example.visual_polish

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectorsActivity : AppCompatActivity(),
    SelectorItemsAdapter.ListItemClickListener {
    // recycler view and adapter
    var mRecyclerView: RecyclerView? = null
    var mAdapter: SelectorItemsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selectors_activity)

        // Reference the recycler view with a call to findViewById
        mRecyclerView = findViewById(R.id.mainRecyclerView) as RecyclerView

        // The linear layout manager will position list items in a vertical list
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = layoutManager


        // The adapter is responsible for displaying each item in the list
       // mAdapter = SelectorItemsAdapter(this)
        mRecyclerView!!.adapter = mAdapter
    }

    /**
     * This is where we receive our callback from the clicklistener in the adapter
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    override fun onListItemClick(clickedItemIndex: Int) {

        // In here, handle what happens when an item is clicked
        // In this case, I am just logging the index of the item clicked
        Log.v(
            TAG,
            "List item clicked at index: $clickedItemIndex"
        )
    }

    companion object {
        private val TAG = SelectorsActivity::class.java.simpleName
    }
}