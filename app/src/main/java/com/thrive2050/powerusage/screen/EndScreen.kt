package com.thrive2050.powerusage.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.thrive2050.powerusage.data.PowerStatEntry

@Composable
fun EndScreen(onResetClicked: () -> Unit, powerStats: List<PowerStatEntry>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Video Ended")
            Text("Power Stats: $powerStats")
            Button(
                onClick = onResetClicked
            ) {
                Text("Reset App")
            }
        }
    }
}
