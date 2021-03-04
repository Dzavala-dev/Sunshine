package com.example.boardingpass.utilities

import com.example.boardingpass.BoardingPassInfo
import com.example.boardingpass.R
import java.sql.Timestamp
import java.util.concurrent.TimeUnit

/**
 * This class is used to generate fake data that will be displayed in the boarding pass layout
 */
object FakeDataUtils {
    /**
     * Generates fake boarding pass data to be displayed.
     * @return fake boarding pass data
     */
    fun generateFakeBoardingPassInfo(): BoardingPassInfo {
        val bpi = BoardingPassInfo()
        bpi.passengerName = "MR. RANDOM PERSON"
        bpi.flightCode = "UD 777"
        bpi.originCode = "JFK"
        bpi.destCode = "DCA"
        val now = System.currentTimeMillis()

        // Anything from 0 minutes up to (but not including) 30 minutes
        val randomMinutesUntilBoarding = (Math.random() * 30).toLong()
        // Standard 40 minute boarding time
        val totalBoardingMinutes: Long = 40
        // Anything from 1 hours up to (but not including) 6 hours
        val randomFlightLengthHours = (Math.random() * 5 + 1).toLong()
        val boardingMillis: Long =
            now + FakeDataUtils.minutesToMillis(randomMinutesUntilBoarding)
        val departure: Long =
            boardingMillis + FakeDataUtils.minutesToMillis(totalBoardingMinutes)
        val arrival: Long = departure + FakeDataUtils.hoursToMillis(randomFlightLengthHours)
        bpi.boardingTime = Timestamp(boardingMillis)
        bpi.departureTime = Timestamp(departure)
        bpi.arrivalTime = Timestamp(arrival)
        bpi.departureTerminal = "3A"
        bpi.departureGate = "33C"
        bpi.seatNumber = "1A"
        bpi.barCodeImageResource = R.drawable.art_plane
        return bpi
    }

    private fun minutesToMillis(minutes: Long): Long {
        return TimeUnit.MINUTES.toMillis(minutes)
    }

    private fun hoursToMillis(hours: Long): Long {
        return TimeUnit.HOURS.toMillis(hours)
    }
}