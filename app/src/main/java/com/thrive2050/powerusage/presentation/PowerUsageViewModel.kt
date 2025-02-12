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