package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_CACHE
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.MediaDevice
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.SearchResponseMediaPacket
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.removeEmbeddedComponent
import com.ivanempire.lighthouse.updateEmbeddedComponent

/**
 * The one stateful component of Lighthouse - contains the list of all devices known to the library
 * at any given time
 */
class LighthouseState {

    private val deviceList = arrayListOf<AbridgedMediaDevice>()

    /**
     * Must be called from one place only so as not to mess up internal state. Clears the internal
     * state of all devices and adds everything back from [updatedList]
     *
     * @param updatedList The list of media devices that have been updated with the latest packets
     * @return A new list of media devices that are as up-to-date as possible given latest packets
     */
    fun setDeviceList(updatedList: List<AbridgedMediaDevice>): List<MediaDevice> {
        deviceList.clear()
        deviceList.addAll(updatedList)
        return deviceList
    }

    /**
     * Delegates all the latest incoming [MediaPacket] instances to the relevant parsing methods
     *
     * @param latestPacket The latest parsed instance of a [MediaPacket]
     * @return A new version of [deviceList] with the appropriate [AbridgedMediaDevice] updated
     */
    fun parseMediaPacket(latestPacket: MediaPacket): List<AbridgedMediaDevice> {
        return when (latestPacket) {
            is AliveMediaPacket -> parseAliveMediaPacket(latestPacket)
            is UpdateMediaPacket -> parseUpdateMediaPacket(latestPacket)
            is ByeByeMediaPacket -> parseByeByeMediaPacket(latestPacket)
            is SearchResponseMediaPacket -> parseAliveMediaPacket(latestPacket.toAlivePacket())
        }
    }

    /**
     * Handles the latest parsed instance of an [AliveMediaPacket] and either creates a new
     * [AbridgedMediaDevice] to add to the existing list, or updates any embedded components that
     * said packet may target. Since ALIVE packets follow the sending equation of 3+2d+k (where
     * a root device has d embedded devices, and k embedded services) the follow-up root packets
     * are ignored.
     *
     * @param latestPacket Latest instance of an [AliveMediaPacket]
     * @return A new version of [deviceList] with updated information from ALIVE packet
     */
    private fun parseAliveMediaPacket(latestPacket: AliveMediaPacket): List<AbridgedMediaDevice> {
        var targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.usn.uuid }
        val targetComponent = latestPacket.usn

        // Create a new device since we haven't seen it yet
        if (targetDevice == null) {
            val baseDevice = AbridgedMediaDevice(
                uuid = targetComponent.uuid,
                host = latestPacket.host,
                cache = latestPacket.cache,
                bootId = latestPacket.bootId,
                mediaDeviceServer = latestPacket.server,
                configId = latestPacket.configId,
                location = latestPacket.location,
                searchPort = latestPacket.searchPort,
                secureLocation = latestPacket.secureLocation,
                latestTimestamp = System.currentTimeMillis()
            )
            baseDevice.updateEmbeddedComponent(targetComponent)
            deviceList.add(baseDevice)
        } else {
            // Update the existing device
            targetDevice = targetDevice.copy(latestTimestamp = System.currentTimeMillis())
            targetDevice.updateEmbeddedComponent(targetComponent)
        }
        targetDevice?.extraHeaders?.putAll(latestPacket.extraHeaders)
        return deviceList
    }

    /**
     * Handles the latest parsed instance of an [UpdateMediaPacket] and either creates a new device
     * (since UDP does not guarantee packet order), or updates the corresponding component that said
     * packet may target
     *
     * @param latestPacket Latest instance of an [UpdateMediaPacket]
     * @return A new version of [deviceList] with updated information from UPDATE packet
     */
    private fun parseUpdateMediaPacket(latestPacket: UpdateMediaPacket): List<AbridgedMediaDevice> {
        var targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.usn.uuid }
        val targetComponent = latestPacket.usn
        if (targetDevice == null) {
            // Edge-case 1, where UPDATE packet came before ALIVE - build full device, but specify
            // empty fields
            val baseDevice = AbridgedMediaDevice(
                uuid = targetComponent.uuid,
                host = latestPacket.host,
                cache = NOT_AVAILABLE_CACHE,
                bootId = latestPacket.bootId,
                mediaDeviceServer = null,
                configId = latestPacket.configId,
                location = latestPacket.location,
                searchPort = latestPacket.searchPort,
                secureLocation = latestPacket.secureLocation,
                latestTimestamp = System.currentTimeMillis()
            )
            baseDevice.updateEmbeddedComponent(targetComponent)
            deviceList.add(baseDevice)
        } else {
            deviceList.remove(targetDevice)
            targetDevice = targetDevice.copy(latestTimestamp = System.currentTimeMillis())
            when (targetComponent) {
                // ALIVE came first, UPDATE for root should only update certain fields
                // cache and server are not affected
                is RootDeviceInformation -> {
                    val updatedDevice = targetDevice.copy(
                        bootId = latestPacket.bootId,
                        configId = latestPacket.configId,
                        searchPort = latestPacket.searchPort,
                        location = latestPacket.location,
                        secureLocation = latestPacket.secureLocation,
                    )
                    deviceList.add(updatedDevice)
                }
                else -> targetDevice.updateEmbeddedComponent(targetComponent)
            }
        }
        targetDevice?.extraHeaders?.putAll(latestPacket.extraHeaders)
        return deviceList
    }

    /**
     * Handles the latest parsed instance of a [ByeByeMediaPacket] and removes the embedded
     * component or root device that said packet may target
     *
     * @param latestPacket Latest instance of a [ByeByeMediaPacket]
     * @return A modified version of [deviceList] with the target device/component removed
     */
    private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket): List<AbridgedMediaDevice> {
        val targetComponent = latestPacket.usn
        val targetDevice = deviceList.firstOrNull { it.uuid == targetComponent.uuid } ?: return deviceList

        when (targetComponent) {
            is RootDeviceInformation -> deviceList.remove(targetDevice)
            else -> targetDevice.removeEmbeddedComponent(targetComponent)
        }

        return deviceList
    }

    /**
     * Iterates over the entire device list and filters out any stale devices. A stale device is
     * defined as one that has not seen a media packet in the last [AbridgedMediaDevice.cache]
     * seconds
     *
     * @return A device list with stale root devices removed
     */
    fun parseStaleDevices(): List<AbridgedMediaDevice> {
        return deviceList.filter {
            System.currentTimeMillis() - it.latestTimestamp > it.cache * 1000
        }
    }
}

/**
 * Converts a [SearchResponseMediaPacket] to an [AliveMediaPacket] since the parsing is almost
 * identical between the two with regards to the creation of an [AbridgedMediaDevice]
 */
private fun SearchResponseMediaPacket.toAlivePacket(): AliveMediaPacket {
    return AliveMediaPacket(
        host = host,
        cache = cache,
        location = location,
        notificationType = notificationType,
        server = server,
        usn = usn,
        bootId = bootId,
        configId = configId,
        searchPort = searchPort,
        secureLocation = secureLocation
    )
}
