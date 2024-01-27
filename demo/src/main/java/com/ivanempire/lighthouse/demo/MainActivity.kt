package com.ivanempire.lighthouse.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var lighthouseClient: LighthouseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Example implementation of a custom logging system
        val customLogger = object : LighthouseLogger() {
            override fun logStateMessage(tag: String, message: String) {
                Log.d(tag, message)
            }

            override fun logStatusMessage(tag: String, message: String) {
                Log.d(tag, message)
            }

            override fun logPacketMessage(tag: String, message: String) {
                Log.d(tag, message)
            }

            override fun logErrorMessage(tag: String, message: String, ex: Throwable?) {
                Log.e(tag, message, ex)
            }
        }

        // Setup the client
        lighthouseClient = LighthouseClient
            .Builder(this)
            .setLogger(customLogger)
            .setRetryCount(2)
            .build()

        // Skips the need for Dagger in a simple demo app
        val viewModelFactory = MainActivityViewModelFactory(lighthouseClient)
        val viewModel = viewModelFactory.create(MainActivityViewModel::class.java)

        setContent {
            val discoveredDeviceList = viewModel.discoveredDevices.collectAsState()

            Column(modifier = Modifier.padding(16.dp)) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    items(
                        items = discoveredDeviceList.value,
                        key = { it.uuid },
                    ) {
                        DeviceListItem(device = it)
                    }
                }

                val isDiscoveryRunning = remember { mutableStateOf(false) }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        viewModel.stopDiscovery()
                        isDiscoveryRunning.value = false
                    }, enabled = !isDiscoveryRunning.value) {
                        Text(text = "Stop discovery")
                    }
                    Button(onClick = {
                        viewModel.startDiscovery()
                        isDiscoveryRunning.value = true
                    }, enabled = !isDiscoveryRunning.value) {
                        Text(text = "Start discovery")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListItem(device: AbridgedMediaDevice) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val ttl = device.cache - (currentTime - device.latestTimestamp) / 1000
    ListItem(
        headlineText = { Text(device.location.toString()) },
        supportingText = { Text(device.uuid) },
        trailingContent = { Text(ttl.toString()) },
    )
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
}
