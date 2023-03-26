package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetails(
    abridgedDevice: AbridgedMediaDevice,
    onClose: () -> Unit,
    loadDetailedDevice: suspend () -> DetailedMediaDevice?,
) {
    val detailedDevice = remember(abridgedDevice.uuid) { mutableStateOf<DetailedMediaDevice?>(null) }
    val currentDevice = detailedDevice.value
    var errorLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(abridgedDevice.uuid) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            if (currentDevice == null) {
                if (errorLoading) {
                    Text("Could not load details")
                } else {
                    Text("Loading...")
                }
            } else {
                Text(currentDevice.deviceType)
                Text(currentDevice.friendlyName)
                Text(currentDevice.manufacturer)
                currentDevice.manufacturerUrl?.let { Text(it) }
                currentDevice.modelDescription?.let { Text(it) }
                Text(currentDevice.modelName)
                currentDevice.modelNumber?.let { Text(it) }
                currentDevice.modelUrl?.let { Text(it) }
                currentDevice.serialNumber?.let { Text(it) }
                Text(currentDevice.udn)
                currentDevice.presentationUrl?.let { Text(it) }
            }
        }
    }
    LaunchedEffect(abridgedDevice.uuid) {
        val result = loadDetailedDevice()
        if (result == null)
            errorLoading = true
        else
            detailedDevice.value = result
    }
}