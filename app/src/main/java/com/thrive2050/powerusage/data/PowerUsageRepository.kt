/*
Copyright (C) 2024, 2025 John Behan
This file is part of Thrive 2050's Android Power Usage app

Thrive 2050's Android Power Usage app is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Thrive 2050's Android Power Usage app is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
*/

package com.thrive2050.powerusage.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.thrive2050.powerusage.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface PowerUsageRepository {
    fun getBatteryStats(): Flow<BatteryStats>
}

class PowerUsageRepositoryImpl(private val context: Context) : PowerUsageRepository {
    override fun getBatteryStats(): Flow<BatteryStats> = callbackFlow {
        Log.d("PowerUsageRepo", "getBatteryStats() called")
        while (true) {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, intentFilter)
            val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)?.toDouble() ?: 0.0

            Log.d("PowerUsageRepo", "Level: $level, Voltage: $voltage, Current: $currentNow")
            trySend(BatteryStats(level, voltage, currentNow))
            delay(Constants.POLLING_INTERVAL_MS)
        }
    }
}