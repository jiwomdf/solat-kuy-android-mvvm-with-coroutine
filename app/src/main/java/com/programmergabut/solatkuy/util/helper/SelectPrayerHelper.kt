package com.programmergabut.solatkuy.util.helper

import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.text.SimpleDateFormat

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

        /* fun selectNextPrayerToLocalPrayer(selList: MutableList<NotifiedPrayer>): NotifiedPrayer? {

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
                6 -> NotifiedPrayer(
                    5,
                    EnumConfig.isha,
                    true,
                    strIshaTime
                )
                1 -> NotifiedPrayer(
                    1,
                    EnumConfig.fajr,
                    true,
                    strFajrTime
                )
                2 -> NotifiedPrayer(
                    2,
                    EnumConfig.dhuhr,
                    true,
                    strDhuhrTime
                )
                3 -> NotifiedPrayer(
                    3,
                    EnumConfig.asr,
                    true,
                    strAsrTime
                )
                4 -> NotifiedPrayer(
                    4,
                    EnumConfig.maghrib,
                    true,
                    strMaghribTime
                )
                5 -> NotifiedPrayer(
                    5,
                    EnumConfig.isha,
                    true,
                    strIshaTime
                )
                else -> null
            }

        } */

        /* fun selNextPrayerByLastID(listData: MutableList<NotifiedPrayer>, selID: Int): NotifiedPrayer? {

            listData.sortBy { x -> x.prayerID }

            val firstID = listData[0].prayerID
            val lastID = listData[listData.count() - 1].prayerID

            var nextID = selID + 1

            if(nextID > lastID)
                nextID = firstID

            return listData.find { x -> x.prayerID == nextID }
        } */
    }

}