package com.programmergabut.solatkuy.data.local

import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.local.dao.MsSettingDao
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import kotlinx.coroutines.CoroutineScope
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
    private var msSettingDao: MsSettingDao = db.msSettingDao()

    fun getNotifiedPrayer() = notifiedPrayerDao.getNotifiedPrayer()
    fun getMsApi1() = msApi1Dao.getMsApi1()
    fun getMsSetting() = msSettingDao.getMsSetting()

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

    /* fun updateMsSetting(isHasOpen: Boolean){
        GlobalScope.launch(contextProviders.IO) {
            msSettingDao.updateMsSetting(isHasOpen)
        }
    } */

}