package com.ivanempire.lighthouse.models

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.DatagramPacket

/** Tests [SearchRequest] creation */
class SearchRequestTest {

    @Test
    fun `given valid values correctly builds unicast search request`() {
        val searchRequest = UnicastSearchRequest(
            hostname = "239.255.255.250",
            portNumber = 1900,
            searchTarget = "ssdp:all",
            osVersion = "Windows/NT/5.0",
            productVersion = "GUPnP/1.0.5"
        )
        val properString = "HOST: 239.255.255.250:1900\n" +
                "MAN: \"ssdp:discover\"\n" +
                "ST: ssdp:all\n" +
                "USER-AGENT: Windows/NT/5.0 UPnP/2.0 GUPnP/1.0.5\n"
        assertEquals(properString, searchRequest.toString())
        assertArrayEquals(DatagramPacket(
            "M-SEARCH * HTTP/1.1\n$properString".toByteArray(),
            properString.length
        ).data, searchRequest.toDatagramPacket().data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given invalid hostname value correctly throws IllegalArgumentException during creation`() {
        UnicastSearchRequest(
            hostname = "239.255.255.250",
            portNumber = 1900,
            searchTarget = "",
            osVersion = "Windows/NT/5.0",
            productVersion = "GUPnP/1.0.5"
        )
    }
    @Test(expected = IllegalArgumentException::class)
    fun `given invalid searchTarget value correctly throws IllegalArgumentException during creation`() {
        UnicastSearchRequest(
            hostname = "239.255.255.250",
            portNumber = 1900,
            searchTarget = "",
            osVersion = "Windows/NT/5.0",
            productVersion = "GUPnP/1.0.5"
        )
    }
}