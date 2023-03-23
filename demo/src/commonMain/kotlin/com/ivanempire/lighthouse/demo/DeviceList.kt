package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice

@Composable
fun DeviceList(devices: List<AbridgedMediaDevice>) {
    LazyColumn {
        items(
            items = devices,
            key = { it.uuid }
        ) {
            DeviceListItem(it)
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
        trailingContent = { Text(ttl.toString()) }
    )
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
}
