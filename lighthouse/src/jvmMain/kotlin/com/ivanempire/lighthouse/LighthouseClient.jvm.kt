package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.core.RealDiscoveryManager
import com.ivanempire.lighthouse.core.RealLighthouseClient
import com.ivanempire.lighthouse.socket.RealSocketListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Specify a retry count in the off-chance the network packet is not received by the
 * multicast group due to the nature of UDP
 *
 * @param logger SLF4J logger
 * @param retryCount Number of times to retry sending an SSDP search packet, must be > 0
 */
fun LighthouseClient(
    logger: Logger = LoggerFactory.getLogger("LighthouseClient"),
    retryCount: Int = 1,
): LighthouseClient {
    require(retryCount > 0) { "Retry count must be greater than 0" }

    val socketListener = RealSocketListener(logger, retryCount)
    val discoveryManager = RealDiscoveryManager(
        LighthouseState(),
        socketListener
    )

    return RealLighthouseClient(discoveryManager)
}