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