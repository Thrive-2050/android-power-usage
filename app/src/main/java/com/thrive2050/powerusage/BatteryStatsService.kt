package com.thrive2050.powerusage

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class BatteryStatsService : Service() {
    private var initialBatteryLevel: Int = -1
    private var batteryCapacity: Double = -1.0
    private var voltage: Double = -1.0
    private var startTime: Long = -1

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()
        Log.d("BatteryStatsService", "Service onCreate")

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            val batteryStatus: Intent? = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                if (initialBatteryLevel == -1) {
                    initialBatteryLevel = level
                    startTime = System.currentTimeMillis()
                    batteryCapacity = getBatteryCapacity(this).toDouble()
                    voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).toDouble()
                } else {
                    val timeNow = System.currentTimeMillis()
                    val currentNow = getCurrentNow(this)
                    val currentInAmperes = currentNow.toDouble() / 1000000.0
                    val powerInWatts = currentInAmperes * voltage
                    val timeElapsedInSeconds = (timeNow - startTime) / 1000 // Get the time elapsed since the last measurement
                    val energyInJoules = powerInWatts * timeElapsedInSeconds
                    val energyInWattHours = energyInJoules / 3600.0
                    val energyInKilowattHours = energyInWattHours / 1000.0
                    startTime = System.currentTimeMillis()
                    Log.d("BatteryStatsService", "Time Elapsed: $timeElapsedInSeconds seconds")
                    Log.d("BatteryStatsService", "Joules: $energyInJoules")

                    // Send power consumption to activity
                    val broadcastIntent = Intent("ENERGY_CONSUMPTION_UPDATE")
                    broadcastIntent.putExtra("ENERGY_CONSUMPTION", energyInKilowattHours)
                    Log.d("BatteryStatsService", "Sending broadcast with action: $broadcastIntent")
                    sendBroadcast(broadcastIntent)
                    Log.d("BatteryStatsService", "Sent broadcast")
                }
            }
            // Schedule the next execution after 10 seconds
            handler.postDelayed(runnable, 1000)
        }

        // Start the initial execution
        handler.post(runnable)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun getBatteryCapacity(context: Context): Long {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    }

    private fun getCurrentNow(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
    }

    private fun calculatePowerConsumption(batteryConsumed: Int, batteryCapacity: Double, voltage: Double, timeElapsed: Double): Double {
        // Implement your power consumption calculation logic here
        // This is a placeholder, you'll need to refine it based on your specific needs
        return (batteryConsumed.toDouble() / 100.0) * (batteryCapacity.toDouble() / 1000.0) * voltage.toDouble() / timeElapsed
    }
}