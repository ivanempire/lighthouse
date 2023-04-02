package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.flow.Flow

/**
 * All discovery managers should conform to this interface in order to setup two required list
 * flows: the newly-discovered devices to add, and the stale devices to remove
 */
internal interface DiscoveryManager {

    /**
     * Creates a [Flow] of a list of [AbridgedMediaDevice] instances which have been built after
     * receiving the proper SSDP packets from the multicast socket.
     *
     * @param searchRequest The [SearchRequest] to send at the start of device discovery
     */
    fun createNewDeviceFlow(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>>
}
