package com.thrive2050.powerusage

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.thrive2050.powerusage.ui.theme.PowerUsageTheme

class BatteryViewModel : ViewModel() {
    private val _batteryCurrent = mutableIntStateOf(0)
    val batteryCurrent: State<Int> = _batteryCurrent

    fun updateBatteryCurrent(current: Int) {
        _batteryCurrent.intValue = current
    }
}

class MainActivity : ComponentActivity() {
    private val batteryViewModel by viewModels<BatteryViewModel>()

    private val batteryCurrentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "BATTERY_CURRENT_ACTION") {
                val current = intent.getIntExtra("BATTERY_CURRENT", 0)
                batteryViewModel.updateBatteryCurrent(current)
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(
            batteryCurrentReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        enableEdgeToEdge()
        setContent {
            PowerUsageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    CurrentDisplay(batteryViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryCurrentReceiver)
    }
}

@Composable
fun CurrentDisplay(viewModel: BatteryViewModel) {
    val batteryCurrent by viewModel.batteryCurrent
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Battery Current: $batteryCurrent microamperes")
    }
}