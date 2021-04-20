package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import com.programmergabut.solatkuy.data.local.dao.MsSettingDao
import com.programmergabut.solatkuy.data.local.dao.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.ApiResponse
import com.programmergabut.solatkuy.data.remote.api.PrayerApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.Result
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.Timings
import com.programmergabut.solatkuy.util.ContextProviders
import com.programmergabut.solatkuy.util.DebugUtil
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class PrayerRepositoryImpl @Inject constructor(
    private val notifiedPrayerDao: NotifiedPrayerDao,
    private val msApi1Dao: MsApi1Dao,
    private val msSettingDao: MsSettingDao,
    private val contextProviders: ContextProviders,
    private val qiblaApiService: QiblaApiService,
    private val prayerApiService: PrayerApiService
): DebugUtil(), PrayerRepository {

    /* NotifiedPrayer */
    override suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) =
        notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)
    override suspend fun updatePrayerTime(prayerName: String, prayerTime: String) =
        notifiedPrayerDao.updatePrayerTime(prayerName, prayerTime)
    override suspend fun getListNotifiedPrayer(): List<NotifiedPrayer> =
        notifiedPrayerDao.getListNotifiedPrayerSync()

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
              response = execute(qiblaApiService.fetchQibla(msApi1.latitude, msApi1.longitude))
              response.responseStatus = "1"
          }
          catch (ex: Exception){
              response = CompassResponse()
              response.responseStatus = "-1"
              response.message = ex.message.toString()
          }
          response
      }
    }
    override suspend fun fetchPrayerApi(msApi1: MsApi1): Deferred<PrayerResponse> {
        return CoroutineScope(IO).async {
            lateinit var response: PrayerResponse
            try {
                response = execute(prayerApiService.fetchPrayer(msApi1.latitude, msApi1.longitude,
                    msApi1.method, msApi1.month, msApi1.year))
                response.responseStatus = "1"
            }
            catch (ex: Exception){
                response = PrayerResponse()
                response.responseStatus = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getListNotifiedPrayerr(msApi1: MsApi1): LiveData<Resource<List<NotifiedPrayer>>> {
        return object : NetworkBoundResource<List<NotifiedPrayer>, PrayerResponse>(contextProviders) {
            override fun loadFromDB(): LiveData<List<NotifiedPrayer>> = notifiedPrayerDao.getListNotifiedPrayer()

            override fun shouldFetch(data: List<NotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<ApiResponse<PrayerResponse>> {
                return liveData {
                    withContext(CoroutineScope(IO).coroutineContext) {
                        lateinit var response: PrayerResponse
                        try {
                            response = execute(prayerApiService.fetchPrayer(msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year))
                            emit(ApiResponse.success(response))
                        } catch (ex: Exception) {
                            response = PrayerResponse()
                            response.message = ex.message.toString()
                            emit(ApiResponse.error(ex.message.toString(), response))
                        }
                    }
                }
            }

            override fun saveCallResult(data: PrayerResponse) {
                val todayData = getTodayTimings(data.data)
                if(todayData != null) {
                    val prayerMaps = createPrayerMap(todayData)
                    prayerMaps.forEach { prayer ->
                        notifiedPrayerDao.updatePrayerTime(prayer.key, prayer.value)
                    }
                }
            }

            override fun onFetchFailed() {

            }
        }.asLiveData()
    }

    private fun getTodayTimings(data: List<Result>): Timings? {
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        return data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings
    }

    private fun createPrayerMap(timings: Timings): MutableMap<String, String> {
        val prayerMap = mutableMapOf<String, String>()
        prayerMap[EnumConfig.FAJR] = timings.fajr
        prayerMap[EnumConfig.DHUHR] = timings.dhuhr
        prayerMap[EnumConfig.ASR] = timings.asr
        prayerMap[EnumConfig.MAGHRIB] = timings.maghrib
        prayerMap[EnumConfig.ISHA] = timings.isha
        prayerMap[EnumConfig.SUNRISE] = timings.sunrise
        return prayerMap
    }

}

