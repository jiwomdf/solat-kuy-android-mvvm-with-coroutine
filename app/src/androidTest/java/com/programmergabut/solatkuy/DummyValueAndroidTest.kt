package com.programmergabut.solatkuy

import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.json.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EnumConfig

object DummyValueAndroidTest {

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
                EnumConfig.FAJR_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.DHUHR,
                true,
                EnumConfig.DHUHR_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.ASR,
                true,
                EnumConfig.ASR_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.MAGHRIB,
                true,
                EnumConfig.MAGHRIB_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.ISHA,
                true,
                EnumConfig.ISHA_TIME
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.SUNRISE,
                true,
                EnumConfig.SUNRISE_TIME
            )
        )


        return listNotifiedPrayer
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
        return MsApi1(0, "-7.5633548", "110.8041806", "11","7","2020")
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