package utilities

import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * These utilities will be used to communicate with the network.
 */
class NetworkUtils {
     val GITHUB_BASE_URL = "https://api.github.com/search/repositories"
     val PARAM_QUERY = "q"

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
     val PARAM_SORT = "sort"
     val sortBy = "stars"

    /**
     * Builds the URL used to query Github.
     *
     * @param githubSearchQuery The keyword that will be queried for.
     * @return The URL to use to query the weather server.
     */
    fun buildUrl(githubSearchQuery: String?): URL? {
        val builtUri =
            Uri.parse(NetworkUtils.GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(NetworkUtils.PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(NetworkUtils.PARAM_SORT, NetworkUtils.sortBy)
                .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection =
            url.openConnection() as HttpURLConnection
        return try {
            val `in` = urlConnection.inputStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}