package com.example.webpages_map_sharing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_openweb)
    }

    /**
     * This method is called when the Open Website button is clicked. It will open the website
     * specified by the URL represented by the variable urlAsString using implicit Intents.
     *
     * @param v Button that was clicked.
     */
    fun onClickOpenWebpageButton(v: View?) {
        // COMPLETED (5) Create a String that contains a URL ( make sure it starts with http:// or https:// )
        val urlAsString = "http://www.udacity.com"

        // COMPLETED (6) Replace the Toast with a call to openWebPage, passing in the URL String from the previous step
        openWebPage(urlAsString)
    }

    /**
     * This method is called when the Open Location in Map button is clicked. It will open the
     * a map to the location represented by the variable addressString using implicit Intents.
     *
     * @param v Button that was clicked.
     */
    open fun onClickOpenAddressButton(v: View?): Unit {
        // COMPLETED (5) Store an address in a String
        val addressString = "1600 Amphitheatre Parkway, CA"

        // COMPLETED (6) Use Uri.Builder with the appropriate scheme and query to form the Uri for the address
        val builder = Uri.Builder()
        builder.scheme("geo")
            .path("0,0")
            .appendQueryParameter("q", addressString)
        val addressUri = builder.build()

        /*if (intent.resolveActivity(packageManager) != null){
            startActivity(intent);
        }*/
        // COMPLETED (7) Replace the Toast with a call to showMap, passing in the Uri from the previous step
        showMap(addressUri)
    }
    /**
     * This method is called when the Share Text Content button is clicked. It will simply share
     * the text contained within the String textThatYouWantToShare.
     *
     * @param v Button that was clicked.
     */
    open fun onClickShareTextButton(v: View?) {
        val textToShare = "Esto es una prueba de texto a compartir"

        // COMPLETED (6) Replace the Toast with shareText, passing in the String from step 5
        /* Send that text to our method that will share it. */
        shareText(textToShare)
    }

    /**
     * This is where you will create and fire off your own implicit Intent. Yours will be very
     * similar to what I've done above. You can view a list of implicit Intents on the Common
     * Intents page from the developer documentation.
     *
     * @see <http:></http:>//developer.android.com/guide/components/intents-common.html/>
     *
     *
     * @param v Button that was clicked.
     */
    fun createYourOwn(v: View?) {
        Toast.makeText(
            this,
            "TODO: Create Your Own Implicit Intent",
            Toast.LENGTH_SHORT
        )
            .show()
    }
    // COMPLETED (1) Create a method called openWebPage that accepts a String as a parameter
    /**
     * This method fires off an implicit Intent to open a webpage.
     *
     * @param url Url of webpage to open. Should start with http:// or https:// as that is the
     * scheme of the URI expected with this Intent according to the Common Intents page
     */
    private fun openWebPage(url: String) {
        // COMPLETED (2) Use Uri.parse to parse the String into a Uri
        /*
         * We wanted to demonstrate the Uri.parse method because its usage occurs frequently. You
         * could have just as easily passed in a Uri as the parameter of this method.
         */
        val webpage = Uri.parse(url)

        // COMPLETED (3) Create an Intent with Intent.ACTION_VIEW and the webpage Uri as parameters
        /*
         * Here, we create the Intent with the action of ACTION_VIEW. This action allows the user
         * to view particular content. In this case, our webpage URL.
         */
        val intent = Intent(Intent.ACTION_VIEW, webpage)

        // COMPLETED (4) Verify that this Intent can be launched and then call startActivity
        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        }
    }

    // COMPLETED (1) Create a method called showMap with a Uri as the single parameter
    /**
     * This method will fire off an implicit Intent to view a location on a map.
     *
     * When constructing implicit Intents, you can use either the setData method or specify the
     * URI as the second parameter of the Intent's constructor,
     * as I do in [.openWebPage]
     *
     * @param geoLocation The Uri representing the location that will be opened in the map
     */
    private fun showMap(geoLocation: Uri) {
        // COMPLETED (2) Create an Intent with action type, Intent.ACTION_VIEW
        /*
         * Again, we create an Intent with the action, ACTION_VIEW because we want to VIEW the
         * contents of this Uri.
         */
        val intent = Intent(Intent.ACTION_VIEW)

        // COMPLETED (3) Set the data of the Intent to the Uri passed into this method
        /*
         * Using setData to set the Uri of this Intent has the exact same affect as passing it in
         * the Intent's constructor. This is simply an alternate way of doing this.
         */intent.data = geoLocation


        // COMPLETED (4) Verify that this Intent can be launched and then call startActivity
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    // COMPLETED (1) Create a void method called shareText that accepts a String as a parameter
    // COMPLETED (1) Create a void method called shareText that accepts a String as a parameter
    /**
     * This method shares text and allows the user to select which app they would like to use to
     * share the text. Using ShareCompat's IntentBuilder, we get some really cool functionality for
     * free. The chooser that is started using the [IntentBuilder.startChooser] method will
     * create a chooser when more than one app on the device can handle the Intent. This happens
     * when the user has, for example, both a texting app and an email app. If only one Activity
     * on the phone can handle the Intent, it will automatically be launched.
     *
     * @param textToShare Text that will be shared
     */
    private fun shareText(textToShare: String) {
        // COMPLETED (2) Create a String variable called mimeType and set it to "text/plain"
        /*
         * You can think of MIME types similarly to file extensions. They aren't the exact same,
         * but MIME types help a computer determine which applications can open which content. For
         * example, if you double click on a .pdf file, you will be presented with a list of
         * programs that can open PDFs. Specifying the MIME type as text/plain has a similar affect
         * on our implicit Intent. With text/plain specified, all apps that can handle text content
         * in some way will be offered when we call startActivity on this particular Intent.
         */
        val mimeType = "text/plain"

        // COMPLETED (3) Create a title for the chooser window that will pop up
        /* This is just the title of the window that will pop up when we call startActivity */
        val title = "Learning How to Share"

        // COMPLETED (4) Use ShareCompat.IntentBuilder to build the Intent and start the chooser
        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */
        ShareCompat.IntentBuilder /* The from method specifies the Context from which this share is coming from */
            .from(this)
            .setType(mimeType)
            .setChooserTitle(title)
            .setText(textToShare)
            .startChooser()
    }
}