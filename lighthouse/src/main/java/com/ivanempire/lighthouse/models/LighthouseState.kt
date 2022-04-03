package com.ivanempire.lighthouse.models

import android.util.Log
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaService
import com.ivanempire.lighthouse.models.devices.MediaDevice
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceAttribute
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.RootDeviceAttribute
import java.lang.IllegalStateException

// BOOTID.UPNP.ORG     ==> changes, means device will reboot; see how to handle this
// CONFIGID.UPNP.ORG   ==> changes, pull new XML description
// NEXTBOOTID.UPNP.ORG ==> next bootId to use
object LighthouseState {

    private val deviceList = mutableListOf<AbridgedMediaDevice>()

    fun setDeviceList(updatedList: List<AbridgedMediaDevice>): List<MediaDevice> {
        deviceList.clear()
        deviceList.addAll(updatedList)
        return deviceList
    }

    fun parseMediaPacket(latestPacket: MediaPacket): List<MediaDevice> {
        return when (latestPacket) {
            is AliveMediaPacket -> parseAliveMediaPacket(latestPacket)
            // is UpdateMediaPacket -> parseUpdateMediaPacket(latestPacket)
            is ByeByeMediaPacket -> parseByeByeMediaPacket(latestPacket)
            else -> throw IllegalStateException("")
        }
    }

    private fun parseAliveMediaPacket(latestPacket: AliveMediaPacket): List<MediaDevice> {
        /**
         *     val uuid: UUID,
         val location: URL,
         val server: MediaDeviceServer,
         val serviceList: List<AdvertisedMediaService>,
         val deviceList: List<AdvertisedMediaDevice>
         */
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.uuid }
        if (targetDevice == null) {
            var newDevice = AbridgedMediaDevice(
                uuid = latestPacket.uuid,
                location = latestPacket.location,
                server = latestPacket.server
            )
            when (latestPacket.deviceAttribute) {
                is RootDeviceAttribute -> {
                    Log.d("", "Found root device for")
                }
                is EmbeddedDeviceAttribute -> {
                    val embeddedDevice = AdvertisedMediaDevice(
                        deviceType = latestPacket.deviceAttribute.deviceType,
                        deviceVersion = latestPacket.deviceAttribute.deviceVersion
                    )
                    newDevice.deviceList.add(embeddedDevice)
                }
                is EmbeddedServiceAttribute -> {
                    val embeddedService = AdvertisedMediaService(
                        serviceType = latestPacket.deviceAttribute.serviceType,
                        serviceVersion = latestPacket.deviceAttribute.serviceVersion
                    )
                    newDevice.serviceList.add(embeddedService)
                }
                else -> {}
            }
            deviceList.add(newDevice)
            return deviceList
        } else {
        }
    }

    private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket): List<MediaDevice> {
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.uuid } ?: return deviceList
        deviceList.remove(targetDevice)
        return deviceList
    }
}
