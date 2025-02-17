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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.DecimalFormat

@Composable
fun MainScreen(energyConsumption: Double, videoUrl: Uri) {
    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayerScreen(videoUrl)
        CurrentDisplay(energyConsumption, Modifier.align(Alignment.TopStart))
    }
}

@Composable
fun VideoPlayerScreen(videoUrl: Uri) {
    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                Log.d("VideoView", "Video URL: $videoUrl")
                setVideoURI(videoUrl)
                setOnErrorListener { mp, what, extra ->
                    Log.e("VideoView", "Error: what=$what, extra=$extra")
                    true // Return true to indicate that you've handled the error
                }
                start()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CurrentDisplay(energyConsumption: Double, modifier: Modifier = Modifier) {
    val decimalFormat = DecimalFormat("#.###")
    val formattedWh = decimalFormat.format(energyConsumption)
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