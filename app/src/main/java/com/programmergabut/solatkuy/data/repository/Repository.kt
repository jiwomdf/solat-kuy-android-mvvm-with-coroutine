package com.programmergabut.solatkuy.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.data.api.CalendarApiService
import com.programmergabut.solatkuy.data.local.MsApi1Dao
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.model.prayerJson.Timings
import com.programmergabut.solatkuy.room.SolatKuyRoom
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class Repository(application: Application, scope: CoroutineScope) {

    private var notifiedPrayerDao: NotifiedPrayerDao? = null
    private var msApi1Dao: MsApi1Dao? = null

    var mListPrayerLocal: LiveData<List<PrayerLocal>>
    var mMsApi1Local: LiveData<MsApi1>

    private var db: SolatKuyRoom = SolatKuyRoom.getDataBase(application, scope)

    init {
        mListPrayerLocal = db.notifiedPrayerDao().getNotifiedPrayer()
        mMsApi1Local = db.msApi1Dao().getMsApi1()
    }

    // Room
    suspend fun updateNotifiedPrayer(prayerLocal: PrayerLocal){
        notifiedPrayerDao = db.notifiedPrayerDao()
        notifiedPrayerDao?.updateNotifiedPrayer(prayerLocal.prayerName, prayerLocal.isNotified, prayerLocal.prayerTime)
    }

    private suspend fun updatePrayerTime(prayerName: String, prayerTime: String){
        notifiedPrayerDao = db.notifiedPrayerDao()
        notifiedPrayerDao?.updatePrayerTime(prayerName, prayerTime)
    }

    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean){
        notifiedPrayerDao = db.notifiedPrayerDao()
        notifiedPrayerDao?.updatePrayerIsNotified(prayerName, isNotified)
    }

    suspend fun updateMsApi1(api1ID: Int, latitude: String, longitude: String, method: String, month: String, year:String){
        msApi1Dao = db.msApi1Dao()
        msApi1Dao?.updateMsApi1(api1ID, latitude,longitude,method,month,year)
    }

    //Retrofit
    private fun retrofit(): CalendarApiService{
        return Builder()
            .baseUrl("http://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(CalendarApiService::class.java)
    }

    suspend fun fetchPrayerApi(latitude:String, longitude:String, method: String, month: String, year: String): PrayerApi {
        val retVal =  retrofit().fetchPrayer(latitude, longitude, method, month,year)

        updateDbAfterFetchingData(retVal)

        return retVal
    }

    private suspend fun updateDbAfterFetchingData(retVal: PrayerApi) {

        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val timings = retVal.data.find { obj -> obj.date.gregorian.day == currentDate.toString() }?.timings

        val map = mutableMapOf<String,String>()

        map["Fajr"] = timings?.fajr.toString()
        map["Dhuhr"] = timings?.dhuhr.toString()
        map["Asr"] = timings?.asr.toString()
        map["Maghrib"] = timings?.maghrib.toString()
        map["Isha"] = timings?.isha.toString()
        map["sunrise"] = timings?.sunrise.toString()

        map.forEach { p ->
            updatePrayerTime(p.key,p.value)
        }
    }


}