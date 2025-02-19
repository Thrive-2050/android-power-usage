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
GNU General Public License
*/

package com.thrive2050.powerusage.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.thrive2050.powerusage.data.PowerStatEntry
import java.io.File
import java.io.FileWriter

@Composable
fun EndScreen(
    onResetClicked: () -> Unit,
    powerStats: List<PowerStatEntry>
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Video Ended")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    shareCsv(powerStats, context)
                }
            ) {
                Text("Share CSV")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onResetClicked
            ) {
                Text("Reset App")
            }
        }
    }
}

fun shareCsv(powerStats: List<PowerStatEntry>, context: Context) {
    try {
        // Create CSV data as a string
        val csvData = buildString {
            append("timestamp,Wh\n")
            powerStats.forEach { entry ->
                append("${entry.timestamp},${entry.energyInWattHours}\n") // Use formattedTimestamp here!
            }
        }

        // Create a temporary file
        val file = File(context.cacheDir, "power_stats.csv")
        FileWriter(file).use { writer ->
            writer.write(csvData)
        }

        // Get the URI for the file using FileProvider
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Replace with your authority
            file
        )

        // Create the share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        // Create a chooser intent
        val chooserIntent = Intent.createChooser(shareIntent, "Share CSV using...")

        // Start the chooser activity
        context.startActivity(chooserIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle error (e.g., show a Toast)
    }
}