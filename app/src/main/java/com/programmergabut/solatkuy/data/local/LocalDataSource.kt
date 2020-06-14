package com.programmergabut.solatkuy.data.local

import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocalDataSource private
constructor(private val contextProviders: ContextProviders, db: SolatKuyRoom) {

    companion object {
        private var instance: LocalDataSource? = null

        fun getInstance(contextProviders: ContextProviders, db: SolatKuyRoom): LocalDataSource =
            instance ?: LocalDataSource(contextProviders, db)
    }

    private var notifiedPrayerDao = db.notifiedPrayerDao()
    private var msApi1Dao = db.msApi1Dao()
    private var msSettingDao = db.msSettingDao()
    private var msFavAyahDao = db.msFavAyahDao()
    private var msFavSurahDao = db.msFavSurahDao()

    fun getNotifiedPrayer() = notifiedPrayerDao.getNotifiedPrayer()
    fun getMsApi1() = msApi1Dao.getMsApi1()
    fun getMsSetting() = msSettingDao.getMsSetting()

    fun getMsFavAyah() = msFavAyahDao.getMsFavAyah()
    fun getMsFavAyahBySurahID(surahID: Int) = msFavAyahDao.getMsFavAyahBySurahID(surahID)
    fun isFavAyah(ayahID: Int, surahID: Int) = msFavAyahDao.isFavAyah(ayahID, surahID)

    fun getMsFavSurahByID(surahID: Int) = msFavSurahDao.getMsFavSurahBySurahID(surahID)
    fun getMsFavSurah() = msFavSurahDao.getMsFavSurah()

    /* fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        GlobalScope.launch(contextProviders.IO) {
            notifiedPrayerDao.updateNotifiedPrayer(
                NotifiedPrayer.prayerName,
                NotifiedPrayer.isNotified,
                NotifiedPrayer.prayerTime
            )
        }
    } */

    fun updatePrayerTime(prayerName: String, prayerTime: String){
        GlobalScope.launch(contextProviders.IO){
            notifiedPrayerDao.updatePrayerTime(prayerName, prayerTime)
        }
    }

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean){
        GlobalScope.launch(contextProviders.IO) {
            notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)
        }
    }

    fun updateMsApi1(msApi1: MsApi1){
        GlobalScope.launch(contextProviders.IO) {
            msApi1Dao.updateMsApi1(msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
        }
    }

    fun insertFavAyah(msFavAyah: MsFavAyah){
        GlobalScope.launch(contextProviders.IO) {
            msFavAyahDao.insertMsAyah(msFavAyah)
        }
    }

    fun deleteFavAyah(msFavAyah: MsFavAyah){
        GlobalScope.launch(contextProviders.IO) {
            msFavAyahDao.deleteMsFavAyah(msFavAyah)
        }
    }

    fun insertFavSurah(msFavSurah: MsFavSurah){
        GlobalScope.launch(contextProviders.IO) {
            msFavSurahDao.insertMsSurah(msFavSurah)
        }
    }

    fun deleteFavSurah(msFavSurah: MsFavSurah){
        GlobalScope.launch(contextProviders.IO) {
            msFavSurahDao.deleteMsFavSurah(msFavSurah)
        }
    }

    /* fun updateMsSetting(isHasOpen: Boolean){
        GlobalScope.launch(contextProviders.IO) {
            msSettingDao.updateMsSetting(isHasOpen)
        }
    } */

}