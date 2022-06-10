package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.flow.Flow

/**
 *
 */
interface DiscoveryManager {

    /**
     * Creates a
     */
    fun createNewDeviceFlow(): Flow<List<AbridgedMediaDevice>>

    /**
     * Creates a [Flow] of a list of [AbridgedMediaDevice] that have not received any SSDP packets
     * in the last [AbridgedMediaDevice.cache] seconds. These will be removed from the main list
     * during processing.
     *
     * @return [Flow] of a list of stale [AbridgedMediaDevice] instances
     */
    fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>>
}
