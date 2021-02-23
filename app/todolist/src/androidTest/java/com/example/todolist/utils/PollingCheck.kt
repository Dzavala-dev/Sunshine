package com.example.todolist.utils

import junit.framework.Assert
import java.util.concurrent.Callable


abstract class PollingCheck {
    private var mTimeout: Long = 3000

    constructor() {}
    constructor(timeout: Long) {
        mTimeout = timeout
    }

    protected abstract fun check(): Boolean
    fun run() {
        if (check()) {
            return
        }
        var timeout = mTimeout
        while (timeout > 0) {
            try {
                Thread.sleep(PollingCheck.Companion.TIME_SLICE)
            } catch (e: InterruptedException) {
                Assert.fail("Notification error, unexpected InterruptedException")
            }
            if (check()) {
                return
            }
            timeout -= PollingCheck.Companion.TIME_SLICE
        }
        Assert.fail("Notification not set, unexpected timeout")
    }

    companion object {
        private const val TIME_SLICE: Long = 50

        @Throws(Exception::class)
        fun check(
            message: CharSequence,
            timeout: Long,
            condition: Callable<Boolean>
        ) {
            var timeout = timeout
            while (timeout > 0) {
                if (condition.call()) {
                    return
                }
                Thread.sleep(PollingCheck.Companion.TIME_SLICE)
                timeout -= PollingCheck.Companion.TIME_SLICE
            }
            Assert.fail(message.toString())
        }
    }
}