package com.example.boardingpass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.boardingpass.databinding.ActivityMainBinding
import com.example.boardingpass.utilities.FakeDataUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    var mBinding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*
         * DataBindUtil.setContentView replaces our normal call of setContent view.
         * DataBindingUtil also created our ActivityMainBinding that we will eventually use to
         * display all of our data.
         */mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val fakeBoardingInfo = FakeDataUtils.generateFakeBoardingPassInfo()
        displayBoardingPassInfo(fakeBoardingInfo)
    }

    private fun displayBoardingPassInfo(info: BoardingPassInfo) {
        mBinding!!.textViewPassengerName.text = info.passengerName
        // COMPLETED (7) Use the flightInfo attribute in mBinding below to get the appropriate text Views
        mBinding!!.flightInfo.textViewOriginAirport.text = info.originCode
        mBinding!!.flightInfo.textViewFlightCode.text = info.flightCode
        mBinding!!.flightInfo.textViewDestinationAirport.text = info.destCode
        val formatter = SimpleDateFormat(
            getString(R.string.timeFormat),
            Locale.getDefault()
        )
        val boardingTime = formatter.format(info.boardingTime)
        val departureTime = formatter.format(info.departureTime)
        val arrivalTime = formatter.format(info.arrivalTime)
        mBinding!!.textViewBoardingTime.text = boardingTime
        mBinding!!.textViewDepartureTime.text = departureTime
        mBinding!!.textViewArrivalTime.text = arrivalTime
        val totalMinutesUntilBoarding: Long = info.minutesUntilBoarding
        val hoursUntilBoarding =
            TimeUnit.MINUTES.toHours(totalMinutesUntilBoarding)
        val minutesLessHoursUntilBoarding =
            totalMinutesUntilBoarding - TimeUnit.HOURS.toMinutes(
                hoursUntilBoarding
            )
        val hoursAndMinutesUntilBoarding = getString(
            R.string.countDownFormat,
            hoursUntilBoarding,
            minutesLessHoursUntilBoarding
        )
        mBinding!!.textViewBoardingInCountdown.text = hoursAndMinutesUntilBoarding
        // COMPLETED (8) Use the boardingInfo attribute in mBinding below to get the appropriate text Views
        mBinding!!.boardingInfo.textViewTerminal.text = info.departureTerminal
        mBinding!!.boardingInfo.textViewGate.text = info.departureGate
        mBinding!!.boardingInfo.textViewSeat.text = info.seatNumber
    }
}

