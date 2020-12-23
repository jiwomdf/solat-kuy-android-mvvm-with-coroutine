package com.programmergabut.solatkuy.util.helper

import com.programmergabut.solatkuy.data.local.localentity.MsTimings
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.text.SimpleDateFormat
import java.util.*

class SelectPrayerHelper {

    companion object{
        fun selectNextPrayerToInt(timings: MsTimings): Int {

            var prayer: Int = -1

            //prayer time
            val sdfPrayer = SimpleDateFormat("HH:mm", Locale.getDefault())
            val fajr = DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim()))
            val dhuhr =  DateTime(sdfPrayer.parse(timings.dhuhr.split(" ")[0].trim()))
            val asr =  DateTime(sdfPrayer.parse(timings.asr.split(" ")[0].trim()))
            val maghrib =  DateTime(sdfPrayer.parse(timings.maghrib.split(" ")[0].trim()))
            val isha =  DateTime(sdfPrayer.parse(timings.isha.split(" ")[0].trim()))

            //sunrise & nextFajr time
            val sunrise =  DateTime(sdfPrayer.parse(timings.sunrise.split(" ")[0].trim()))
            val nextfajr = DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim())).plusDays(1)
            val midNight = DateTime(sdfPrayer.parse("00:00"))

            val now = DateTime(sdfPrayer.parse(LocalTime.now().toString()))

            if(midNight.isBefore(now) && now.isBefore(fajr)) //--> img_isha time
                prayer = 6
            else if(fajr.isEqual(now) || (fajr.isBefore(now) && now.isBefore(sunrise))) //--> img_fajr time
                prayer = 1
            else if(dhuhr.isEqual(now) || (dhuhr.isBefore(now) && now.isBefore(asr))) //--> img_dhuhr time
                prayer = 2
            else if(asr.isEqual(now) || (asr.isBefore(now) && now.isBefore(maghrib))) //--> img_asr time
                prayer = 3
            else if(maghrib.isEqual(now) || (maghrib.isBefore(now) && now.isBefore(isha))) //--> img_maghrib time
                prayer = 4
            else if(isha.isEqual(now) || (isha.isBefore(now) && now.isBefore(nextfajr))) //--> img_isha time
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

            //img_sunrise & next img_fajr time
            val sunriseTime =  DateTime(sdfPrayer.parse(strSunriseTime.split(" ")[0].trim()))
            val nextfajrTime = DateTime(sdfPrayer.parse(strFajrTime.split(" ")[0].trim())).plusDays(1)
            val zerozeroTime = DateTime(sdfPrayer.parse("00:00"))

            val nowTime = DateTime(sdfPrayer.parse(LocalTime.now().toString()))

            if(nowTime.isAfter(zerozeroTime) && nowTime.isBefore(fajrTime)) //--> img_isha time
                prayer = 1 //img_fajr

            if(fajrTime.isBefore(nowTime) && nowTime.isBefore(sunriseTime)) //--> img_fajr time
                prayer = 2 //img_dhuhr
            else if(dhuhrTime.isBefore(nowTime) && nowTime.isBefore(asrTime)) //--> img_dhuhr time
                prayer = 3 //img_asr
            else if(asrTime.isBefore(nowTime) && nowTime.isBefore(maghribTime)) //--> img_asr time
                prayer = 4 //img_maghrib
            else if(maghribTime.isBefore(nowTime) && nowTime.isBefore(ishaTime)) //--> img_maghrib time
                prayer = 5 //img_isha
            else if(ishaTime.isBefore(nowTime) && nowTime.isBefore(nextfajrTime)) //--> img_isha time
                prayer = 1 //img_fajr

            return when(prayer){
                6 -> NotifiedPrayer(
                    5,
                    EnumConfig.img_isha,
                    true,
                    strIshaTime
                )
                1 -> NotifiedPrayer(
                    1,
                    EnumConfig.img_fajr,
                    true,
                    strFajrTime
                )
                2 -> NotifiedPrayer(
                    2,
                    EnumConfig.img_dhuhr,
                    true,
                    strDhuhrTime
                )
                3 -> NotifiedPrayer(
                    3,
                    EnumConfig.img_asr,
                    true,
                    strAsrTime
                )
                4 -> NotifiedPrayer(
                    4,
                    EnumConfig.img_maghrib,
                    true,
                    strMaghribTime
                )
                5 -> NotifiedPrayer(
                    5,
                    EnumConfig.img_isha,
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