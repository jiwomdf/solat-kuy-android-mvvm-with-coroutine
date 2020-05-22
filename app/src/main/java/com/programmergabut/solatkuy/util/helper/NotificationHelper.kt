package com.programmergabut.solatkuy.util.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.broadcaster.MoreTimeBroadcastReceiver
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */

class NotificationHelper(c: Context): ContextWrapper(c) {

    private val channel1ID: String = "c_1"
    private val channel1Name: String = "channel1"
    private var mManager: NotificationManager? = null

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 =  NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT)

            channel1.enableLights(true)
            channel1.vibrationPattern = longArrayOf(0)
            channel1.enableVibration(true)
            channel1.lightColor = R.color.colorPrimary
            channel1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            getManager()?.createNotificationChannel(channel1)
        } else {
            //no notification channel
        }

    }

    fun getManager(): NotificationManager? {
        if(mManager == null)
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        return mManager
    }

    fun getPrayerReminderNC(pID: Int, pTime: String, pCity: String, pName: String, listPrayerBundle: Bundle, intent: PendingIntent): NotificationCompat.Builder {

        val message = "now is $pTime in $pCity let's pray $pName"

        val moreTimeIntent = Intent(this, MoreTimeBroadcastReceiver::class.java)
        moreTimeIntent.putExtra("moreTime_prayerID", pID)
        moreTimeIntent.putExtra("moreTime_prayerTime", pTime)
        moreTimeIntent.putExtra("moreTime_prayerCity", pCity)
        moreTimeIntent.putExtra("moreTime_prayerName", pName)
        moreTimeIntent.putExtra("moreTime_listPrayerBundle", listPrayerBundle)

        val actionIntent = PendingIntent.getBroadcast(this, 0, moreTimeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val lIcon = when(pName){
            getString(R.string.fajr) -> R.drawable.fajr
            getString(R.string.dhuhr) -> R.drawable.dhuhr
            getString(R.string.asr) -> R.drawable.asr
            getString(R.string.maghrib) -> R.drawable.maghrib
            getString(R.string.isha) -> R.drawable.isha
            else -> R.drawable.fajr
        }

        val bitmap = BitmapFactory.decodeResource(resources, lIcon)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val v = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            CoroutineScope(Dispatchers.IO).launch{
                for(i in 1 .. 4){
                    v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
                    delay(1200)
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return NotificationCompat.Builder(applicationContext, channel1ID)
                .setContentTitle(pName)
                .setContentText(message)
                .setSubText(EnumConfig.duaAfterAdhan)
                .setColor(getColor(R.color.colorPrimary))
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "remind me 10 more minute", actionIntent)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.ic_notifications_active_24dp)
                .setContentIntent(intent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.DEFAULT_ALL)
        }
        else{
            return NotificationCompat.Builder(this, channel1ID)
                .setContentTitle(pName)
                .setContentText(message)
                .setSubText(EnumConfig.duaAfterAdhan)
                .setColor(getColor(R.color.colorPrimary))
                .setVibrate(longArrayOf(500, 500, 500))
                .setSmallIcon(R.drawable.ic_notifications_active_24dp)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        }
    }

}