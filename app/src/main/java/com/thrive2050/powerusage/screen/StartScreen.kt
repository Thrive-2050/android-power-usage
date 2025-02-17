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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.thrive2050.powerusage.component.CenteredMessage
import com.thrive2050.powerusage.Constants
import com.thrive2050.powerusage.GistUrlFetcher

@Composable
fun StartScreenContent(
    gistUrlFetcher: GistUrlFetcher,
    onVideoUrlReceived: (Uri?) -> Unit,
    onStartVideoClicked: () -> Unit
) {
    var isFetchingUrl by remember { mutableStateOf(false) }
    var urlFetched by remember { mutableStateOf(false) }
    var urlFetchFailed by remember { mutableStateOf(false) }

    if (isFetchingUrl) {
        LaunchedEffect(key1 = Unit) {
            val fetchedUrl = gistUrlFetcher.getUrlFromGist(Constants.GIST_URL)
            if (fetchedUrl != null) {
                onVideoUrlReceived(fetchedUrl)
                urlFetched = true
            } else {
                urlFetchFailed = true
                onVideoUrlReceived(null)
            }
            isFetchingUrl = false
        }
    }

    StartScreen(
        fetchingUrlFailed = urlFetchFailed,
        onGetUrlClicked = {
            isFetchingUrl = true
        },
        onStartVideoClicked = onStartVideoClicked,
        isStartVideoButtonEnabled = urlFetched
    )
}

@Composable
fun StartScreen(
    fetchingUrlFailed: Boolean,
    onGetUrlClicked: () -> Unit,
    onStartVideoClicked: () -> Unit,
    isStartVideoButtonEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        if (fetchingUrlFailed) {
            CenteredMessage("Failed to get video URL")
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onGetUrlClicked) {
                    Text("Get URL")
                }
                Button(
                    onClick = onStartVideoClicked,
                    enabled = isStartVideoButtonEnabled
                ) {
                    Text("Start Video")
                }
            }
        }
    }
}