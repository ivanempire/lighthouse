package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_REQUEST
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.flow.Flow

/**
 * The main entrypoint for the Lighthouse library
 */
interface LighthouseClient {

    /**
     * When called, this method will start device discovery by kicking off all of the necessary
     * setup steps down to the socket listener
     *
     * @param searchRequest The [SearchRequest] to send to the multicast group to discover devices
     * @return Flow of lists of [AbridgedMediaDevice] that have been discovered on the network
     */
    fun discoverDevices(searchRequest: SearchRequest = DEFAULT_SEARCH_REQUEST): Flow<List<AbridgedMediaDevice>>

    /**
     * When called, downloads the description from the device and parses the response
     *
     * @param abridgedMediaDevice device to use to request the description
     */
    suspend fun retrieveDescription(abridgedMediaDevice: AbridgedMediaDevice): DetailedMediaDevice
}
