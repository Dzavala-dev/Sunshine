package com.example.quizexample

import android.content.ContentResolver
import androidx.appcompat.app.AppCompatActivity
import android.database.Cursor
import android.media.tv.TvContract.Channels.CONTENT_URI
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi


/**
 * Gets the data from the ContentProvider and shows a series of flash cards.
 */
class MainActivity : AppCompatActivity() {
    // COMPLETED (3) Create an instance variable storing a Cursor called mData
    // The data from the DroidTermsExample content provider
    private var mData: Cursor? = null

    // The current state of the app
    private var mCurrentState = 0
    private var mButton: Button? = null

    // This state is when the word definition is hidden and clicking the button will therefore
    // show the definition
    private val STATE_HIDDEN = 0

    // This state is when the word definition is shown and clicking the button will therefore
    // advance the app to the next word
    private val STATE_SHOWN = 1
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the views
        mButton = findViewById(R.id.button_next)

        //Run the database operation to get the cursor off of the main thread
        // COMPLETED (5) Create and execute your AsyncTask here
        WordFetchTask().execute()
    }

    /**
     * This is called from the layout when the button is clicked and switches between the
     * two app states.
     * @param view The view that was clicked
     */
    fun onButtonClick(view: View?) {

        // Either show the definition of the current word, or if the definition is currently
        // showing, move to the next word.
        when (mCurrentState) {
            STATE_HIDDEN -> showDefinition()
            STATE_SHOWN -> nextWord()
        }
    }

    fun nextWord() {

        // Change button text
        mButton?.text = getString(R.string.show_definition)
        mCurrentState = STATE_HIDDEN
    }

    fun showDefinition() {

        // Change button text
        mButton?.text = getString(R.string.next_word)
        mCurrentState = STATE_SHOWN
    }

    // COMPLETED (1) Create AsyncTask with the following generic types <Void, Void, Cursor>
    // COMPLETED (2) In the doInBackground method, write the code to access the DroidTermsExample
    // provider and return the Cursor object
    // COMPLETED (4) In the onPostExecute method, store the Cursor object in mData
    // Use an async task to do the data fetch off of the main thread.
    inner class WordFetchTask :
        AsyncTask<Void?, Void?, Cursor?>() {
        // Invoked on a background thread
         @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
         override fun doInBackground(vararg p0: Void?): Cursor? {
            // Make the query to get the data

            // Get the content resolver
            val resolver: ContentResolver = getContentResolver()

            // Call the query method on the resolver with the correct Uri from the contract class
            return resolver.query(
                CONTENT_URI,
                null, null, null, null
            )
        }

        // Invoked on UI thread
        override fun onPostExecute(cursor: Cursor?) {
            super.onPostExecute(cursor)

            // Set the data for MainActivity
            mData = cursor
        }
    }
}

