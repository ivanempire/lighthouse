package com.ivanempire.lighthouse.models

import android.util.Log
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaService
import com.ivanempire.lighthouse.models.devices.MediaDevice
import com.ivanempire.lighthouse.models.devices.addIfNew
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.DeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceAttribute
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.RootDeviceAttribute
import java.lang.IllegalStateException

// BOOTID.UPNP.ORG     ==> changes, means device will reboot; see how to handle this
// CONFIGID.UPNP.ORG   ==> changes, pull new XML description
// NEXTBOOTID.UPNP.ORG ==> next bootId to use
object LighthouseState {

    private val TAG = this::class.java.simpleName

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
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.uuid }

        return if (targetDevice == null) {
            val baseDevice = AbridgedMediaDevice(
                uuid = latestPacket.uuid,
                location = latestPacket.location,
                server = latestPacket.server
            )
            val updatedDevice = updateDeviceAttributes(baseDevice, latestPacket.deviceAttribute)
            deviceList.add(updatedDevice)
            deviceList
        } else {
            val updatedDevice = updateDeviceAttributes(targetDevice, latestPacket.deviceAttribute)
            deviceList.add(updatedDevice)
            deviceList
        }
    }

    private fun updateDeviceAttributes(
        baseDevice: AbridgedMediaDevice,
        latestAttribute: DeviceAttribute?
    ): AbridgedMediaDevice {
        if (latestAttribute == null) {
            return baseDevice
        }
        when (latestAttribute) {
            is RootDeviceAttribute -> {
                Log.d(TAG, "Found root device for UUID: ${baseDevice.uuid}")
            }
            is EmbeddedDeviceAttribute -> {
                val embeddedDevice = AdvertisedMediaDevice(
                    deviceType = latestAttribute.deviceType,
                    deviceVersion = latestAttribute.deviceVersion
                )
                baseDevice.deviceList.addIfNew(embeddedDevice)
            }
            is EmbeddedServiceAttribute -> {
                val embeddedService = AdvertisedMediaService(
                    serviceType = latestAttribute.serviceType,
                    serviceVersion = latestAttribute.serviceVersion
                )
                baseDevice.serviceList.addIfNew(embeddedService)
            }
        }
        return baseDevice
    }

    // Ah damn this needs to handle service removal too
    private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket): List<MediaDevice> {
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.uuid } ?: return deviceList
        deviceList.remove(targetDevice)
        return deviceList
    }
}
