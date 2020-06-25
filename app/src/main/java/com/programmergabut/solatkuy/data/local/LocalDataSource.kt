package com.programmergabut.solatkuy.data.local

import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LocalDataSource constructor(db: SolatKuyRoom) {

    companion object {
        private var instance: LocalDataSource? = null

        fun getInstance(db: SolatKuyRoom): LocalDataSource =
            instance ?: LocalDataSource(db)
    }

    private var notifiedPrayerDao = db.notifiedPrayerDao()
    private var msApi1Dao = db.msApi1Dao()
    private var msSettingDao = db.msSettingDao()
    private var msFavAyahDao = db.msFavAyahDao()
    private var msFavSurahDao = db.msFavSurahDao()

    /* Notified Prayer */
    fun getNotifiedPrayer() = notifiedPrayerDao.getNotifiedPrayer()
    suspend fun updatePrayerTime(prayerName: String, prayerTime: String) = notifiedPrayerDao.updatePrayerTime(prayerName, prayerTime)
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)
    fun updateListPrayerTime(data: PrayerResponse){

        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val timings = data.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

        val map = mutableMapOf<String, String>()

        map[EnumConfig.fajr] = timings?.fajr.toString()
        map[EnumConfig.dhuhr] = timings?.dhuhr.toString()
        map[EnumConfig.asr] = timings?.asr.toString()
        map[EnumConfig.maghrib] = timings?.maghrib.toString()
        map[EnumConfig.isha] = timings?.isha.toString()
        map[EnumConfig.sunrise] = timings?.sunrise.toString()

        CoroutineScope(Dispatchers.IO).launch {
            map.forEach { p ->
                updatePrayerTime(p.key, p.value)
            }
        }
    }

    /* MsApi1 */
    fun getMsApi1() = msApi1Dao.getMsApi1()
    suspend fun updateMsApi1(msApi1: MsApi1) = msApi1Dao.updateMsApi1(msApi1.api1ID, msApi1.latitude,
        msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)

    /* MsSetting */
    fun getMsSetting() = msSettingDao.getMsSetting()
    suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = msSettingDao.updateIsUsingDBQuotes(isUsingDBQuotes)

    /* MsFavAyah */
    fun getMsFavAyah() = msFavAyahDao.getMsFavAyah()
    fun getMsFavAyahBySurahID(surahID: Int) = msFavAyahDao.getMsFavAyahBySurahID(surahID)
    suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    fun getMsFavSurahByID(surahID: Int) = msFavSurahDao.getMsFavSurahBySurahID(surahID)
    fun getMsFavSurah() = msFavSurahDao.getMsFavSurah()
    suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)


}