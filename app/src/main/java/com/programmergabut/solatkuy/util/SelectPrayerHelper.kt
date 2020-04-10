package com.programmergabut.solatkuy.util

import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.Timings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Period
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class SelectPrayerHelper {

    companion object{
        fun selectNextPrayerToInt(timings: Timings): Int {

            var prayer: Int = -1

            //prayer time
            val sdfPrayer = SimpleDateFormat("HH:mm", Locale.getDefault())
            val fajrTime = DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim()))
            val dhuhrTime =  DateTime(sdfPrayer.parse(timings.dhuhr.split(" ")[0].trim()))
            val asrTime =  DateTime(sdfPrayer.parse(timings.asr.split(" ")[0].trim()))
            val maghribTime =  DateTime(sdfPrayer.parse(timings.maghrib.split(" ")[0].trim()))
            val ishaTime =  DateTime(sdfPrayer.parse(timings.isha.split(" ")[0].trim()))

            //sunrise & next fajr time
            val sunriseTime =  DateTime(sdfPrayer.parse(timings.sunrise.split(" ")[0].trim()))
            val nextfajrTime = DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim())).plusDays(1)
            val zerozeroTime = DateTime(sdfPrayer.parse("00:00"))

            val nowTime = DateTime(sdfPrayer.parse(LocalTime.now().toString()))

            if(nowTime.isAfter(zerozeroTime) && nowTime.isBefore(fajrTime)) //--> isha time
                prayer = 6

            if(fajrTime.isBefore(nowTime) && nowTime.isBefore(sunriseTime)) //--> fajr time
                prayer = 1
            else if(dhuhrTime.isBefore(nowTime) && nowTime.isBefore(asrTime)) //--> dhuhr time
                prayer = 2
            else if(asrTime.isBefore(nowTime) && nowTime.isBefore(maghribTime)) //--> asr time
                prayer = 3
            else if(maghribTime.isBefore(nowTime) && nowTime.isBefore(ishaTime)) //--> maghrib time
                prayer = 4
            else if(ishaTime.isBefore(nowTime) && nowTime.isBefore(nextfajrTime)) //--> isha time
                prayer = 5

            return prayer
        }

        fun selectNextPrayerToLocalPrayer(selList: MutableList<PrayerLocal>): PrayerLocal? {

            var prayer: Int = -1

            val strFajrTime = selList[0].prayerTime
            val strDhuhrTime = selList[1].prayerTime
            val strAsrTime = selList[2].prayerTime
            val strMaghribTime = selList[3].prayerTime
            val strIshaTime = selList[4].prayerTime
            val strSunriseTime = selList[5].prayerTime

            //prayer time
            val sdfPrayer = SimpleDateFormat("HH:mm", Locale.getDefault())
            val fajrTime = DateTime(sdfPrayer.parse(strFajrTime.split(" ")[0].trim()))
            val dhuhrTime =  DateTime(sdfPrayer.parse(strDhuhrTime.split(" ")[0].trim()))
            val asrTime =  DateTime(sdfPrayer.parse(strAsrTime.split(" ")[0].trim()))
            val maghribTime =  DateTime(sdfPrayer.parse(strMaghribTime.split(" ")[0].trim()))
            val ishaTime =  DateTime(sdfPrayer.parse(strIshaTime.split(" ")[0].trim()))

            //sunrise & next fajr time
            val sunriseTime =  DateTime(sdfPrayer.parse(strSunriseTime.split(" ")[0].trim()))
            val nextfajrTime = DateTime(sdfPrayer.parse(strFajrTime.split(" ")[0].trim())).plusDays(1)
            val zerozeroTime = DateTime(sdfPrayer.parse("00:00"))

            val nowTime = DateTime(sdfPrayer.parse(LocalTime.now().toString()))

            if(nowTime.isAfter(zerozeroTime) && nowTime.isBefore(fajrTime)) //--> isha time
                prayer = 1 //fajr

            if(fajrTime.isBefore(nowTime) && nowTime.isBefore(sunriseTime)) //--> fajr time
                prayer = 2 //dhuhr
            else if(dhuhrTime.isBefore(nowTime) && nowTime.isBefore(asrTime)) //--> dhuhr time
                prayer = 3 //asr
            else if(asrTime.isBefore(nowTime) && nowTime.isBefore(maghribTime)) //--> asr time
                prayer = 4 //maghrib
            else if(maghribTime.isBefore(nowTime) && nowTime.isBefore(ishaTime)) //--> maghrib time
                prayer = 5 //isha
            else if(ishaTime.isBefore(nowTime) && nowTime.isBefore(nextfajrTime)) //--> isha time
                prayer = 1 //fajr

            return when(prayer){
                6 -> PrayerLocal(5, R.string.isha.toString(), true, strIshaTime)
                1 -> PrayerLocal(1, R.string.fajr.toString(), true, strFajrTime)
                2 -> PrayerLocal(2, R.string.dhuhr.toString(), true, strDhuhrTime)
                3 -> PrayerLocal(3, R.string.asr.toString(), true, strAsrTime)
                4 -> PrayerLocal(4, R.string.maghrib.toString(), true, strMaghribTime)
                5 -> PrayerLocal(5, R.string.isha.toString(), true, strIshaTime)
                else -> null
            }

        }
    }

}