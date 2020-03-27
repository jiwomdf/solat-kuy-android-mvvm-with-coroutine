package com.programmergabut.solatkuy.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import androidx.core.app.NotificationCompat
import com.programmergabut.solatkuy.R

class NotificationHelper(c: Context): ContextWrapper(c) {

    private val channel1ID: String = "c_1"
    private val channel1Name: String = "channel1"

    private var mManager: NotificationManager? = null

    init {
        val channel1 = NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT)
        channel1.enableLights(true)
        channel1.enableVibration(true)
        channel1.lightColor = R.color.colorPrimary
        channel1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        getManager()?.createNotificationChannel(channel1)
    }

    fun getManager(): NotificationManager? {
        if(mManager == null)
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        return mManager
    }

    fun getPrayerReminderNC(title: String, message: String): NotificationCompat.Builder {

        return NotificationCompat.Builder(applicationContext, channel1ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications_active_24dp)
    }

}