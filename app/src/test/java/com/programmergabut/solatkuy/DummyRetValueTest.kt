package com.programmergabut.solatkuy

import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EnumConfig
import retrofit2.Call
import java.util.*

object DummyRetValueTest {

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

    inline fun <reified BASE> fetchAsmaAlHusnaApi(): AsmaAlHusnaResponse {
        return JsonToPojoConverter.convertJson<BASE, AsmaAlHusnaResponse>(READ_SURAH_EN_SERVICE_JSON)
    }

    inline fun <reified BASE> surahEnID_1(): ReadSurahEnResponse{
        return JsonToPojoConverter.convertJson<BASE, ReadSurahEnResponse>(READ_SURAH_EN_SERVICE_JSON)
    }

    inline fun <reified BASE> surahArID_1(): ReadSurahArResponse{
        return JsonToPojoConverter.convertJson<BASE, ReadSurahArResponse>(READ_SURAH_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchPrayerApi(): PrayerResponse {
        return JsonToPojoConverter.convertJson<BASE, PrayerResponse>(CALENDAR_API_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchCompassApi(): CompassResponse{
        return JsonToPojoConverter.convertJson<BASE, CompassResponse>(QIBLA_API_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchAllSurahAr(): AllSurahResponse{
        return JsonToPojoConverter.convertJson<BASE, AllSurahResponse>(ALL_SURAH_SERVICE_JSON)
    }

    inline fun <reified BASE> fetchAllSurahWithLowerCase(): List<com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data> {
        val response = JsonToPojoConverter.convertJson<BASE, AllSurahResponse>(ALL_SURAH_SERVICE_JSON)

        return response.data.map { surah ->
            com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data(
                surah.englishName,
                surah.englishName.toLowerCase(Locale.getDefault()).replace("-", " "),
                surah.englishNameTranslation,
                surah.name,
                surah.number,
                surah.numberOfAyahs,
                surah.revelationType
            )
        }
    }


    /* Database */
    val surahID = 1
    val msfavSurah = MsFavSurah(1,"test","test")
    val msFavAyah = MsFavAyah(1,1,"test","test","test")
    val msFavSurah = MsFavSurah(1,"test", "test")
    val msApi1: MsApi1 = MsApi1(1, EnumConfigTesting.START_LAT, EnumConfigTesting.START_LNG,
            EnumConfigTesting.START_METHOD, EnumConfigTesting.START_MONTH, EnumConfigTesting.START_YEAR)
    val msSetting = MsSetting(1, true, isUsingDBQuotes = true)

    fun getMapPrayer(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()

        map[EnumConfig.FAJR] = "04:35"
        map[EnumConfig.DHUHR] = "11:41"
        map[EnumConfig.ASR] = "15:02"
        map[EnumConfig.MAGHRIB] = "17:32"
        map[EnumConfig.ISHA] = "18:42"
        map[EnumConfig.SUNRISE] = "05:00"

        return map
    }

    fun getListMsFavAyah(): List<MsFavAyah> {
        return mutableListOf(MsFavAyah(1, 2,"test","test","test"))
    }

}