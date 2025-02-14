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
import com.thrive2050.powerusage.data.EnergyConsumption
import com.thrive2050.powerusage.domain.GetEnergyConsumptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PowerUsageViewModel(private val getEnergyConsumptionUseCase: GetEnergyConsumptionUseCase) : ViewModel() {
    private val _energyConsumption = MutableStateFlow(EnergyConsumption(0.0))
    val energyConsumption: StateFlow<EnergyConsumption> = _energyConsumption.asStateFlow()

    init {
        Log.d("PowerUsageVM", "ViewModel initialized")
        getEnergyConsumption()
    }

    private fun getEnergyConsumption() {
        Log.d("PowerUsageVM", "getEnergyConsumption() called")
        getEnergyConsumptionUseCase().onEach { energyConsumption ->
            Log.d("PowerUsageVM", "Energy Consumption updated: ${energyConsumption.energyInWattHours}")
            _energyConsumption.value = energyConsumption
        }.launchIn(viewModelScope)
    }
}