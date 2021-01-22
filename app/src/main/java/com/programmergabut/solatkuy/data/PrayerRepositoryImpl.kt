package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import java.lang.Exception
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class PrayerRepositoryImpl @Inject constructor(
    private val remoteDataSourceAladhan: RemoteDataSourceAladhan,
    private val notifiedPrayerDao: NotifiedPrayerDao,
    private val msApi1Dao: MsApi1Dao,
    private val msSettingDao: MsSettingDao,
): PrayerRepository {

    /* NotifiedPrayer */
    override suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) =
        notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)
    override suspend fun updatePrayerTime(prayerName: String, prayerTime: String) =
        notifiedPrayerDao.updatePrayerTime(prayerName, prayerTime)
    override suspend fun getListNotifiedPrayer(): List<NotifiedPrayer>? =
        notifiedPrayerDao.getListNotifiedPrayer()

    /* MsApi1 */
    override fun observeMsApi1(): LiveData<MsApi1> = msApi1Dao.observeMsApi1()
    override suspend fun updateMsApi1(msApi1: MsApi1) =
        msApi1Dao.updateMsApi1(msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)

    /* MsSetting */
    override fun observeMsSetting(): LiveData<MsSetting> = msSettingDao.observeMsSetting()
    override suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = msSettingDao.updateIsUsingDBQuotes(isUsingDBQuotes)
    override suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String) =
        msApi1Dao.updateMsApi1MonthAndYear(api1ID, month, year)
    override suspend fun updateIsHasOpenApp(isHasOpen: Boolean) = msSettingDao.updateIsHasOpenApp(isHasOpen)

    /* Remote */
    override suspend fun fetchCompass(msApi1: MsApi1): Deferred<CompassResponse> {
      return CoroutineScope(IO).async {
          lateinit var response: CompassResponse
          try {
              response = remoteDataSourceAladhan.fetchCompassApi(msApi1)
              response.statusResponse = "1"
          }
          catch (ex: Exception){
              response = CompassResponse()
              response.statusResponse = "-1"
              response.messageResponse = ex.message.toString()
          }
          response
      }
    }
    override suspend fun fetchPrayerApi(msApi1: MsApi1): Deferred<PrayerResponse> {
        return CoroutineScope(IO).async {
            lateinit var response: PrayerResponse
            try {
                response = remoteDataSourceAladhan.fetchPrayerApi(msApi1)
                response.statusResponse= "1"
            }
            catch (ex: Exception){
                response = PrayerResponse()
                response.statusResponse= "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

}

