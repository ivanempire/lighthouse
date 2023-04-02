package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/** Specific implementation of [LighthouseClient] */
internal class RealLighthouseClient(
    private val discoveryManager: DiscoveryManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LighthouseClient {

    override fun discoverDevices(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        return discoveryManager.createNewDeviceFlow(searchRequest)
            .flowOn(dispatcher)
    }
}
