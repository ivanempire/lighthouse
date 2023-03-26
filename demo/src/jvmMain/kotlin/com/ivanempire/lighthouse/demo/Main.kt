package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ivanempire.lighthouse.LighthouseClient
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun main() {
    System.setProperty("org.slf4j.simpleLogger.logFile", "System.out")
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")

    val lighthouseClient = LighthouseClient()

    val devicesFlow = lighthouseClient.discoverDevices()
        .map { devices -> devices.sortedBy { it.uuid } }

    application {
        Window(
            title = "Lighthouse Demo",
            onCloseRequest = ::exitApplication
        ) {
            DemoContent(
                devicesFlow = devicesFlow,
                loadDetailedDevice = {
                    try {
                        lighthouseClient.retrieveDescription(it)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        null
                    }
                }
            )
        }
    }
}