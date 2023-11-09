package com.ivanempire.lighthouse.models.packets

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [UniqueServiceName] */
class UniqueServiceNameTest {

    @Test
    fun `given root device USN correctly parses`() {
        val usnString1 = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::upnp:rootdevice"
        val usn = UniqueServiceName(usnString1)
        assertEquals(
            RootDeviceInformation("709e1cf4-920d-4aa7-8678-6dab1b6c68a9"),
            usn,
        )

        val usnString2 = "uuid:66363c8f-408d-47eb-9c5b-9d5301eb93d3::upnp:rootdevice"
        assertEquals(
            RootDeviceInformation(
                uuid = "66363c8f-408d-47eb-9c5b-9d5301eb93d3",
            ),
            UniqueServiceName(usnString2),
        )
    }

    @Test
    fun `given embedded service USN correctly parses`() {
        val usnStringService1 = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::urn:schemas-upnp-org:service:Dimming:1"
        assertEquals(
            EmbeddedService("709e1cf4-920d-4aa7-8678-6dab1b6c68a9", "Dimming", "1"),
            UniqueServiceName(usnStringService1),
        )

        val usnStringService2 = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::urn:schemas-upnp-org:service:SwitchPower:1"
        assertEquals(
            EmbeddedService("709e1cf4-920d-4aa7-8678-6dab1b6c68a9", "SwitchPower", "1"),
            UniqueServiceName(usnStringService2),
        )
    }

    @Test
    fun `given embedded device USN correctly parses`() {
        val usnStringDevice = "uuid:709e1cf4-920d-4aa7-8678-6dab1b6c68a9::urn:schemas-upnp-org:device:DimmableLight:1"
        assertEquals(
            EmbeddedDevice("709e1cf4-920d-4aa7-8678-6dab1b6c68a9", "DimmableLight", "1"),
            UniqueServiceName(usnStringDevice),
        )
    }

    @Test
    fun `given USN with domain correctly parses`() {
        val usnString = "uuid:00000000-0000-0000-0200-00125A8A0960::urn:schemas-microsoft-com:device:presence:1"
        assertEquals(
            EmbeddedDevice(
                uuid = "00000000-0000-0000-0200-00125A8A0960",
                deviceType = "presence",
                deviceVersion = "1",
                domain = "schemas-microsoft-com",
            ),
            UniqueServiceName(usnString),
        )
    }

    @Test
    fun `given complex USN 1 correctly parses`() {
        val usnString = "uuid:ebf5a0a0-1dd1-11b2-a90f-e0dbd1eb5ad2::urn:schemas-upnp-org:service:DiscoverFriendlies:1"
        assertEquals(
            EmbeddedService(
                uuid = "ebf5a0a0-1dd1-11b2-a90f-e0dbd1eb5ad2",
                serviceType = "DiscoverFriendlies",
                serviceVersion = "1",
            ),
            UniqueServiceName(usnString),
        )
    }

    @Test
    fun `given complex USN 2 correctly parses`() {
        val usnString = "uuid:5f6085cb-5c18-40c6-a299-dbc05135b0c4::urn:samsung.com:service:ScreenSharingService:1"
        assertEquals(
            EmbeddedService(
                uuid = "5f6085cb-5c18-40c6-a299-dbc05135b0c4",
                serviceType = "ScreenSharingService",
                domain = "samsung.com",
                serviceVersion = "1",
            ),
            UniqueServiceName(usnString),
        )
    }

    @Test
    fun `given USN for Amcrest camera correctly parses`() {
        val usnString = "uuid:device_3_0-AMC066F0BC0A3747AF::urn:schemas-upnp-org:device:3.0-AMC066F0BC0A3747AF"
        assertEquals(
            EmbeddedDevice("device_3_0-AMC066F0BC0A3747AF", "3.0-AMC066F0BC0A3747AF", ""),
            UniqueServiceName(usnString),
        )
    }
}
