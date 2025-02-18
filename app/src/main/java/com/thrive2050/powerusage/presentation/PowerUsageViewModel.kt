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

package com.thrive2050.powerusage.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrive2050.powerusage.data.PowerStatEntry
import com.thrive2050.powerusage.domain.GetEnergyConsumptionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PowerUsageViewModel(private val getEnergyConsumptionUseCase: GetEnergyConsumptionUseCase) : ViewModel() {
    private val _energyConsumption = MutableStateFlow<List<PowerStatEntry>>(emptyList())
    val energyConsumption: StateFlow<List<PowerStatEntry>> = _energyConsumption.asStateFlow()
    private var collectingJob: Job? = null
    private var isCollecting = false

    init {
        Log.d("PowerUsageVM", "ViewModel initialized")
    }

    fun startCollecting() {
        Log.d("PowerUsageVM", "startCollecting() called")
        _energyConsumption.update { emptyList() }
        isCollecting = true
        collectingJob = getEnergyConsumption()
    }

    fun stopCollecting() {
        Log.d("PowerUsageVM", "stopCollecting() called")
        isCollecting = false
        collectingJob?.cancel()
        collectingJob = null
        Log.d("PowerUsageVM", "Collecting stopped")
        Log.d("PowerUsageVM", "Energy Consumption: ${_energyConsumption.value}")
    }

    private fun getEnergyConsumption(): Job {
        Log.d("PowerUsageVM", "getEnergyConsumption() called")
        return getEnergyConsumptionUseCase().onEach { batteryUsage ->
            if (isCollecting) {
                val timestamp = formatTimestamp(System.currentTimeMillis())
                val powerStatEntry = PowerStatEntry(timestamp, batteryUsage.energyInWattHours)
                Log.d(
                    "PowerUsageVM",
                    "Energy Consumption updated: ${powerStatEntry.energyInWattHours}"
                )
                _energyConsumption.update { currentList ->
                    (currentList + powerStatEntry)
                }

            }
        }.launchIn(viewModelScope)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }
}