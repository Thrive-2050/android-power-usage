package com.thrive2050.powerusage

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
import com.thrive2050.powerusage.ui.theme.PowerUsageTheme
import java.text.DecimalFormat


class MainActivity : ComponentActivity() {
    private var energyConsumption by mutableDoubleStateOf(0.0)

    private val energyConsumptionUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("BatteryMainActivity", "Broadcast received: ${intent.action}")
            if (intent.action == "ENERGY_CONSUMPTION_UPDATE") {
                energyConsumption = intent.getDoubleExtra("ENERGY_CONSUMPTION", 0.0)

                Log.d("BatteryMainActivity", "energyConsumption: $energyConsumption")
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // can start this when item starts that we want to measure
        Log.d("BatteryMainActitvity", "Starting the Service")
        startService(Intent(this, BatteryStatsService::class.java))
        Log.d("BatteryMainActitvity", "Registering the Service")
        registerReceiver(energyConsumptionUpdateReceiver, IntentFilter("ENERGY_CONSUMPTION_UPDATE"),
            RECEIVER_NOT_EXPORTED
        )
        enableEdgeToEdge()
        setContent {
            PowerUsageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    CurrentDisplay(energyConsumption)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // can stop whenever the item we want to measure stops
        stopService(Intent(this, BatteryStatsService::class.java))
        unregisterReceiver(energyConsumptionUpdateReceiver)
    }
}

@Composable
fun CurrentDisplay(energyConsumption: Double) {
    val decimalFormat = DecimalFormat("#.########")
    val formattedKwh = decimalFormat.format(energyConsumption)
    Log.d("BatteryMainActivity", "formattedKwh: $formattedKwh")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Energy Consumption: $formattedKwh kWh")
    }
}