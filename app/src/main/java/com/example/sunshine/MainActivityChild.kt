package com.example.sunshine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivityChild : AppCompatActivity() {
    /* Fields that will store our EditText and Button */
    private var mNameEntry: EditText? = null
    private var mDoSomethingCoolButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_child)

        /*
         * Using findViewById, we get a reference to our Button from xml. This allows us to
         * do things like set the onClickListener which determines what happens when the button
         * is clicked.
         */mDoSomethingCoolButton = findViewById(R.id.b_do_something_cool) as Button
        mNameEntry = findViewById(R.id.et_text_entry) as EditText

        /* Setting an OnClickListener allows us to do something when this button is clicked. */mDoSomethingCoolButton!!.setOnClickListener { // COMPLETED (1) Retrieve the text from the EditText and store it in a variable
            /* We'll first get the text entered by the user in the EditText */
            val textEntered = mNameEntry!!.text.toString()

            /*
                             * Storing the Context in a variable in this case is redundant since we could have
                             * just used "this" or "MainActivity.this" in the method call below. However, we
                             * wanted to demonstrate what parameter we were using "MainActivity.this" for as
                             * clear as possible.
                             */
            val context: Context = this@MainActivityChild

            /* This is the class that we want to start (and open) when the button is clicked. */
            val destinationActivity: Class<*> =
                ChildActivity::class.java

            /*
                             * Here, we create the Intent that will start the Activity we specified above in
                             * the destinationActivity variable. The constructor for an Intent also requires a
                             * context, which we stored in the variable named "context".
                             */
            val startChildActivityIntent = Intent(context, destinationActivity)

            // COMPLETED (2) Use the putExtra method to put the String from the EditText in the Intent
            /*
                             * We use the putExtra method of the Intent class to pass some extra stuff to the
                             * Activity that we are starting. Generally, this data is quite simple, such as
                             * a String or a number. However, there are ways to pass more complex objects.
                             */startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, textEntered)

            /*
                             * Once the Intent has been created, we can use Activity's method, "startActivity"
                             * to start the ChildActivity.
                             */startActivity(startChildActivityIntent)
        }
    }
}