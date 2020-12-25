package com.programmergabut.solatkuy

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.En
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Date
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Edition
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource

object DummyRetValue {

    /* Last update : 2 July 2020 */

    /* Remote */
    fun getNotifiedPrayer(): List<NotifiedPrayer> {
        val listNotifiedPrayer = mutableListOf<NotifiedPrayer>()

        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfigTesting.FAJR,
                true,
                EnumConfigTesting.FAJR_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfigTesting.DHUHR,
                true,
                EnumConfigTesting.DHUHR_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfigTesting.ASR,
                true,
                EnumConfigTesting.ASR_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfigTesting.MAGHRIB,
                true,
                EnumConfigTesting.MAGHRIB_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfigTesting.ISHA,
                true,
                EnumConfigTesting.ISHA_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfigTesting.SUNRISE,
                true,
                EnumConfigTesting.SUNRISE_TIME
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

    fun surahArID_1(): ReadSurahArResponse{
        val listAyah = mutableListOf<com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah>()
        listAyah.add(com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah(
            0,0,0,0,0,0,0,"test","test",false, isLastRead = false
        ))
        return ReadSurahArResponse(0,
            com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Data(listAyah,
                com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Edition(
                    "","","","","","",""),
                "", "", "", 0,0, ""
            )
            ,
            "")
    }

    fun fetchPrayerApi(): PrayerResponse{
        val listData = mutableListOf<com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data>()
        listData.add(com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data(
            Date(null, null, null, null),null,
            Timings(EnumConfigTesting.ASR_TIME, EnumConfigTesting.DHUHR_TIME,
                EnumConfigTesting.FAJR_TIME, EnumConfigTesting.IMSAK_TIME, EnumConfigTesting.ISHA_TIME,
                EnumConfigTesting.MAGHRIB_TIME, EnumConfigTesting.MID_NIGHT_TIME, EnumConfigTesting.SUNRISE_TIME, EnumConfigTesting.SUNSET_TIME)))

        return PrayerResponse(0, listData,"testing")
    }

    fun fetchCompassApi(): CompassResponse{
        return CompassResponse(0,com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.Data(0.0,0.0,0.0),"testing")
    }

    fun fetchAllSurah(): AllSurahResponse{
        return AllSurahResponse(0,
            mutableListOf(
                com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data(
                    "","","","",0,0,"")
            )
        ,"test")
    }

    fun fetchAllSurahData(): List<com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data>{
        return mutableListOf(
            com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data(
                "","","","",0,0,"")
        )
    }

    /* Database */
    fun getMsApi1(): MsApi1 {
        return MsApi1(0, "-7.5633548", "110.8041806", "3","7","2020")
    }

    fun getListMsFavAyah(): List<MsFavAyah> {
        return mutableListOf(MsFavAyah(1, 2,"","",""))
    }

    fun getListMsFavSurah(surahid: Int): MutableLiveData<Resource<MsFavSurah>> {
        val liveData: MutableLiveData<Resource<MsFavSurah>> = MutableLiveData()
        liveData.value = Resource.success(
            MsFavSurah(1, "test","test")
        )
        return liveData
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