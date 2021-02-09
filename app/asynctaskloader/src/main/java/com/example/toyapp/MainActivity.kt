package com.example.toyapp

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import com.example.android.asynctaskloader.utilities.NetworkUtils
import utilities.NetworkUtils
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<String?> {
    private var mSearchBoxEditText: EditText? = null
    private var mUrlDisplayTextView: TextView? = null
    private var mSearchResultsTextView: TextView? = null
    private var mErrorMessageDisplay: TextView? = null
    private var mLoadingIndicator: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSearchBoxEditText = findViewById(R.id.et_search_box) as EditText?
        mUrlDisplayTextView = findViewById(R.id.tv_url_display) as TextView?
        mSearchResultsTextView = findViewById(R.id.tv_github_search_results_json) as TextView?
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display) as TextView?
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator) as ProgressBar?
        if (savedInstanceState != null) {
            val queryUrl =
                savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA)
            mUrlDisplayTextView!!.text = queryUrl
        }

        /*
         * Initialize the loader
         */getSupportLoaderManager().initLoader(
            GITHUB_SEARCH_LOADER,
            null,
            this
        )
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using [NetworkUtils]) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally request that an AsyncTaskLoader performs the GET request.
     */
    private fun makeGithubSearchQuery() {
        val githubQuery = mSearchBoxEditText!!.text.toString()

        /*
         * If the user didn't enter anything, there's nothing to search for. In the case where no
         * search text was entered but the search button was clicked, we will display a message
         * stating that there is nothing to search for and we will not attempt to load anything.
         *
         * If there is text entered in the search box when the search button was clicked, we will
         * create the URL that will return our Github search results, display that URL, and then
         * pass that URL to the Loader. The reason we pass the URL as a String is simply a matter
         * of convenience. There are other ways of achieving this same result, but we felt this
         * was the simplest.
         */if (TextUtils.isEmpty(githubQuery)) {
            mUrlDisplayTextView!!.text = "No query entered, nothing to search for."
            return
        }
        val githubSearchUrl: URL = NetworkUtils.buildUrl(githubQuery)
        mUrlDisplayTextView!!.text = githubSearchUrl.toString()
        val queryBundle = Bundle()
        queryBundle.putString(
            SEARCH_QUERY_URL_EXTRA,
            githubSearchUrl.toString()
        )

        /*
         * Now that we've created our bundle that we will pass to our Loader, we need to decide
         * if we should restart the loader (if the loader already existed) or if we need to
         * initialize the loader (if the loader did NOT already exist).
         *
         * We do this by first store the support loader manager in the variable loaderManager.
         * All things related to the Loader go through through the LoaderManager. Once we have a
         * hold on the support loader manager, (loaderManager) we can attempt to access our
         * githubSearchLoader. To do this, we use LoaderManager's method, "getLoader", and pass in
         * the ID we assigned in its creation. You can think of this process similar to finding a
         * View by ID. We give the LoaderManager an ID and it returns a loader (if one exists). If
         * one doesn't exist, we tell the LoaderManager to create one. If one does exist, we tell
         * the LoaderManager to restart it.
         */
        val loaderManager: LoaderManager = getSupportLoaderManager()
        val githubSearchLoader: Loader<String> =
            loaderManager.getLoader(GITHUB_SEARCH_LOADER)!!
        if (githubSearchLoader == null) {
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this)
        } else {
            loaderManager.restartLoader(
                GITHUB_SEARCH_LOADER,
                queryBundle,
                this
            )
        }
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private fun showJsonDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay!!.visibility = View.INVISIBLE
        /* Then, make sure the JSON data is visible */mSearchResultsTextView!!.visibility =
            View.VISIBLE
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private fun showErrorMessage() {
        /* First, hide the currently visible data */
        mSearchResultsTextView!!.visibility = View.INVISIBLE
        /* Then, show the error */mErrorMessageDisplay!!.visibility = View.VISIBLE
    }

   override fun onCreateLoader(id: Int, args: Bundle?): Loader<String?> {
        return object : AsyncTaskLoader<String?>(this) {
            // TODO (1) Create a String member variable called mGithubJson that will store the raw JSON
             override fun onStartLoading() {

                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) {
                    return
                }

                // TODO (2) If mGithubJson is not null, deliver that result. Otherwise, force a load

                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */mLoadingIndicator!!.visibility = View.VISIBLE
                forceLoad()
            }

            override fun loadInBackground(): String? {

                /* Extract the search query from the args using our constant */
                val searchQueryUrlString =
                    args!!.getString(SEARCH_QUERY_URL_EXTRA)

                /* If the user didn't enter anything, there's nothing to search for */return if (TextUtils.isEmpty(
                        searchQueryUrlString
                    )
                ) {
                    null
                } else try {
                    val githubUrl = URL(searchQueryUrlString)
                    NetworkUtils.getResponseFromHttpUrl(githubUrl)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                /* Parse the URL from the passed in String and perform the search */
            } // TODO (3) Override deliverResult and store the data in mGithubJson
            // TODO (4) Call super.deliverResult after storing the data
        }
    }

     override fun onLoadFinished(loader: Loader<String?>, data: String?) {

        /* When we finish loading, we want to hide the loading indicator from the user. */
        mLoadingIndicator!!.visibility = View.INVISIBLE
        /*
         * If the results are null, we assume an error has occurred. There are much more robust
         * methods for checking errors, but we wanted to keep this particular example simple.
         */if (null == data) {
            showErrorMessage()
        } else {
            mSearchResultsTextView!!.text = data
            showJsonDataView()
        }
    }

    override fun onLoaderReset(loader: Loader<String?>) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemThatWasClickedId = item.itemId
        if (itemThatWasClickedId == R.id.action_search) {
            makeGithubSearchQuery()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val queryUrl = mUrlDisplayTextView!!.text.toString()
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl)
    }

    companion object {
        /* A constant to save and restore the URL that is being displayed */
        private const val SEARCH_QUERY_URL_EXTRA = "query"

        /*
     * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
     * to any number you like, as long as you use the same variable name.
     */
        private const val GITHUB_SEARCH_LOADER = 22
    }
}