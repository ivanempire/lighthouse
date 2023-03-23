package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/** Specific implementation of [LighthouseClient] */
internal class RealLighthouseClient(
    private val discoveryManager: DiscoveryManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LighthouseClient {

    override fun discoverDevices(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        val foundDevicesFlow = discoveryManager.createNewDeviceFlow(searchRequest)
        val tickFlow = flow {
            while (true) {
                emit(Unit)
                delay(1000)
            }
        }

        return combine(foundDevicesFlow, tickFlow) { devices, _ -> devices }
            .map { devices ->
                devices.filter { device ->
                    device.cache * 1000 > System.currentTimeMillis() - device.latestTimestamp
                }
            }
            .flowOn(dispatcher)
    }
}
