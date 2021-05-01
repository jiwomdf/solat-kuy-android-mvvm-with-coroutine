package com.programmergabut.solatkuy.ui.main

import com.programmergabut.solatkuy.data.local.localentity.MsTimings
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.text.SimpleDateFormat
import java.util.*

class SelectPrayerHelper {
    companion object {
        fun selectNextPrayer(timings: MsTimings): Int {
            var prayer: Int = -1

            //prayer time
            val fajr = parseTimingToDateTime(timings.fajr)
            val dhuhr = parseTimingToDateTime(timings.dhuhr)
            val asr = parseTimingToDateTime(timings.asr)
            val maghrib = parseTimingToDateTime(timings.maghrib)
            val isha = parseTimingToDateTime(timings.isha)

            //sunrise & nextFajr time
            val sunrise = parseTimingToDateTime(timings.sunrise)
            val nextfajr = parseTimingToDateTime(timings.fajr).plusDays(1)
            val midNight = parseTimingToDateTime("00:00")
            val now = parseTimingToDateTime(LocalTime.now().toString())

            if (midNight.isBefore(now) && now.isBefore(fajr)) //--> img_isha time
                prayer = 6
            else if (fajr.isEqual(now) || (fajr.isBefore(now) && now.isBefore(sunrise))) //--> img_fajr time
                prayer = 1
            else if (dhuhr.isEqual(now) || (dhuhr.isBefore(now) && now.isBefore(asr))) //--> img_dhuhr time
                prayer = 2
            else if (asr.isEqual(now) || (asr.isBefore(now) && now.isBefore(maghrib))) //--> img_asr time
                prayer = 3
            else if (maghrib.isEqual(now) || (maghrib.isBefore(now) && now.isBefore(isha))) //--> img_maghrib time
                prayer = 4
            else if (isha.isEqual(now) || (isha.isBefore(now) && now.isBefore(nextfajr))) //--> img_isha time
                prayer = 5

            return prayer
        }

        private fun parseTimingToDateTime(timings: String): DateTime {
            val formattedPrayerTime = timings.split(" ")[0].trim()
            val sdfPrayer = SimpleDateFormat("HH:mm", Locale.getDefault())
            return DateTime(sdfPrayer.parse(formattedPrayerTime))
        }
    }
}