package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.ALIVE_PACKET_NTS_HEADER
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.BYEBYE_PACKET_NTS_HEADER
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParserTest.Fixtures.UPDATE_PACKET_NTS_HEADER
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests [MediaPacketParser] */
class MediaPacketParserTest {

    @Test
    fun `given ALIVE header set returns instance of ALIVE packet`() {
        val parsedPacket = MediaPacketParser(ALIVE_PACKET_NTS_HEADER)

        assertTrue(parsedPacket is AliveMediaPacket)
    }

    @Test
    fun `given UPDATE header set returns instance of UPDATE packet`() {
        val parsedPacket = MediaPacketParser(UPDATE_PACKET_NTS_HEADER)

        assertTrue(parsedPacket is UpdateMediaPacket)
    }

    @Test
    fun `given BYEBYE header set returns instance of BYEBYE packet`() {
        val parsedPacket = MediaPacketParser(BYEBYE_PACKET_NTS_HEADER)

        assertTrue(parsedPacket is ByeByeMediaPacket)
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
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice"
        )

        val UPDATE_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:update",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice"
        )

        val BYEBYE_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:byebye",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice"
        )

        val INVALID_PACKET_NTS_HEADER = hashMapOf(
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:invalid",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice"
        )
    }
}
