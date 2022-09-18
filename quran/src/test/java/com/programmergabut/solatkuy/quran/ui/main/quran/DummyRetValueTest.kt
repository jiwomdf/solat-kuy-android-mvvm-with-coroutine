package com.programmergabut.solatkuy.quran.ui.main.quran

import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.json.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Constant
import java.util.*

object DummyRetValueTest {

    /* Last update : 4 Jan 2021 */
   val ALL_SURAH_SERVICE_JSON = "allSurahService.json"
   val CALENDAR_API_SERVICE_JSON = "calendarApiService.json"
   val QIBLA_API_SERVICE_JSON = "qiblaApiService.json"
   val READ_SURAH_EN_SERVICE_JSON = "readSurahEnService.json"
   val READ_SURAH_SERVICE_JSON = "readSurahService.json"

    /* Remote */
    fun getListNotifiedPrayer(): List<MsNotifiedPrayer> {
        val listNotifiedPrayer = mutableListOf<MsNotifiedPrayer>()

        listNotifiedPrayer.add(
            MsNotifiedPrayer(
                EnumConfigTesting.FAJR,
                true,
                EnumConfigTesting.FAJR_TIME
            )
        )
        listNotifiedPrayer.add(
            MsNotifiedPrayer(
                EnumConfigTesting.DHUHR,
                true,
                EnumConfigTesting.DHUHR_TIME
            )
        )
        listNotifiedPrayer.add(
            MsNotifiedPrayer(
                EnumConfigTesting.ASR,
                true,
                EnumConfigTesting.ASR_TIME
            )
        )
        listNotifiedPrayer.add(
            MsNotifiedPrayer(
                EnumConfigTesting.MAGHRIB,
                true,
                EnumConfigTesting.MAGHRIB_TIME
            )
        )
        listNotifiedPrayer.add(
            MsNotifiedPrayer(
                EnumConfigTesting.ISHA,
                true,
                EnumConfigTesting.ISHA_TIME
            )
        )
        listNotifiedPrayer.add(
            MsNotifiedPrayer(
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

    inline fun <reified BASE> fetchAllSurahAr(): AllSurahResponse {
        return JsonToPojoConverter.convertJson<BASE, AllSurahResponse>(ALL_SURAH_SERVICE_JSON)
    }

    inline fun <reified BASE> getAllSurahAr(): List<MsSurah>{
        val response = JsonToPojoConverter.convertJson<BASE, AllSurahResponse>(ALL_SURAH_SERVICE_JSON)
        return response.data.map { surah ->
            MsSurah(
                surah.number,
                surah.englishName,
                surah.englishName.toLowerCase(Locale.getDefault()).replace("-", " "),
                surah.englishNameTranslation,
                surah.name,
                surah.numberOfAyahs,
                surah.revelationType
            )
        }
    }

    inline fun <reified BASE> fetchAllSurahWithLowerCase(): List<MsSurah> {
        val response = JsonToPojoConverter.convertJson<BASE, AllSurahResponse>(ALL_SURAH_SERVICE_JSON)
        return response.data.map { surah ->
            MsSurah(
                surah.number,
                surah.englishName,
                surah.englishName.toLowerCase(Locale.getDefault()).replace("-", " "),
                surah.englishNameTranslation,
                surah.name,
                surah.numberOfAyahs,
                surah.revelationType
            )
        }
    }


    /* Database */
    val surahID = 1
    val msfavSurah = MsFavSurah(1,"test","test")
    val msFavSurah = MsFavSurah(1,"test", "test")
    val msConfiguration: MsConfiguration = MsConfiguration(1, EnumConfigTesting.START_LAT, EnumConfigTesting.START_LNG,
            EnumConfigTesting.START_METHOD, EnumConfigTesting.START_MONTH, EnumConfigTesting.START_YEAR)
    val msSetting = MsSetting(1, true)

    fun getMapPrayer(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()

        map[Constant.FAJR] = "04:35"
        map[Constant.DHUHR] = "11:41"
        map[Constant.ASR] = "15:02"
        map[Constant.MAGHRIB] = "17:32"
        map[Constant.ISHA] = "18:42"
        map[Constant.SUNRISE] = "05:00"

        return map
    }

}