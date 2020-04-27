package com.programmergabut.solatkuy.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.data.api.AsmaAlHusnaService
import com.programmergabut.solatkuy.data.api.CalendarApiService
import com.programmergabut.solatkuy.data.api.QiblaApiService
import com.programmergabut.solatkuy.data.local.MsApi1Dao
import com.programmergabut.solatkuy.data.local.MsSettingDao
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.asmaalhusnaJson.AsmaAlHusnaApi
import com.programmergabut.solatkuy.data.model.compassJson.CompassApi
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.MsSetting
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.room.SolatKuyRoom
import com.programmergabut.solatkuy.util.EnumPrayer
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class Repository(application: Application, scope: CoroutineScope) {

    private var notifiedPrayerDao: NotifiedPrayerDao? = null
    private var msApi1Dao: MsApi1Dao? = null
    private var msSettingDao: MsSettingDao? = null

    var mListPrayerLocal: LiveData<List<PrayerLocal>>
    var mMsApi1: LiveData<MsApi1>
    var mMsSetting: LiveData<MsSetting>

    private var db: SolatKuyRoom = SolatKuyRoom.getDataBase(application, scope)

    init {
        /* for observable data */
        mListPrayerLocal = db.notifiedPrayerDao().getNotifiedPrayer()
        mMsApi1 = db.msApi1Dao().getMsApi1()
        mMsSetting = db.msSettingDao().getMsSetting()

        /* for db transaction */
        notifiedPrayerDao = db.notifiedPrayerDao()
        msApi1Dao = db.msApi1Dao()
        msSettingDao = db.msSettingDao()
    }

    // Room
    suspend fun updateNotifiedPrayer(prayerLocal: PrayerLocal){
        notifiedPrayerDao?.updateNotifiedPrayer(prayerLocal.prayerName, prayerLocal.isNotified, prayerLocal.prayerTime)
    }

    private suspend fun updatePrayerTime(prayerName: String, prayerTime: String){
        notifiedPrayerDao?.updatePrayerTime(prayerName, prayerTime)
    }

    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean){
        notifiedPrayerDao?.updatePrayerIsNotified(prayerName, isNotified)
    }

    suspend fun updateMsApi1(api1ID: Int, latitude: String, longitude: String, method: String, month: String, year:String){
        msApi1Dao?.updateMsApi1(api1ID, latitude,longitude,method,month,year)
    }

    suspend fun updateMsSetting(isHasOpen: Boolean){
        msSettingDao?.updateMsSetting(isHasOpen)
    }

    //Retrofit
    private val strApi = "https://api.aladhan.com/v1/"
    private fun getCalendarApi(): CalendarApiService{
        return Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(CalendarApiService::class.java)
    }

    private fun getQiblaApi(): QiblaApiService{
        return Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(QiblaApiService::class.java)
    }

    private fun getAsmaAlHusnaApi(): AsmaAlHusnaService{
        return Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(AsmaAlHusnaService::class.java)
    }

    suspend fun fetchPrayerApi(latitude:String, longitude:String, method: String, month: String, year: String): PrayerApi {
        val retVal =  getCalendarApi().fetchPrayer(latitude, longitude, method, month,year)

        updateDbAfterFetchingData(retVal)

        return retVal
    }

    private suspend fun updateDbAfterFetchingData(retVal: PrayerApi) {

        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val timings = retVal.data.find { obj -> obj.date.gregorian.day == currentDate.toString() }?.timings

        val map = mutableMapOf<String,String>()

        map[EnumPrayer.fajr] = timings?.fajr.toString()
        map[EnumPrayer.dhuhr] = timings?.dhuhr.toString()
        map[EnumPrayer.asr] = timings?.asr.toString()
        map[EnumPrayer.maghrib] = timings?.maghrib.toString()
        map[EnumPrayer.isha] = timings?.isha.toString()
        map[EnumPrayer.sunrise] = timings?.sunrise.toString()

        map.forEach { p ->
            updatePrayerTime(p.key,p.value)
        }
    }

    suspend fun fetchQiblaApi(latitude:String, longitude:String): CompassApi{
        return  getQiblaApi().fetchQibla(latitude, longitude)
    }

    suspend fun fetchAsmaAlHusnaApi(): AsmaAlHusnaApi {
        return  getAsmaAlHusnaApi().fetchAsmaAlHusnaApi()
    }


}