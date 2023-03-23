package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ivanempire.lighthouse.LighthouseClient
import kotlinx.coroutines.flow.map

fun main() {
    val lighthouseClient = LighthouseClient()

    val devicesFlow = lighthouseClient.discoverDevices()
        .map { devices -> devices.sortedBy { it.uuid } }

    application {
        Window(
            title = "Lighthouse Demo",
            onCloseRequest = ::exitApplication
        ) {
            val devices by devicesFlow.collectAsState(emptyList())

            Surface(modifier = Modifier.fillMaxSize()) {
                DeviceList(devices)
            }
        }
    }
}