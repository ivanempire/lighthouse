package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.InetAddress
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [MulticastSearchRequest] creation and conversion */
class MulticastSearchRequestTest {

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
        val baseResultString = "${StartLine.SEARCH.rawString}\r\nHOST:239.255.255.250:1900\r\nMAN:\"ssdp:discover\"\r\nMX:5\r\nST:ssdp:all\r\nCPFN.UPNP.ORG:LighthouseClient\r\nCPUUID.UPNP.ORG:747f550a-8dec-33a1-8470-e314bf440695\r\n"

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
        val completeResultString = "${StartLine.SEARCH.rawString}\r\nHOST:239.255.255.250:1900\r\nMAN:\"ssdp:discover\"\r\nMX:5\r\nST:ssdp:all\r\nUSER-AGENT:Windows/NT5.0 UPnP/2.0 GUPnP/1.0.5\r\nCPFN.UPNP.ORG:LighthouseClient\r\nCPUUID.UPNP.ORG:747f550a-8dec-33a1-8470-e314bf440695\r\n"

        assertEquals(completeResultString, completeSearchRequest.toString())
    }
}
