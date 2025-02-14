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

package com.thrive2050.powerusage.domain

import android.util.Log
import com.thrive2050.powerusage.data.EnergyConsumption
import com.thrive2050.powerusage.data.PowerUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.abs

class GetEnergyConsumptionUseCase(private val repository: PowerUsageRepository) {
    private var initialBatteryLevel: Int = -1
    private var startTime: Long = -1

    operator fun invoke(): Flow<EnergyConsumption> = repository.getBatteryStats().map { batteryStats ->
        Log.d("EnergyCalc", "Battery Stats received: Level=${batteryStats.level}, Voltage=${batteryStats.voltage}, Current=${batteryStats.currentNow}")
        var energyInWattHours = 0.0
        if (initialBatteryLevel == -1) {
            Log.d("EnergyCalc", "Initializing: initialBatteryLevel=${batteryStats.level}, startTime=${System.currentTimeMillis()}")
            initialBatteryLevel = batteryStats.level
            startTime = System.currentTimeMillis()
        } else {
            val timeNow = System.currentTimeMillis()
            val currentInAmperes = abs(batteryStats.currentNow.toDouble() / 1000000.0)
            val powerInWatts = currentInAmperes * batteryStats.voltage
            val timeElapsedInSeconds = (timeNow - startTime) / 1000
            val energyInJoules = powerInWatts * timeElapsedInSeconds
            Log.d("EnergyCalc", "Calculating: currentInAmperes=$currentInAmperes, powerInWatts=$powerInWatts, timeElapsedInSeconds=$timeElapsedInSeconds, energyInJoules=$energyInJoules, energyInWattHours=$energyInWattHours")
            energyInWattHours = energyInJoules / 3600.0
            startTime = System.currentTimeMillis()
        }
        EnergyConsumption(energyInWattHours)
    }
}