package com.thrive2050.powerusage.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface PowerUsageRepository {
    fun getBatteryStats(): Flow<BatteryStats>
}

class PowerUsageRepositoryImpl(private val context: Context) : PowerUsageRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getBatteryStats(): Flow<BatteryStats> = callbackFlow {
        Log.d("PowerUsageRepo", "getBatteryStats() called")
        val batteryStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("PowerUsageRepo", "Battery status intent received")
                if (intent != null) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).toDouble()
                    val batteryManager = context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                    val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                    Log.d("PowerUsageRepo", "Level: $level, Voltage: $voltage, Current: $currentNow")
                    trySend(BatteryStats(level, voltage, currentNow))
                } else {
                    Log.e("PowerUsageRepo", "Battery status intent is null")
                }
            }
        }

        context.registerReceiver(batteryStatusReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        awaitClose {
            Log.d("PowerUsageRepo", "Closing battery status receiver")
            context.unregisterReceiver(batteryStatusReceiver)
        }
    }
}