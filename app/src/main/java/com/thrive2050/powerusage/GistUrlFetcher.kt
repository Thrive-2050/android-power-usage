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

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.isNotBlank

class GistUrlFetcher {
    suspend fun getUrlFromGist(gistUrl: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(gistUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val videoUrlString = reader.readLine()
                reader.close()
                connection.disconnect()

                if (videoUrlString != null && videoUrlString.isNotBlank()) {
                    Log.d("GetURLFromGist", "Video URL from Gist: $videoUrlString")
                    Uri.parse(videoUrlString)
                } else {
                    Log.e("GistUrlFetcher", "Invalid or empty video URL in Gist")
                    null
                }
            } catch (e: IOException) {
                Log.e("GistUrlFetcher", "Error fetching video URL from Gist", e)
                null
            }
        }
    }
}