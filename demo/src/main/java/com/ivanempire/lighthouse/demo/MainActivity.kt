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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice

class MainActivity : ComponentActivity() {

    private lateinit var lighthouseClient: LighthouseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lighthouseClient = LighthouseClient
            .Builder(this)
            .setRetryCount(2)
            .build()

        setContent {
            val devicesFlow = remember { lighthouseClient.discoverDevices() }
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
    ListItem(
        headlineText = { Text(device.host.toString()) },
        supportingText = { Text(device.uuid.toString()) },
    )
}
