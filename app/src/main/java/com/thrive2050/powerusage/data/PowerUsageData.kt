package com.thrive2050.powerusage.data

data class BatteryStats(
    val level: Int,
    val voltage: Double,
    val currentNow: Int
)

data class EnergyConsumption(
    val energyInWattHours: Double
)
