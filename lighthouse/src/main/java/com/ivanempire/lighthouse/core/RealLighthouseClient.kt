package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.SearchRequest
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class RealLighthouseClient(
    private val discoveryManager: DiscoveryManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LighthouseClient {

    override fun discoverDevices(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        return discoveryManager.createNewDeviceFlow(searchRequest).combine(discoveryManager.createStaleDeviceFlow()) { newDeviceList, oldDeviceList ->
            // TODO: Factor in stale devices
            newDeviceList // - oldDeviceList.toSet()
        }
            .flowOn(dispatcher)
    }
}
