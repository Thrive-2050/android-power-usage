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

package com.thrive2050.powerusage.screen

import android.net.Uri
import android.util.Log
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.thrive2050.powerusage.data.PowerStatEntry
import kotlinx.coroutines.delay
import java.text.DecimalFormat

@Composable
fun MainScreen(
    energyConsumption: List<PowerStatEntry>,
    videoUrl: Uri,
    onVideoEnded: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayerScreen(videoUrl, onVideoEnded)
        CurrentDisplay(energyConsumption, Modifier.align(Alignment.TopStart))
    }
}

@Composable
fun VideoPlayerScreen(videoUrl: Uri, onVideoEnded: () -> Unit) {
    val context = LocalContext.current
    val videoView = remember {
        VideoView(context).apply {
            Log.d("VideoView", "Video URL: $videoUrl")
            setVideoURI(videoUrl)
            setOnErrorListener { mp, what, extra ->
                Log.e("VideoView", "Error: what=$what, extra=$extra")
                true // Return true to indicate that you've handled the error
            }
            setOnCompletionListener {
                Log.d("VideoView", "Video completed")
                onVideoEnded()
            }
            setOnInfoListener { mp, what, extra ->
                Log.d("VideoView", "Info: what=$what, extra=$extra")
                true
            }
        }
    }

    AndroidView(
        factory = {
            videoView
        },
        modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(key1 = Unit) {
        Log.d("VideoView", "Starting video playback but waiting 3 seconds")
        delay(3000)
        Log.d("VideoView", "Video playback started after 3 seconds")
        videoView.start()
    }
}

@Composable
fun CurrentDisplay(
    energyConsumption: List<PowerStatEntry>,
    modifier: Modifier = Modifier
) {
    val decimalFormat = DecimalFormat("#.###")
    val formattedWh = decimalFormat.format(
        energyConsumption.lastOrNull()?.energyInWattHours ?: 0.0
    )
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(8.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Text(
            text = "Energy Consumption:\r\n$formattedWh Wh",
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}