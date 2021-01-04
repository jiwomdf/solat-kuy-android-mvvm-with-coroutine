package com.programmergabut.solatkuy

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.En
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.Edition
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
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

    inline fun <reified BASE> fetchPrayerApi(): PrayerResponse{
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

        val mappedResponse = response.data.map { surah ->
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

        return mappedResponse
    }


    /* Database */
    fun getMsApi1(): MsApi1 {
        return MsApi1(0, EnumConfigTesting.START_LAT, EnumConfigTesting.START_LNG,
            EnumConfigTesting.START_METHOD, EnumConfigTesting.START_MONTH, EnumConfigTesting.START_YEAR)
    }

    fun getListMsFavAyah(): List<MsFavAyah> {
        return mutableListOf(MsFavAyah(1, 2,"test","test","test"))
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