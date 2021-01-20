package com.programmergabut.solatkuy.broadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.programmergabut.solatkuy.service.ServiceBootComplete
import com.programmergabut.solatkuy.util.LogConfig.Companion.ERROR

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if(context != null) {
                val service = Intent(context, ServiceBootComplete::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    context.startForegroundService(service)
                }
                else{
                    context.startService(service)
                }
            }
            else{
                val errMsg = "context == null"
                Log.d(ERROR,errMsg)
                throw Exception("BootCompleteReceiver $errMsg")
            }
        }
    }
}