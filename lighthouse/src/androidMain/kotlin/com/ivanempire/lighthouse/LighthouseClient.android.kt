package com.ivanempire.lighthouse

import android.content.Context
import android.net.wifi.WifiManager
import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.core.RealDiscoveryManager
import com.ivanempire.lighthouse.core.RealLighthouseClient
import com.ivanempire.lighthouse.socket.AndroidSocketListener

/**
 * Specify a retry count in the off-chance the network packet is not received by the
 * multicast group due to the nature of UDP
 *
 * @param context Android context
 * @param retryCount Number of times to retry sending an SSDP search packet, must be > 0
 */
fun LighthouseClient(context: Context, retryCount: Int = 1): LighthouseClient {
    require(retryCount > 0) { "Retry count must be greater than 0" }

    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val socketListener = AndroidSocketListener(wifiManager, retryCount)
    val discoveryManager = RealDiscoveryManager(
        LighthouseState(),
        socketListener,
    )

    return RealLighthouseClient(discoveryManager)
}
