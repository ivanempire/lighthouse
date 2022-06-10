package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import java.net.InetAddress
import java.net.URL
import java.util.UUID

object TestUtils {

    fun generateMediaDevice(
        deviceUUID: UUID? = null,
        embeddedDevices: MutableList<EmbeddedDevice>? = null,
        embeddedServices: MutableList<EmbeddedService>? = null,
        cache: Int? = null,
        latestTimestamp: Long? = null
    ): AbridgedMediaDevice {
        return AbridgedMediaDevice(
            uuid = deviceUUID ?: UUID.randomUUID(),
            host = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            cache = cache ?: 0,
            bootId = (Math.random() * 1000).toInt(),
            configId = (Math.random() * 1000).toInt(),
            searchPort = 1900,
            location = URL("http://192.168.2.50:58121/"),
            secureLocation = URL("https://192.168.2.50:58121/"),
            server = SERVER_LIST.random(),
            latestTimestamp = latestTimestamp ?: System.currentTimeMillis(),
            deviceList = embeddedDevices ?: mutableListOf(),
            serviceList = embeddedServices ?: mutableListOf()
        )
    }

    fun generateAdvertisedMediaDevice(): EmbeddedDevice {
        return EmbeddedDevice(
            uuid = UUID.randomUUID(),
            deviceType = "presence",
            bootId = 11,
            deviceVersion = "1",
            domain = "http://www.website.com"
        )
    }

    fun generateAdvertisedMediaService(): EmbeddedService {
        return EmbeddedService(
            uuid = UUID.randomUUID(),
            serviceType = "RenderingControl",
            bootId = 11,
            serviceVersion = "1",
            domain = "http://www.website.com"
        )
    }

    fun generateByeByePacket(
        deviceUUID: UUID,
        uniqueServiceName: UniqueServiceName? = null,
        bootId: Int = 100,
    ): ByeByeMediaPacket {
        return ByeByeMediaPacket(
            host = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            notificationType = NotificationType("upnp:rootdevice"),
            usn = uniqueServiceName ?: UniqueServiceName(deviceUUID.toString(), bootId),
            bootId = bootId,
            configId = 110
        )
    }

    inline fun <reified T : UniqueServiceName> generateUSN(
        deviceUUID: UUID
    ): UniqueServiceName {
        return when (T::class) {
            RootDeviceInformation::class -> {
                UniqueServiceName(deviceUUID.toString(), -1)
            }
            EmbeddedDevice::class -> {
                UniqueServiceName("uuid:$deviceUUID::urn:schemas-microsoft-com:device:presence:1", -1)
            }
            else -> {
                UniqueServiceName("uuid:$deviceUUID::urn:schemas-upnp-org:service:RenderingControl:1", -1)
            }
        }
    }

    private val SERVER_LIST = listOf(
        MediaDeviceServer("Windows", "NT/5.0,", "UPnP/1.0"),
        MediaDeviceServer("N/A", "N/A", "N/A"),
        MediaDeviceServer("Linux/3.18.71+", "UPnP/1.0", "GUPnP/1.0.5")
    )
}
