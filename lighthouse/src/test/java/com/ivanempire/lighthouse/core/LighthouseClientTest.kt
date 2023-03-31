package com.ivanempire.lighthouse.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.DatagramPacket

class LighthouseClientTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an expiring device discoverDevices returns active devices`() = runTest {
        val startTime = 1_680_267_973_881
        val timeFlow = flow {
            emit(startTime)
            emit(startTime + 3_000_000)
        }
        val socketListener = FakeSocketListener(
            listOf(
                Fixtures.PACKET1,
                Fixtures.PACKET2,
                Fixtures.PACKET3
            )
        )
        val discoveryManager = RealDiscoveryManager(LighthouseState(), socketListener, timeFlow) { startTime }
        val client = RealLighthouseClient(discoveryManager)
        val devicesFlow = client.discoverDevices()
        assertEquals(3, devicesFlow.first().size) // initial list
        assertEquals(2, devicesFlow.first().size) // list 3000 seconds later, after PACKET1 has gone stale
    }

    object Fixtures {
        val PACKET1 = DatagramPacket(
            "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=1800\r\nLocation: http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
            386
        )
        val PACKET2 = DatagramPacket(
            "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=3600\r\nLocation: http://192.168.1.191:8091/97dd174d-75b0-41b4-b678-687f286b28f2.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:97dd174d-75b0-41b4-b678-687f286b28f2::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
            386
        )
        val PACKET3 = DatagramPacket(
            "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=3600\r\nLocation: http://192.168.1.191:8091/d7e635e0-104a-4745-95aa-d4afb459062d.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:d7e635e0-104a-4745-95aa-d4afb459062d::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
            386
        )
    }
}