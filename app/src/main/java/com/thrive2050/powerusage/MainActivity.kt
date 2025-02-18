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

package com.thrive2050.powerusage

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.thrive2050.powerusage.data.PowerUsageRepositoryImpl
import com.thrive2050.powerusage.domain.GetEnergyConsumptionUseCase
import com.thrive2050.powerusage.presentation.PowerUsageViewModel
import com.thrive2050.powerusage.presentation.PowerUsageViewModelFactory
import com.thrive2050.powerusage.screen.EndScreen
import com.thrive2050.powerusage.screen.MainScreen
import com.thrive2050.powerusage.screen.StartScreenContent
import com.thrive2050.powerusage.ui.theme.PowerUsageTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PowerUsageViewModel by viewModels {
        PowerUsageViewModelFactory(GetEnergyConsumptionUseCase(PowerUsageRepositoryImpl(this)))
    }
    private val gistUrlFetcher = GistUrlFetcher()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PowerUsageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val energyConsumption by viewModel.energyConsumption.collectAsState()
                    var videoUrl by remember { mutableStateOf<Uri?>(null) }
                    var videoPlaying by remember { mutableStateOf(false) }
                    var videoEnded by remember { mutableStateOf(false) }

                    if (videoPlaying) {
                        MainScreen(
                            energyConsumption.lastOrNull()?.energyInWattHours ?: 0.0,
                            videoUrl!!,
                            onVideoEnded = {
                                videoPlaying = false;
                                videoEnded = true
                            }
                        )
                    } else if (videoEnded) {
                        EndScreen(
                            onResetClicked = {
                                videoEnded = false;
                                videoUrl = null
                            }
                        )
                    } else {
                        StartScreenContent(
                            gistUrlFetcher = gistUrlFetcher,
                            onVideoUrlReceived = { url ->
                                videoUrl = url
                            },
                            onStartVideoClicked = {
                                videoPlaying = true
                            }
                        )
                    }
                }
            }
        }
    }
}