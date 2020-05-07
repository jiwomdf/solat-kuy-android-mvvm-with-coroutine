package com.programmergabut.solatkuy.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.RemoteDataSource
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
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class Repository(application: Application, scope: CoroutineScope, private val remoteDataSource: RemoteDataSource) {

    private var db: SolatKuyRoom = SolatKuyRoom.getDataBase(application, scope)

    private var notifiedPrayerDao = db.notifiedPrayerDao()
    private var msApi1Dao =db.msApi1Dao()
    private var msSettingDao: MsSettingDao = db.msSettingDao()

    var mListPrayerLocal = db.notifiedPrayerDao().getNotifiedPrayer()
    var mMsApi1 = db.msApi1Dao().getMsApi1()
    var mMsSetting = db.msSettingDao().getMsSetting()

    companion object{
        @Volatile
        private var instance: Repository? = null

        fun getInstance(application: Application ,scope: CoroutineScope, remoteDataSource: RemoteDataSource) =
            instance ?: synchronized(this){
                instance ?: Repository(application, scope, remoteDataSource)
            }
    }

    // Room
    suspend fun updateNotifiedPrayer(prayerLocal: PrayerLocal){
        notifiedPrayerDao.updateNotifiedPrayer(prayerLocal.prayerName, prayerLocal.isNotified, prayerLocal.prayerTime)
    }

    private suspend fun updatePrayerTime(prayerName: String, prayerTime: String){
        notifiedPrayerDao.updatePrayerTime(prayerName, prayerTime)
    }

    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean){
        notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)
    }

    suspend fun updateMsApi1(api1ID: Int, latitude: String, longitude: String, method: String, month: String, year:String){
        msApi1Dao.updateMsApi1(api1ID, latitude,longitude,method,month,year)
    }

    suspend fun updateMsSetting(isHasOpen: Boolean){
        msSettingDao.updateMsSetting(isHasOpen)
    }


    //Retrofit
    fun getCompass(latitude:String, longitude:String): MutableLiveData<Resource<CompassApi>>{
        val result = MutableLiveData<Resource<CompassApi>>()

        remoteDataSource.fetchCompassApi(object : RemoteDataSource.LoadCompassCallback{
            override fun onReceived(response: Resource<CompassApi>) {
                result.postValue(response)
            }
        }, latitude, longitude)

        return result
    }

    fun getAsmaAlHusna(): MutableLiveData<Resource<AsmaAlHusnaApi>>{
        val result = MutableLiveData<Resource<AsmaAlHusnaApi>>()

        remoteDataSource.fetchAsmaAlHusnaApi(object : RemoteDataSource.LoadAsmaAlHusnaCallback{
            override fun onReceived(response: Resource<AsmaAlHusnaApi>) {
                result.postValue(response)
            }
        })

        return result
    }

    fun fetchPrayerApi(latitude:String, longitude:String, method: String,
                       month: String, year: String): MutableLiveData<Resource<PrayerApi>> {
        val result = MutableLiveData<Resource<PrayerApi>>()

        remoteDataSource.fetchPrayerApi(object : RemoteDataSource.LoadPrayerCallback{
            override fun onReceived(response: Resource<PrayerApi>) {
                result.postValue(response)
                //updateDbAfterFetchingData(result)
            }
        }, latitude, longitude, method, month, year)

        return result
    }

    /* private suspend fun updateDbAfterFetchingData(retVal: PrayerApi) {

    val sdf = SimpleDateFormat("dd", Locale.getDefault())
    val currentDate = sdf.format(Date())

    val timings = retVal.data.find { obj -> obj.date.gregorian.day == currentDate.toString() }?.timings

    val map = mutableMapOf<String,String>()

    map[EnumConfig.fajr] = timings?.fajr.toString()
    map[EnumConfig.dhuhr] = timings?.dhuhr.toString()
    map[EnumConfig.asr] = timings?.asr.toString()
    map[EnumConfig.maghrib] = timings?.maghrib.toString()
    map[EnumConfig.isha] = timings?.isha.toString()
    map[EnumConfig.sunrise] = timings?.sunrise.toString()

    map.forEach { p ->
        updatePrayerTime(p.key,p.value)
    }*/

}