package com.ivanempire.lighthouse

import android.content.Context
import android.net.wifi.WifiManager
import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.core.RealDiscoveryManager
import com.ivanempire.lighthouse.core.RealLighthouseClient
import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_REQUEST
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.socket.RealSocketListener
import java.lang.IllegalStateException
import kotlinx.coroutines.flow.Flow

/**
 * The main entrypoint for the Lighthouse library
 */
interface LighthouseClient {

    /** Builder class for the Lighthouse configuration */
    class Builder(context: Context) {

        private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        private var retryCount = 1

        private val socketListener = RealSocketListener(wifiManager, retryCount)

        private val discoveryManager = RealDiscoveryManager(
            LighthouseState(),
            socketListener
        )

        fun setRetryCount(retryCount: Int) = apply {
            assert(retryCount > 0) {
                IllegalStateException("Retry count must be greater than 0")
            }
            this.retryCount += retryCount
        }

        fun build(): LighthouseClient {
            return RealLighthouseClient(discoveryManager)
        }
    }

    /**
     * When called, this method will start device discovery by kicking off all of the necessary
     * setup steps down to the socket listener
     *
     * @param searchRequest The [SearchRequest] to send to the multicast group to discover devices
     * @return Flow of lists of [AbridgedMediaDevice] that have been discovered on the network
     */
    fun discoverDevices(searchRequest: SearchRequest = DEFAULT_SEARCH_REQUEST): Flow<List<AbridgedMediaDevice>>
}
