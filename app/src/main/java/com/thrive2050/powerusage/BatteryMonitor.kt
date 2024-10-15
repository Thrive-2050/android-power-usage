package com.thrive2050.powerusage

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder

class BatteryMonitor : Service() {

    private val batteryLevelReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context, intent: Intent) {
            val batteryManager = context.getSystemService(BATTERY_SERVICE) as BatteryManager
            val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

            val broadcastIntent = Intent("BATTERY_CURRENT_ACTION").apply {
                putExtra("BATTERY_CURRENT", current)
            }
            context.sendBroadcast(broadcastIntent)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null // This service does not support binding
    }

    override fun onCreate() {
        super.onCreate()
        // Register the battery level receiver
        registerReceiver(batteryLevelReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the battery level receiver
        unregisterReceiver(batteryLevelReceiver)
    }
}