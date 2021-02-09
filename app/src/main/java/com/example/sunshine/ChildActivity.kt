package com.example.sunshine

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChildActivity : AppCompatActivity() {


    /* Field to store our TextView */
    private var mDisplayText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_child)

        /* Typical usage of findViewById... */mDisplayText =
            findViewById(R.id.tv_display) as TextView

        // COMPLETED (3) Use the getIntent method to store the Intent that started this Activity in a variable
        /*
         * Here is where all the magic happens. The getIntent method will give us the Intent that
         * started this particular Activity.
         */
        val intentThatStartedThisActivity = intent

        // COMPLETED (4) Create an if statement to check if this Intent has the extra we passed from MainActivity
        /*
         * Although there is always an Intent that starts any particular Activity, we can't
         * guarantee that the extra we are looking for was passed as well. Because of that, we need
         * to check to see if the Intent has the extra that we specified when we created the
         * Intent that we use to start this Activity. Note that this extra may not be present in
         * the Intent if this Activity was started by any other method.
         * */if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            // COMPLETED (5) If the Intent contains the correct extra, retrieve the text
            /*
             * Now that we've checked to make sure the extra we are looking for is contained within
             * the Intent, we can extract the extra. To do that, we simply call the getStringExtra
             * method on the Intent. There are various other get*Extra methods you can call for
             * different types of data. Please feel free to explore those yourself.
             */
            val textEntered = intentThatStartedThisActivity.getStringExtra(
                Intent.EXTRA_TEXT
            )

            // COMPLETED (6) If the Intent contains the correct extra, use it to set the TextView text
            /*
             * Finally, we can set the text of our TextView (using setText) to the text that was
             * passed to this Activity.
             */mDisplayText!!.text = textEntered
        }
    }
}