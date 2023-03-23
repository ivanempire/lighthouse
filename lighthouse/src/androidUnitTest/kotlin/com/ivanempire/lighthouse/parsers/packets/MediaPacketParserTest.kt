package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.ALIVE_PACKET_NTS_HEADER
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.BYEBYE_PACKET_NTS_HEADER
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.UPDATE_PACKET_NTS_HEADER
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.VALID_ALIVE_PACKET_EXTRA_HEADER_SET
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.VALID_ALIVE_PACKET_HEADER_SET
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests [MediaPacketParser] */
class MediaPacketParserTest {

    @Test
    fun `given ALIVE header set returns instance of ALIVE packet`() {
        val parsedPacket = MediaPacketParser(ALIVE_PACKET_NTS_HEADER)

        assertTrue(parsedPacket is AliveMediaPacket)
        assertTrue(parsedPacket!!.extraHeaders.isEmpty())
    }

    @Test
    fun `given ALIVE header set returns ALIVE packet with correct extra headers`() {
        val parsedPacket = MediaPacketParser(VALID_ALIVE_PACKET_HEADER_SET)

        assertTrue(parsedPacket is AliveMediaPacket)
        assertEquals(1, parsedPacket!!.extraHeaders.size)
        assertEquals(parsedPacket.extraHeaders["Ecosystem.bose.com"], "ECO2")
    }

    @Test
    fun `given UPDATE header set returns instance of UPDATE packet`() {
        val parsedPacket = MediaPacketParser(UPDATE_PACKET_NTS_HEADER)

        assertTrue(parsedPacket is UpdateMediaPacket)
        assertTrue(parsedPacket!!.extraHeaders.isEmpty())
    }

    @Test
    fun `given BYEBYE header set returns instance of BYEBYE packet`() {
        val parsedPacket = MediaPacketParser(BYEBYE_PACKET_NTS_HEADER)

        assertTrue(parsedPacket is ByeByeMediaPacket)
        assertTrue(parsedPacket!!.extraHeaders.isEmpty())
    }

    @Test
    fun `given valid header set parses custom headers correctly`() {
        val parsedPacket = MediaPacketParser(VALID_ALIVE_PACKET_EXTRA_HEADER_SET)

        assertTrue(parsedPacket is AliveMediaPacket)
        assertFalse(parsedPacket!!.extraHeaders.isEmpty())
        assertEquals(4, parsedPacket.extraHeaders.size)
        assertEquals("ECO2", parsedPacket.extraHeaders["Ecosystem.bose.com"])
        assertEquals("whatever", parsedPacket.extraHeaders["custom.header.com"])
        assertEquals("someValue", parsedPacket.extraHeaders["microsoft.com"])
        assertEquals("-1", parsedPacket.extraHeaders["apple.com"])
    }

//    @Test(expected = IllegalStateException::class)
//    fun `given invalid header set throws IllegalStateException`() {
//        val parsedPacket = MediaPacketParser(INVALID_PACKET_NTS_HEADER)
//
//        assertTrue(parsedPacket is ByeByeMediaPacket)
//    }

    object Fixtures {
        val ALIVE_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
        )

        val UPDATE_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:update",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
        )

        val BYEBYE_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:byebye",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
        )

        val INVALID_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:invalid",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
        )

        val VALID_ALIVE_PACKET_HEADER_SET = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.CACHE_CONTROL to "max-age=450",
            HeaderKeys.LOCATION to "http://192.168.2.50:58121/",
            HeaderKeys.SERVER to "Windows/NT/5.0 UPnP/1.0",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:Dimming:1",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::urn:schemas-upnp-org:service:Dimming:1",
            HeaderKeys.BOOT_ID to "5",
            HeaderKeys.CONFIG_ID to "200",
            HeaderKeys.SEARCH_PORT to "2100",
            HeaderKeys.SECURE_LOCATION to "https://192.168.2.50:58121/",
            "Ecosystem.bose.com" to "ECO2",
        )

        val VALID_ALIVE_PACKET_EXTRA_HEADER_SET = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
            "Ecosystem.bose.com" to "ECO2",
            "custom.header.com" to "whatever",
            "microsoft.com" to "someValue",
            "apple.com" to "-1",
        )
    }
}
