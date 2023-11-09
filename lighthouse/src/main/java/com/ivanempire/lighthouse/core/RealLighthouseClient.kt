package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge

/** Specific implementation of [LighthouseClient] */
internal class RealLighthouseClient(
    private val discoveryManager: DiscoveryManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val logger: LighthouseLogger? = null,
) : LighthouseClient {

    override fun discoverDevices(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        logger?.logStatusMessage(TAG, "Discovering devices with search request: $searchRequest")

        val foundDevicesFlow = discoveryManager.createNewDeviceFlow(searchRequest)
        val lostDevicesFlow = discoveryManager.createStaleDeviceFlow()

        return merge(foundDevicesFlow, lostDevicesFlow)
            .distinctUntilChanged()
            .flowOn(dispatcher)
    }

    private companion object {
        const val TAG = "RealLighthouseClient"
    }
}
