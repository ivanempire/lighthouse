package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

/** Specific implementation of [LighthouseClient] */
internal class RealLighthouseClient(
    private val discoveryManager: DiscoveryManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LighthouseClient {

    @OptIn(FlowPreview::class)
    override fun discoverDevices(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        val foundDevicesFlow = discoveryManager.createNewDeviceFlow(searchRequest)
        val lostDevicesFlow = discoveryManager.createStaleDeviceFlow()

        // flattenMerge() causes the overall Flow to emit every second (due to the periodic check
        // for stale devices), however, by dropping empty lists the consumers will only receive
        // an updated list when there's a change: new device, an update, or a device goes offline
        return flowOf(foundDevicesFlow, lostDevicesFlow)
            .flattenMerge()
            .filter { it.isNotEmpty() }
            .flowOn(dispatcher)
    }
}
