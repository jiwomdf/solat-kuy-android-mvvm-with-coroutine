package com.programmergabut.solatkuy

import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EnumConfig

object DummyRetValueAndroidTest {

    /* Last update : 4 Jan 2021 */
    val ALL_SURAH_SERVICE_JSON = "allSurahService.json"
    val CALENDAR_API_SERVICE_JSON = "calendarApiService.json"
    val QIBLA_API_SERVICE_JSON = "qiblaApiService.json"
    val READ_SURAH_EN_SERVICE_JSON = "readSurahEnService.json"
    val READ_SURAH_SERVICE_JSON = "readSurahService.json"


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

    inline fun <reified BASE> fetchAsmaAlHusnaApi(): AsmaAlHusnaResponse {
        return JsonToPojoConverterAndroidTest.convertJson<BASE, AsmaAlHusnaResponse>(READ_SURAH_EN_SERVICE_JSON)
    }

    inline fun <reified BASE> surahEnID_1(): ReadSurahEnResponse{
        return JsonToPojoConverterAndroidTest.convertJson<BASE, ReadSurahEnResponse>(READ_SURAH_EN_SERVICE_JSON)
    }

    inline fun <reified BASE> surahArID_1(): ReadSurahArResponse{
        return JsonToPojoConverterAndroidTest.convertJson<BASE, ReadSurahArResponse>(READ_SURAH_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchPrayerApi(): PrayerResponse{
        return JsonToPojoConverterAndroidTest.convertJson<BASE, PrayerResponse>(CALENDAR_API_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchCompassApi(): CompassResponse{
        return JsonToPojoConverterAndroidTest.convertJson<BASE, CompassResponse>(QIBLA_API_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchAllSurah(): AllSurahResponse{
        return JsonToPojoConverterAndroidTest.convertJson<BASE, AllSurahResponse>(ALL_SURAH_SERVICE_JSON)
    }

    /* Database */
    fun getMsApi1(): MsApi1 {
        return MsApi1(0, "-7.5633548", "110.8041806", "3","7","2020")
    }

    fun getListMsFavAyah(): MutableList<MsFavAyah> {
        return mutableListOf()
    }

    fun getListMsFavSurah(): MutableList<MsFavSurah> {
        return mutableListOf()
    }

    fun getMsSetting(): MsSetting {
        return MsSetting(1, true, isUsingDBQuotes = true)
    }

}