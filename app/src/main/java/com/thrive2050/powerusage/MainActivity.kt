package com.thrive2050.powerusage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.thrive2050.powerusage.data.PowerUsageRepositoryImpl
import com.thrive2050.powerusage.domain.GetEnergyConsumptionUseCase
import com.thrive2050.powerusage.presentation.PowerUsageViewModel
import com.thrive2050.powerusage.presentation.PowerUsageViewModelFactory
import com.thrive2050.powerusage.ui.theme.PowerUsageTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    private val viewModel: PowerUsageViewModel by viewModels {
        PowerUsageViewModelFactory(GetEnergyConsumptionUseCase(PowerUsageRepositoryImpl(this)))
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PowerUsageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val energyConsumption by viewModel.energyConsumption.collectAsState()
                    CurrentDisplay(energyConsumption.energyInWattHours)
                }
            }
        }
    }
}

@Composable
fun CurrentDisplay(energyConsumption: Double) {
    val decimalFormat = DecimalFormat("#.###")
    val formattedWh = decimalFormat.format(energyConsumption)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Energy Consumption:\r\n$formattedWh Wh", textAlign = TextAlign.Center)
    }
}