package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_REQUEST
import com.ivanempire.lighthouse.models.packets.MediaHost
import java.net.InetAddress
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [SearchRequest] creation and conversion */
class SearchRequestTest {

    @Test(expected = IllegalArgumentException::class)
    fun `given out of range values throws IllegalArgumentException`() {
        MulticastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            mx = 6,
            searchTarget = "ssdp:all",
            osVersion = null,
            productVersion = null,
            friendlyName = "LighthouseClient",
            uuid = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given missing ST value throws IllegalArgumentException`() {
        MulticastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            mx = 5,
            searchTarget = "",
            osVersion = null,
            productVersion = null,
            friendlyName = "LighthouseClient",
            uuid = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given missing FN value throws IllegalArgumentException`() {
        MulticastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            mx = 5,
            searchTarget = "ssdp:all",
            osVersion = null,
            productVersion = null,
            friendlyName = "",
            uuid = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
        )
    }

    @Test
    fun `given valid values correctly creates string`() {
        val baseSearchRequest = MulticastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            mx = 5,
            searchTarget = "ssdp:all",
            osVersion = null,
            productVersion = null,
            friendlyName = "LighthouseClient",
            uuid = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
        )
        val baseResultString = "HOST: 239.255.255.250:1900\\r\\nMAN: \"ssdp:discover\"\\r\\nMX: 5\\r\\nST: ssdp:all\\r\\nCPFN.UPNP.ORG: LighthouseClient\\r\\nCPUUID.UPNP.ORG: 747f550a-8dec-33a1-8470-e314bf440695\\r\\n"

        assertEquals(baseResultString, baseSearchRequest.toString())

        val completeSearchRequest = MulticastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            mx = 5,
            searchTarget = "ssdp:all",
            osVersion = "Windows/NT5.0",
            productVersion = "GUPnP/1.0.5",
            friendlyName = "LighthouseClient",
            uuid = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
        )
        val completeResultString = "HOST: 239.255.255.250:1900\\r\\nMAN: \"ssdp:discover\"\\r\\nMX: 5\\r\\nST: ssdp:all\\r\\nUSER-AGENT: Windows/NT5.0 UPnP/2.0 GUPnP/1.0.5\\r\\nCPFN.UPNP.ORG: LighthouseClient\\r\\nCPUUID.UPNP.ORG: 747f550a-8dec-33a1-8470-e314bf440695\\r\\n"

        assertEquals(completeResultString, completeSearchRequest.toString())
    }

    @Test
    fun `debug`() {
        println(DEFAULT_SEARCH_REQUEST.toString().length)
        print(DEFAULT_SEARCH_REQUEST.toString())
    }
}
