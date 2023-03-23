package com.ivanempire.lighthouse.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ivanempire.lighthouse.LighthouseClient
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {

    private lateinit var lighthouseClient: LighthouseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lighthouseClient = LighthouseClient(this)

        val devicesFlow = lighthouseClient.discoverDevices()
            .map { devices -> devices.sortedBy { it.uuid } }

        setContent {
            val devices by devicesFlow.collectAsState(emptyList())

            Surface(modifier = Modifier.fillMaxSize()) {
                DeviceList(devices)
            }
        }
    }
}
