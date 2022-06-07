package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.flow.Flow

interface DiscoveryManager {

    fun createNewDeviceFlow(): Flow<List<AbridgedMediaDevice>>

    fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>>
}
