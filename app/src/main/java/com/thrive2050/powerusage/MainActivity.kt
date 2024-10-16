package com.thrive2050.powerusage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.lifecycleScope
import com.thrive2050.powerusage.ui.theme.PowerUsageTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import kotlin.math.abs


class MainActivity : ComponentActivity() {
    private var energyConsumption by mutableDoubleStateOf(0.0)
    private var initialBatteryLevel: Int = -1
    private var batteryCapacity: Double = -1.0
    private var voltage: Double = -1.0
    private var startTime: Long = -1

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scope = lifecycleScope

        scope.launch(Dispatchers.IO) {
            while (isActive) {
                // Collect battery stats here
                val batteryStats = getBatteryStats()

                // Update UI on the main thread
                withContext(Dispatchers.Main) {
                    // Update UI with batteryStats
                    energyConsumption = batteryStats
                }

                delay(1000) // Delay for 1 second (adjust as needed)
            }
        }

        enableEdgeToEdge()
        setContent {
            PowerUsageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    CurrentDisplay(energyConsumption)
                }
            }
        }
    }

    private fun getBatteryStats(): Double {
        val batteryStatus: Intent? = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        var energyInWattHours = 0.0

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
                val currentInAmperes = abs(currentNow.toDouble() / 1000000.0)
                val powerInWatts = currentInAmperes * voltage
                val timeElapsedInSeconds = (timeNow - startTime) / 1000 // Get the time elapsed since the last measurement
                val energyInJoules = powerInWatts * timeElapsedInSeconds
                energyInWattHours = energyInJoules / 3600.0
                startTime = System.currentTimeMillis()
            }
        }
        return energyInWattHours
    }

    private fun getBatteryCapacity(context: Context): Long {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    }

    private fun getCurrentNow(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
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
