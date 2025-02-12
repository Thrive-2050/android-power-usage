package com.thrive2050.powerusage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thrive2050.powerusage.domain.GetEnergyConsumptionUseCase

class PowerUsageViewModelFactory(private val getEnergyConsumptionUseCase: GetEnergyConsumptionUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PowerUsageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PowerUsageViewModel(getEnergyConsumptionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}