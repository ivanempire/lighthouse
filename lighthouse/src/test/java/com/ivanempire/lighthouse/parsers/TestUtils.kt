package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import java.net.InetAddress
import java.net.URL
import java.util.UUID

/** Functions to generate fake data to use in unit testing */
object TestUtils {

    /**
     * Generates an instance of [AbridgedMediaDevice] to use during unit testing. Defaults to a
     * simple root device if embedded components are not specified.
     *
     * @param deviceUUID The device [UUID] to use during creation
     * @param mediaHost The [MediaHost] to use during device creation
     * @param embeddedDevices List of [EmbeddedDevice] instances to add to the media device
     * @param embeddedServices List of [EmbeddedService] instances to add to the media device
     * @param cache TTL in seconds of the media device
     * @param latestTimestamp Timestamp of the latest received media packet targeting this device
     * @return An instance of [AbridgedMediaDevice] to use during unit testing
     */
    fun generateMediaDevice(
        deviceUUID: String? = null,
        mediaHost: MediaHost? = null,
        embeddedDevices: MutableList<EmbeddedDevice>? = null,
        embeddedServices: MutableList<EmbeddedService>? = null,
        cache: Int? = null,
        latestTimestamp: Long? = null,
    ): AbridgedMediaDevice {
        return AbridgedMediaDevice(
            uuid = deviceUUID ?: UUID.randomUUID().toString(),
            host = mediaHost ?: MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            cache = cache ?: 0,
            bootId = (Math.random() * 1000).toInt(),
            configId = (Math.random() * 1000).toInt(),
            searchPort = 1900,
            location = URL("http://192.168.2.50:58121/"),
            secureLocation = URL("https://192.168.2.50:58121/"),
            mediaDeviceServer = SERVER_LIST.random(),
            latestTimestamp = latestTimestamp ?: System.currentTimeMillis(),
            deviceList = embeddedDevices ?: mutableListOf(),
            serviceList = embeddedServices ?: mutableListOf(),
        )
    }

    /** Generates an instance of [AliveMediaPacket] */
    internal fun generateAlivePacket(
        host: MediaHost = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
        cache: Int = 1900,
        location: URL = URL("http://127.0.0.1:58122/"),
        server: MediaDeviceServer = SERVER_LIST.random(),
        deviceUUID: String,
        uniqueServiceName: UniqueServiceName? = null,
        bootId: Int = 100,
        configId: Int = 130,
        searchPort: Int = 1900,
        secureLocation: URL = URL("https://127.0.0.1:58122/"),
    ): AliveMediaPacket {
        return AliveMediaPacket(
            host = host,
            cache = cache,
            location = location,
            notificationType = NotificationType("upnp:rootdevice"),
            server = server,
            usn = uniqueServiceName ?: UniqueServiceName("uuid:$deviceUUID"),
            bootId = bootId,
            configId = configId,
            searchPort = searchPort,
            secureLocation = secureLocation,
        )
    }

    /** Generates an instance of [UpdateMediaPacket] */
    internal fun generateUpdatePacket(
        host: MediaHost = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
        location: URL = URL("http://192.168.2.50:58121/"),
        deviceUUID: String,
        uniqueServiceName: UniqueServiceName? = null,
        bootId: Int = 100,
        configId: Int = 110,
        searchPort: Int = 1900,
        secureLocation: URL = URL("https://192.168.2.50:58121/"),
    ): UpdateMediaPacket {
        return UpdateMediaPacket(
            host = host,
            location = location,
            notificationType = NotificationType("upnp:rootdevice"),
            usn = uniqueServiceName ?: UniqueServiceName("uuid:$deviceUUID"),
            bootId = bootId,
            configId = configId,
            nextBootId = bootId + 1,
            searchPort = searchPort,
            secureLocation = secureLocation,
        )
    }

    /** Generates an instance of [ByeByeMediaPacket] */
    internal fun generateByeByePacket(
        host: MediaHost = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
        deviceUUID: String,
        uniqueServiceName: UniqueServiceName? = null,
        bootId: Int = 100,
        configId: Int = 110,
    ): ByeByeMediaPacket {
        return ByeByeMediaPacket(
            host = host,
            notificationType = NotificationType("upnp:rootdevice"),
            usn = uniqueServiceName ?: UniqueServiceName("uuid:$deviceUUID"),
            bootId = bootId,
            configId = configId,
        )
    }

    /**
     * Generates an instance of [UniqueServiceName] targeting either a root device, or an embedded
     * component - device or service.
     *
     * @param deviceUUID The device [UUID] to target
     * @param identifier The name of the component to create
     * @param version The version string of the component to create
     * @return An instance of [UniqueServiceName] to use in unit testing
     */
    internal inline fun <reified T : UniqueServiceName> generateUSN(
        deviceUUID: String,
        identifier: String = "RenderingControl",
        version: String = "3.0",
        domain: String = "schemas-upnp-org",
    ): UniqueServiceName {
        return when (T::class) {
            RootDeviceInformation::class -> {
                UniqueServiceName("uuid:$deviceUUID")
            }
            EmbeddedDevice::class -> {
                UniqueServiceName("uuid:$deviceUUID::urn:$domain:device:$identifier:$version")
            }
            EmbeddedService::class -> {
                UniqueServiceName("uuid:$deviceUUID::urn:$domain:service:$identifier:$version")
            }
            else -> {
                UniqueServiceName("uuid:$deviceUUID::urn:$domain:service:$identifier:$version")
            }
        }
    }

    private val SERVER_LIST =
        listOf(
            MediaDeviceServer("Windows", "NT/5.0,", "UPnP/1.0"),
            MediaDeviceServer("N/A", "N/A", "N/A"),
            MediaDeviceServer("Linux/3.18.71+", "UPnP/1.0", "GUPnP/1.0.5"),
        )
}
