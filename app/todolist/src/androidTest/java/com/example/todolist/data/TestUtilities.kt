package com.example.todolist.data

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import com.example.todolist.utils.PollingCheck


internal object TestUtilities {
    val testContentObserver: TestUtilities.TestContentObserver
        get() = TestUtilities.TestContentObserver.Companion.getTestContentObserver()

    /**
     * Students: The test functions for insert and delete use TestContentObserver to test
     * the ContentObserver callbacks using the PollingCheck class from the Android Compatibility
     * Test Suite tests.
     * NOTE: This only tests that the onChange function is called; it DOES NOT test that the
     * correct Uri is returned.
     */
    internal class TestContentObserver private constructor(val mHT: HandlerThread) :
        ContentObserver(Handler(mHT.looper)) {
        var mContentChanged = false

        /**
         * Called when a content change occurs.
         *
         *
         * To ensure correct operation on older versions of the framework that did not provide a
         * Uri argument, applications should also implement this method whenever they implement
         * the { #onChange(boolean, Uri)} overload.
         *
         * @param selfChange True if this is a self-change notification.
         */
        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        /**
         * Called when a content change occurs. Includes the changed content Uri when available.
         *
         * @param selfChange True if this is a self-change notification.
         * @param uri        The Uri of the changed content, or null if unknown.
         */
        override fun onChange(
            selfChange: Boolean,
            uri: Uri?
        ) {
            mContentChanged = true
        }

        /**
         * Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
         * It's useful to look at the Android CTS source for ideas on how to test your Android
         * applications. The reason that PollingCheck works is that, by default, the JUnit testing
         * framework is not running on the main Android application thread.
         */
        fun waitForNotificationOrFail() {
            object : PollingCheck(5000) {
                 override fun check(): Boolean {
                    return mContentChanged
                }
            }.run()
            mHT.quit()
        }

        companion object {
            val testContentObserver: TestUtilities.TestContentObserver
                get() {
                    val ht = HandlerThread("ContentObserverThread")
                    ht.start()
                    return TestUtilities.TestContentObserver(ht)
                }
        }

    }
}
