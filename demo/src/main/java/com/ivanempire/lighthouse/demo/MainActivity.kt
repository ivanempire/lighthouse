package com.ivanempire.lighthouse.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {

    private lateinit var lighthouseClient: LighthouseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lighthouseClient = LighthouseClient
            .Builder(this)
            .setRetryCount(2)
            .build()

        val devicesFlow = lighthouseClient.discoverDevices()
            .map { devices -> devices.sortedBy { it.uuid } }

        setContent {
            val devices = devicesFlow.collectAsState(emptyList())

            Surface(modifier = Modifier.fillMaxSize()) {
                LazyColumn {
                    items(
                        items = devices.value,
                        key = { it.uuid }
                    ) {
                        DeviceListItem(it)
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
        trailingContent = { Text(ttl.toString())}
    )
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
}
