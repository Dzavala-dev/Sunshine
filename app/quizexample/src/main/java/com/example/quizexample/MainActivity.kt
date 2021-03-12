package com.example.quizexample

import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.udacity.example.quizexample.R

/**
 * Gets the data from the ContentProvider and shows a series of flash cards.
 */
class MainActivity : AppCompatActivity() {
    // The data from the DroidTermsExample content provider
    private var mData: Cursor? = null

    // The current state of the app
    private var mCurrentState = 0

    // The index of the definition and word column in the cursor
    private var mDefCol = 0
    private var mWordCol = 0
    private var mWordTextView: TextView? = null
    private var mDefinitionTextView: TextView? = null
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
        // COMPLETED (1) You'll probably want more than just the Button
        mWordTextView = findViewById(R.id.text_view_word) as TextView
        mDefinitionTextView = findViewById(R.id.text_view_definition) as TextView
        mButton = findViewById(R.id.button_next) as Button

        //Run the database operation to get the cursor off of the main thread
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
        // COMPLETED (3) Go to the next word in the Cursor, show the next word and hide the definition
        // Note that you shouldn't try to do this if the cursor hasn't been set yet.
        // If you reach the end of the list of words, you should start at the beginning again.
        if (mData != null) {
            // Move to the next position in the cursor, if there isn't one, move to the first
            if (!mData!!.moveToNext()) {
                mData!!.moveToFirst()
            }
            // Hide the definition TextView
            mDefinitionTextView!!.visibility = View.INVISIBLE

            // Change button text
            mButton!!.text = getString(R.string.show_definition)

            // Get the next word
            mWordTextView!!.text = mData!!.getString(mWordCol)
            mDefinitionTextView!!.text = mData!!.getString(mDefCol)
            mCurrentState = STATE_HIDDEN
        }
    }

    fun showDefinition() {
        // COMPLETED (4) Show the definition
        if (mData != null) {
            // Show the definition TextView
            mDefinitionTextView!!.visibility = View.VISIBLE

            // Change button text
            mButton!!.text = getString(R.string.next_word)
            mCurrentState = STATE_SHOWN
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // COMPLETED (5) Remember to close your cursor!
        mData!!.close()
    }

    // Use an async task to do the data fetch off of the main thread.
    inner class WordFetchTask :
        AsyncTask<Void?, Void?, Cursor?>() {
        // Invoked on a background thread
        protected override fun doInBackground(vararg p0: Void?): Cursor? {
            // Make the query to get the data

            // Get the content resolver
            val resolver = contentResolver

            // Call the query method on the resolver with the correct Uri from the contract class
            return resolver.query(
                DroidTermsExampleContract.CONTENT_URI,
                null, null, null, null
            )
        }

        // Invoked on UI thread
        override fun onPostExecute(cursor: Cursor?) {
            super.onPostExecute(cursor)
            // COMPLETED (2) Initialize anything that you need the cursor for, such as setting up
            // the screen with the first word and setting any other instance variables

            //Set up a bunch of instance variables based off of the data

            // Set the data for MainActivity
            mData = cursor
            // Get the column index, in the Cursor, of each piece of data
            mDefCol = mData!!.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION)
            mWordCol = mData!!.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD)

            // Set the initial state
            nextWord()
        }
    }
}
