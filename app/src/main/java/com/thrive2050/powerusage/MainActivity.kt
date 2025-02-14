package com.thrive2050.powerusage

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.thrive2050.powerusage.data.PowerUsageRepositoryImpl
import com.thrive2050.powerusage.domain.GetEnergyConsumptionUseCase
import com.thrive2050.powerusage.presentation.PowerUsageViewModel
import com.thrive2050.powerusage.presentation.PowerUsageViewModelFactory
import com.thrive2050.powerusage.ui.theme.PowerUsageTheme
import java.text.DecimalFormat

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
                    var isLoading by remember { mutableStateOf(true) }
                    var loadFailed by remember { mutableStateOf(false) }

                    LaunchedEffect(key1 = Unit) {
                        val fetchedUrl = gistUrlFetcher.getUrlFromGist(Constants.GIST_URL)
                        if (fetchedUrl != null) {
                            videoUrl = fetchedUrl
                            isLoading = false
                        } else {
                            loadFailed = true
                            isLoading = false
                        }
                    }

                    if (loadFailed) {
                        CenteredMessage("Failed to load video")
                    } else if (isLoading) {
                        CenteredMessage("Loading video...")
                    } else if (videoUrl != null) {
                        MainScreen(energyConsumption.energyInWattHours, videoUrl!!)
                    }
                }
            }
        }
    }
}

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

@Composable
fun CenteredMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}