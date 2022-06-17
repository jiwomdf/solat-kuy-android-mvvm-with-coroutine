package com.programmergabut.solatkuy.ui.main

import com.programmergabut.solatkuy.model.Timings
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.text.SimpleDateFormat
import java.util.*

object SelectPrayerHelper {
    fun selectNextPrayer(timings: Timings): Int {
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

        return when {
            midNight.isBefore(now) && now.isBefore(fajr) -> { // --> img_isha time
                6
            }
            fajr.isEqual(now) || (fajr.isBefore(now) && now.isBefore(sunrise)) -> { // --> img_fajr time
                1
            }
            dhuhr.isEqual(now) || (dhuhr.isBefore(now) && now.isBefore(asr)) -> { // --> img_dhuhr time
                2
            }
            asr.isEqual(now) || (asr.isBefore(now) && now.isBefore(maghrib)) -> { // --> img_asr time
                3
            }
            maghrib.isEqual(now) || (maghrib.isBefore(now) && now.isBefore(isha)) -> { // --> img_maghrib time
                4
            }
            isha.isEqual(now) || (isha.isBefore(now) && now.isBefore(nextfajr)) -> { // --> img_isha time
                5
            }
            else -> {
                -1
            }
        }
    }

    private fun parseTimingToDateTime(timings: String): DateTime {
        val formattedPrayerTime = timings.split(" ")[0].trim()
        val sdfPrayer = SimpleDateFormat("HH:mm", Locale.getDefault())
        return DateTime(sdfPrayer.parse(formattedPrayerTime))
    }
}