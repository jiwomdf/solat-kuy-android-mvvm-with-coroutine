package com.programmergabut.solatkuy

import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.En
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Date
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Edition
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.enumclass.EnumConfig

object DummyRetValue {

    /* Last update : 2 July 2020 */

    fun getNotifiedPrayer(): List<NotifiedPrayer> {
        val listNotifiedPrayer = mutableListOf<NotifiedPrayer>()

        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.fajr,
                true,
                "04:29"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.dhuhr,
                true,
                "11:35"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.asr,
                true,
                "14:56"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.maghrib,
                true,
                "17:26"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.isha,
                true,
                "18:36"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.sunrise,
                true,
                "05:00"
            )
        )


        return listNotifiedPrayer
    }

    fun fetchAsmaAlHusnaApi(): AsmaAlHusnaResponse {

        val list = mutableListOf<Data>()

        list.add(Data(En("test"),"test",0,"test"))

        return AsmaAlHusnaResponse(0, list, "testing")
    }

    fun surahEnID_1(): ReadSurahEnResponse{
        val listAyah = mutableListOf<Ayah>()
        listAyah.add(Ayah(0,0,0,0,0,0,0,"test"))
        return ReadSurahEnResponse(0,
            com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Data(listAyah,
                Edition("","","","","","",""),
                "", "", "", 0,0, ""
            )
            ,"")
    }

    fun fetchPrayerApi(): PrayerResponse{
        val listData = mutableListOf<com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data>()
        listData.add(com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data(
            Date(null, null, null, null),null,
            Timings("", "", "", "", "", "", "", "", "")))

        return PrayerResponse(0, listData,"testing")
    }

    fun fetchCompassApi(): CompassResponse{
        return CompassResponse(0,com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.Data(0.0,0.0,0.0),"testing")
    }

    fun getMsApi1(): MsApi1 {
        return MsApi1(0, "","","","", "")
    }

}