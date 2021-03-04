package com.example.boardingpass

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

/**
 * BoardingPassInfo is a simple POJO that contains information about, you guessed it, a boarding
 * pass! Normally, it is best practice in Java to declare member variables as private and provide
 * getters, but we are leaving these fields public for ease of use.
 */
class BoardingPassInfo {
    var passengerName: String? = null
    var flightCode: String? = null
    var originCode: String? = null
    var destCode: String? = null
    var boardingTime: Timestamp? = null
    var departureTime: Timestamp? = null
    var arrivalTime: Timestamp? = null
    var departureTerminal: String? = null
    var departureGate: String? = null
    var seatNumber: String? = null
    var barCodeImageResource = 0
    val minutesUntilBoarding: Long
        get() {
            val millisUntilBoarding =
                boardingTime!!.time - System.currentTimeMillis()
            return TimeUnit.MILLISECONDS.toMinutes(millisUntilBoarding)
        }
}