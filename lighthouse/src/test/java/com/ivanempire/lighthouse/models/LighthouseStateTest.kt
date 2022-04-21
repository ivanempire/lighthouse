// package com.ivanempire.lighthouse.models
//
// import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SHELL_BYEBYE_PACKET
// import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
// import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
// import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
// import java.util.UUID
// import org.junit.Assert.assertTrue
// import org.junit.Test
// import org.mockito.Mockito.mock
// import org.mockito.Mockito.`when`
//
// class LighthouseStateTest {
//
//    private lateinit var sut: LighthouseState
//
//    @Test
//    fun `handles bye-bye packets correctly`() {
//        sut = LighthouseState
//        sut.setDeviceList(emptyList())
//
//        `when`(SHELL_BYEBYE_PACKET.uuid).thenReturn(UUID.randomUUID())
//        val updatedList = sut.parseMediaPacket(SHELL_BYEBYE_PACKET)
//        assertTrue(updatedList.isEmpty())
//    }
//
//    object Fixtures {
//        val SHELL_ALIVE_PACKET = mock(AliveMediaPacket::class.java)
//        val SHELL_UPDATE_PACKET = mock(UpdateMediaPacket::class.java)
//        val SHELL_BYEBYE_PACKET = mock(ByeByeMediaPacket::class.java)
//    }
// }
