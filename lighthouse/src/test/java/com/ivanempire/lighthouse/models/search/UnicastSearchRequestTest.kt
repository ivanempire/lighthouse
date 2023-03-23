package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.InetAddress
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [UnicastSearchRequest] creation and conversion */
class UnicastSearchRequestTest {

    @Test(expected = IllegalArgumentException::class)
    fun `given missing ST value throws IllegalArgumentException`() {
        UnicastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            searchTarget = "",
            osVersion = null,
            productVersion = null
        )
    }

    @Test
    fun `given valid values correctly creates string`() {
        val baseSearchRequest = UnicastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            searchTarget = "ssdp:all",
            osVersion = null,
            productVersion = null
        )
        val baseResultString = "${StartLine.SEARCH.rawString}\r\nHOST:239.255.255.250:1900\r\nMAN:\"ssdp:discover\"\r\nST:ssdp:all\r\n\r\n"

        assertEquals(baseResultString, baseSearchRequest.toString())

        val completeSearchRequest = UnicastSearchRequest(
            hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            searchTarget = "ssdp:all",
            osVersion = "Windows/NT5.0",
            productVersion = "GUPnP/1.0.5"
        )

        val completeResultString = "${StartLine.SEARCH.rawString}\r\nHOST:239.255.255.250:1900\r\nMAN:\"ssdp:discover\"\r\nST:ssdp:all\r\nUSER-AGENT:Windows/NT5.0 UPnP/2.0 GUPnP/1.0.5\r\n\r\n"

        assertEquals(completeResultString, completeSearchRequest.toString())
    }
}
