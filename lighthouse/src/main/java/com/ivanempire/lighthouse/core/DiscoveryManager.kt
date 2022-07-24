package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.flow.Flow

/**
 *
 */
interface DiscoveryManager {

    /**
     * Creates a
     * @param searchRequest The [SearchRequest] to send at the start of device discovery
     */
    fun createNewDeviceFlow(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>>

    /**
     * Creates a [Flow] of a list of [AbridgedMediaDevice] that have not received any SSDP packets
     * in the last [AbridgedMediaDevice.cache] seconds. These will be removed from the main list
     * during processing.
     *
     * @return [Flow] of a list of stale [AbridgedMediaDevice] instances
     */
    fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>>
}
