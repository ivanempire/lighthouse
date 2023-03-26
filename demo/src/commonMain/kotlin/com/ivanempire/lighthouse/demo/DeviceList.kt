package com.ivanempire.lighthouse.demo

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice

@Composable
fun DeviceList(devices: List<AbridgedMediaDevice>, onClick: (AbridgedMediaDevice) -> Unit) {
    LazyColumn {
        items(
            items = devices,
            key = { it.uuid }
        ) {
            DeviceListItem(
                device = it,
                onClick = { onClick(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListItem(device: AbridgedMediaDevice, onClick: () -> Unit) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val ttl = device.cache - (currentTime - device.latestTimestamp) / 1000
    ListItem(
        modifier = Modifier.clickable { onClick() },
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
