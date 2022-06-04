package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaService
import com.ivanempire.lighthouse.models.devices.MediaDevice
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceInformation
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.removeEmbeddedComponent
import com.ivanempire.lighthouse.updateEmbeddedComponent

// BOOTID.UPNP.ORG     ==> changes, means device will reboot; see how to handle this
// CONFIGID.UPNP.ORG   ==> changes, pull new XML description
// NEXTBOOTID.UPNP.ORG ==> next bootId to use
object LighthouseState {

    private val deviceList = arrayListOf<AbridgedMediaDevice>()

    fun setDeviceList(updatedList: List<AbridgedMediaDevice>): List<MediaDevice> {
        deviceList.clear()
        deviceList.addAll(updatedList)
        return deviceList
    }

    fun parseMediaPacket(latestPacket: MediaPacket): List<MediaDevice> {
        return when (latestPacket) {
            is AliveMediaPacket -> parseAliveMediaPacket(latestPacket)
            is UpdateMediaPacket -> parseUpdateMediaPacket(latestPacket)
            is ByeByeMediaPacket -> parseByeByeMediaPacket(latestPacket)
        }
    }

    private fun parseUpdateMediaPacket(latestPacket: UpdateMediaPacket): List<MediaDevice> {
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.usn.uuid }
        val targetComponent = latestPacket.usn
        if (targetDevice == null) {
            // If new device, create a new device - components as well
        } else {
            // If existing device, overwrite components, uuuh check what happens to root
            /**
             *     override val host: MediaHost,
             val location: URL?,
             override val notificationType: NotificationType,
             override val notificationSubtype: NotificationSubtype = NotificationSubtype.UPDATE,
             override val usn: UniqueServiceName,
             override val bootId: Int?,
             override val configId: Int?,
             val nextBootId: Int?,
             val searchPort: Int?,
             val secureLocation: URL?
             */
            when (targetComponent) {
                is RootDeviceInformation -> {
                }
                else -> {
                    targetDevice.updateEmbeddedComponent(latestPacket.bootId, targetComponent)
                }
            }
        }
        return emptyList()
    }

    /**
     * I think this is done - check version increment in ALIVE packets (follow-up case)
     */
    private fun parseAliveMediaPacket(latestPacket: AliveMediaPacket): List<MediaDevice> {
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.usn.uuid }
        val targetComponent = latestPacket.usn
        if (targetDevice == null) {
            val baseDevice = AbridgedMediaDevice(
                uuid = targetComponent.uuid,
                host = latestPacket.host,
                cache = latestPacket.cache,
                bootId = latestPacket.bootId,
                server = latestPacket.server,
                configId = latestPacket.configId,
                location = latestPacket.location,
                searchPort = latestPacket.searchPort,
                secureLocation = latestPacket.secureLocation
            )
            when (targetComponent) {
                is RootDeviceInformation -> { /* No-op */ }
                is EmbeddedDeviceInformation -> {
                    baseDevice.deviceList.add(
                        AdvertisedMediaDevice(
                            deviceType = targetComponent.deviceType,
                            deviceVersion = targetComponent.deviceType,
                            domain = targetComponent.domain,
                            bootId = latestPacket.bootId
                        )
                    )
                }
                is EmbeddedServiceInformation -> {
                    baseDevice.serviceList.add(
                        AdvertisedMediaService(
                            serviceType = targetComponent.serviceType,
                            serviceVersion = targetComponent.serviceVersion,
                            domain = targetComponent.domain,
                            bootId = latestPacket.bootId
                        )
                    )
                }
            }
            deviceList.add(baseDevice)
        } else {
            when (targetComponent) {
                is RootDeviceInformation -> { /* No-op */ }
                is EmbeddedDeviceInformation -> {
                    val presentEmbedded = targetDevice.deviceList.firstOrNull { it.deviceType == targetComponent.deviceType }
                    if (presentEmbedded == null) {
                        targetDevice.deviceList.add(
                            AdvertisedMediaDevice(
                                deviceType = targetComponent.deviceType,
                                deviceVersion = targetComponent.deviceVersion,
                                domain = targetComponent.domain,
                                bootId = latestPacket.bootId
                            )
                        )
                    }
                }
                is EmbeddedServiceInformation -> {
                    val presentEmbedded = targetDevice.deviceList.firstOrNull { it.deviceType == targetComponent.serviceType }
                    if (presentEmbedded == null) {
                        targetDevice.deviceList.add(
                            AdvertisedMediaDevice(
                                deviceType = targetComponent.serviceType,
                                deviceVersion = targetComponent.serviceVersion,
                                domain = targetComponent.domain,
                                bootId = latestPacket.bootId
                            )
                        )
                    }
                }
            }
        }
        return deviceList
    }

    /**
     * This is done
     */
    private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket): List<MediaDevice> {
        val targetComponent = latestPacket.usn
        val targetDevice = deviceList.firstOrNull { it.uuid == targetComponent.uuid } ?: return deviceList

        when (targetComponent) {
            is RootDeviceInformation -> deviceList.remove(targetDevice)
            else -> targetDevice.removeEmbeddedComponent(targetComponent)
        }

        return deviceList
    }
}
