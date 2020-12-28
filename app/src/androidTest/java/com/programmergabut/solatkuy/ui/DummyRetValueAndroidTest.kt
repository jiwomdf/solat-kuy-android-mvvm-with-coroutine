package com.programmergabut.solatkuy.ui

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.En
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.*
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Edition
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.EnumConfig

object DummyRetValueAndroidTest {

    /* Last update : 2 July 2020 */

    /* Remote */
    fun getNotifiedPrayer(): List<NotifiedPrayer> {
        val listNotifiedPrayer = mutableListOf<NotifiedPrayer>()

        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.FAJR,
                true,
                "04:29"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.DHUHR,
                true,
                "11:35"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.ASR,
                true,
                "14:56"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.MAGHRIB,
                true,
                "17:26"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.ISHA,
                true,
                "18:36"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.SUNRISE,
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
        val data = com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Data(listAyah,
            Edition("","","","","","",""),
            "", "", "", 0,0, ""
        )
        val response =  ReadSurahEnResponse()
        response.code = 0
        response.data = data
        response.status = ""

        return  response
    }

    fun surahArID_1(): ReadSurahArResponse{
        val listAyah = mutableListOf<com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah>()
        listAyah.add(com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah(
            0,0,0,0,0,0,0,"test","test",false, isLastRead = false
        ))
        val data = com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Data(listAyah,
            com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Edition(
                "","","","","","",""),
            "", "", "", 0,0, ""
        )
        val response = ReadSurahArResponse()
        response.code = 0
        response.data = data
        response.status = ""

        return response
    }

    fun fetchPrayerApi(): PrayerResponse{
        val listData = mutableListOf<com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data>()
        listData.add(
            Data(
            Date(Gregorian("","", Designation("", ""),"", Month("", 1), Weekday(""),""),
                Hijri("","", DesignationX("",""),"", listOf(), MonthX("","",1), WeekdayX("",""),""),
                "", ""),null,
            Timings("15:00", "12:00", "04:00", "05:00",
                "20:00", "18:00", "00:00", "06:00", "17:00"))
        )

        val response =  PrayerResponse()
        response.code = 0
        response.data = listData
        response.status = ""

        return response
    }

    fun fetchCompassApi(): CompassResponse{
        val response =  CompassResponse()
        response.code = 0
        response.data = com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.Data(0.0,0.0,0.0)
        response.status = ""

        return response
    }

    fun fetchAllSurah(): AllSurahResponse{
        val data = mutableListOf(
            com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data(
                "","","","",0,0,"")
        )
        val response = AllSurahResponse()
        response.code = 0
        response.data = data
        response.status = ""

        return response
    }

    /* Database */
    fun getMsApi1(): MsApi1 {
        return MsApi1(0, "-7.5633548", "110.8041806", "3","7","2020")
    }

    fun getListMsFavAyah(): MutableList<MsFavAyah> {
        return mutableListOf(MsFavAyah(1, 2,"","",""))
    }

    fun getListMsFavSurah(): MutableList<MsFavSurah> {
        return mutableListOf(
            MsFavSurah(1, "test","test")
        )
    }

    fun getFavSurahBySurahID(surahid: Int): MutableList<MsFavSurah> {
        return mutableListOf(
            MsFavSurah(1, "test","test")
        )
    }

    fun getMsSetting(): MsSetting {
        return MsSetting(1, true, isUsingDBQuotes = true)
    }

}