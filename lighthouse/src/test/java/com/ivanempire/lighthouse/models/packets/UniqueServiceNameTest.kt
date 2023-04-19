package com.ivanempire.lighthouse.models.packets

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [UniqueServiceName] */
class UniqueServiceNameTest {

    @Test
    fun `given USN for Amcrest camera correctly parses`() {
        val usnString = "uuid:device_3_0-AMC066F0BC0A3747AF::urn:schemas-upnp-org:device:3.0-AMC066F0BC0A3747AF"
        assertEquals(
            EmbeddedDevice("device_3_0-AMC066F0BC0A3747AF", "3.0-AMC066F0BC0A3747AF", ""),
            UniqueServiceName(usnString)
        )
    }

    @Test
    fun `given root device USN correctly parses`() {
        val usnString = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::upnp:rootdevice"
        val usn = UniqueServiceName(usnString)
        assertEquals(
            RootDeviceInformation("709e1cf4-920d-4aa7-8678-6dab1b6c68a9"),
            usn
        )
    }

    @Test
    fun `given embedded service USN correctly parses`() {
        val usnStringService1 = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::urn:schemas-upnp-org:service:Dimming:1"
        assertEquals(
            EmbeddedService("709e1cf4-920d-4aa7-8678-6dab1b6c68a9", "Dimming", "1"),
            UniqueServiceName(usnStringService1)
        )

        val usnStringService2 = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::urn:schemas-upnp-org:service:SwitchPower:1"
        assertEquals(
            EmbeddedService("709e1cf4-920d-4aa7-8678-6dab1b6c68a9", "SwitchPower", "1"),
            UniqueServiceName(usnStringService2)
        )
    }

    @Test
    fun `given embedded device USN correctly parses`() {
        val usnStringDevice = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::urn:schemas-upnp-org:device:DimmableLight:1"
        assertEquals(
            EmbeddedDevice("709e1cf4-920d-4aa7-8678-6dab1b6c68a9", "DimmableLight", "1"),
            UniqueServiceName(usnStringDevice)
        )
    }
}
