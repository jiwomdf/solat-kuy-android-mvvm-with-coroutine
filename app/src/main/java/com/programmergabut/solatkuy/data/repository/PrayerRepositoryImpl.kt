package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import com.programmergabut.solatkuy.data.local.dao.MsSettingDao
import com.programmergabut.solatkuy.data.local.dao.MsNotifiedPrayerDao
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer
import com.programmergabut.solatkuy.data.remote.ApiResponse
import com.programmergabut.solatkuy.data.remote.api.PrayerApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.Result
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.Timings
import com.programmergabut.solatkuy.di.contextprovider.ContextProviderImpl
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.MsCalculationMethodsDao
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.data.remote.json.methodJson.MethodResponse
import com.programmergabut.solatkuy.di.contextprovider.ContextProvider
import com.programmergabut.solatkuy.util.Constant
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class PrayerRepositoryImpl @Inject constructor(
    private val msNotifiedPrayerDao: MsNotifiedPrayerDao,
    private val msApi1Dao: MsApi1Dao,
    private val msSettingDao: MsSettingDao,
    private val msCalculationMethodsDao: MsCalculationMethodsDao,
    private val contextProvider: ContextProvider,
    private val qiblaApiService: QiblaApiService,
    private val prayerApiService: PrayerApiService
): BaseRepository(), PrayerRepository {

    /**
     *  NotifiedPrayer
     */
    override suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) =
        msNotifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)
    override suspend fun updatePrayerTime(prayerName: String, prayerTime: String) =
        msNotifiedPrayerDao.updatePrayerTime(prayerName, prayerTime)
    override suspend fun getListNotifiedPrayer(): List<MsNotifiedPrayer> =
        msNotifiedPrayerDao.getListNotifiedPrayerSync()

    /**
     *  MsApi1
     */
    override fun observeMsApi1(): LiveData<MsApi1> = msApi1Dao.observeMsApi1()
    override suspend fun updateMsApi1(msApi1: MsApi1) =
        msApi1Dao.updateMsApi1(msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)

    override suspend fun updateMsApi1Method(api1ID: Int, methodID: String) {
        msApi1Dao.updateMsApi1Method(api1ID, methodID)
    }

    /**
     *  MsSetting
     */
    override fun observeMsSetting(): LiveData<MsSetting> = msSettingDao.observeMsSetting()
    override suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String) =
        msApi1Dao.updateMsApi1MonthAndYear(api1ID, month, year)
    override suspend fun updateIsHasOpenApp(isHasOpen: Boolean) = msSettingDao.updateIsHasOpenApp(isHasOpen)

    /**
     *  Remote endpoint
     */
    override suspend fun fetchQibla(msApi1: MsApi1): Deferred<Resource<CompassResponse>> {
      return CoroutineScope(contextProvider.io()).async {
          lateinit var response: Resource<CompassResponse>
          try {
              val result = execute(qiblaApiService.fetchQibla(msApi1.latitude, msApi1.longitude))
              response = Resource.success(result)
          } catch (ex: Exception){
              response = Resource.error(CompassResponse())
              response.message = ex.message.toString()
          }
          response
      }
    }

    override suspend fun fetchPrayerApi(msApi1: MsApi1): Deferred<Resource<PrayerResponse>> {
        return CoroutineScope(contextProvider.io()).async {
            lateinit var response: Resource<PrayerResponse>
            try {
                val result = execute(prayerApiService.fetchPrayer(msApi1.latitude, msApi1.longitude,
                    msApi1.method, msApi1.month, msApi1.year))
                response = Resource.success(result)
            } catch (ex: Exception){
                response = Resource.error(PrayerResponse())
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getMethods(): LiveData<Resource<List<MsCalculationMethods>>> {
        return object :
            NetworkBoundResource<List<MsCalculationMethods>, MethodResponse>(contextProvider) {
            override fun loadFromDB(): LiveData<List<MsCalculationMethods>> =
                msCalculationMethodsDao.getMethods()

            override fun shouldFetch(data: List<MsCalculationMethods>?): Boolean = data == null || data.isEmpty()

            override fun createCall(): LiveData<ApiResponse<MethodResponse>> {
                return liveData {
                    withContext(contextProvider.io()) {
                        lateinit var response: MethodResponse
                        try {
                            response = execute(prayerApiService.fetchMethod())
                            emit(ApiResponse.success(response))
                        } catch (ex: Exception) {
                            response = MethodResponse()
                            response.message = ex.message.toString()
                            emit(ApiResponse.error(ex.message.toString(), response))
                        }
                    }
                }
            }

            override fun saveCallResult(data: MethodResponse) {
                val newList = mutableListOf<MsCalculationMethods>()
                val result = data.data
                newList.add(MsCalculationMethods(0, result.eGYPT.name, result.eGYPT.id))
                newList.add(MsCalculationMethods(1, result.fRANCE.name, result.fRANCE.id))
                newList.add(MsCalculationMethods(2, result.gULF.name, result.gULF.id))
                newList.add(MsCalculationMethods(3, result.iSNA.name, result.iSNA.id))
                newList.add(MsCalculationMethods(4, result.jAFARI.name, result.jAFARI.id))
                newList.add(MsCalculationMethods(5, result.kARACHI.name, result.kARACHI.id))
                newList.add(MsCalculationMethods(6, result.kUWAIT.name, result.kUWAIT.id))
                newList.add(MsCalculationMethods(7, result.mAKKAH.name, result.mAKKAH.id))
                newList.add(MsCalculationMethods(8, result.mOONSIGHTING.name, result.mOONSIGHTING.id))
                newList.add(MsCalculationMethods(9, result.mWL.name, result.mWL.id))
                newList.add(MsCalculationMethods(10, result.qATAR.name, result.qATAR.id))
                newList.add(MsCalculationMethods(11, result.rUSSIA.name, result.rUSSIA.id))
                newList.add(MsCalculationMethods(12, result.sINGAPORE.name, result.sINGAPORE.id))
                newList.add(MsCalculationMethods(13, result.tEHRAN.name, result.tEHRAN.id))
                newList.add(MsCalculationMethods(14, result.tURKEY.name, result.tURKEY.id))
                msCalculationMethodsDao.insert(newList)
            }

        }.asLiveData()
    }

    override fun getListNotifiedPrayer(msApi1: MsApi1): LiveData<Resource<List<MsNotifiedPrayer>>> {
        return object : NetworkBoundResource<List<MsNotifiedPrayer>, PrayerResponse>(contextProvider) {
            override fun loadFromDB(): LiveData<List<MsNotifiedPrayer>> = msNotifiedPrayerDao.getListNotifiedPrayer()

            override fun shouldFetch(data: List<MsNotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<ApiResponse<PrayerResponse>> {
                return liveData {
                    withContext(contextProvider.io()) {
                        lateinit var response: PrayerResponse
                        try {
                            response = execute(prayerApiService.fetchPrayer(msApi1.latitude,
                                msApi1.longitude, msApi1.method, msApi1.month, msApi1.year))
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
                        msNotifiedPrayerDao.updatePrayerTime(prayer.key, prayer.value)
                    }
                }
            }

        }.asLiveData()
    }

    private fun getTodayTimings(data: List<Result>): Timings? {
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        return data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings
    }

    private fun createPrayerMap(timings: Timings): MutableMap<String, String> {
        val prayerMap = mutableMapOf<String, String>()
        prayerMap[Constant.FAJR] = timings.fajr
        prayerMap[Constant.DHUHR] = timings.dhuhr
        prayerMap[Constant.ASR] = timings.asr
        prayerMap[Constant.MAGHRIB] = timings.maghrib
        prayerMap[Constant.ISHA] = timings.isha
        prayerMap[Constant.SUNRISE] = timings.sunrise
        return prayerMap
    }

}

