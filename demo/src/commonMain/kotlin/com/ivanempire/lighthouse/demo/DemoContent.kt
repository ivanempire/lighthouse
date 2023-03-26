package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice
import kotlinx.coroutines.flow.Flow

@Composable
fun DemoContent(
    devicesFlow: Flow<List<AbridgedMediaDevice>>,
    loadDetailedDevice: suspend (AbridgedMediaDevice) -> DetailedMediaDevice?,
) {
    val devices by devicesFlow.collectAsState(emptyList())
    var device by remember { mutableStateOf<AbridgedMediaDevice?>(null) }
    val currentDevice = device

    Surface(modifier = Modifier.fillMaxSize()) {
        if (currentDevice == null) {
            DeviceList(
                devices = devices,
                onClick = { device = it }
            )
        } else {
            DeviceDetails(
                abridgedDevice = currentDevice,
                onClose = { device = null },
                loadDetailedDevice = { loadDetailedDevice(currentDevice) },
            )
        }
    }
}