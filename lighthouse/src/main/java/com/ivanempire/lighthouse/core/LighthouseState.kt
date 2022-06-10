package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_NUM
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.MediaDevice
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.removeEmbeddedComponent
import com.ivanempire.lighthouse.updateEmbeddedComponent

// BOOTID.UPNP.ORG     ==> changes, means device will reboot; see how to handle this
// NEXTBOOTID.UPNP.ORG ==> next bootId to use
class LighthouseState {

    private val deviceList = arrayListOf<AbridgedMediaDevice>()

    /**
     *
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
     * @return A modified snapshot of [deviceList] - original list left untouched
     */
    fun parseMediaPacket(latestPacket: MediaPacket): List<AbridgedMediaDevice> {
        return when (latestPacket) {
            is AliveMediaPacket -> parseAliveMediaPacket(latestPacket)
            is UpdateMediaPacket -> parseUpdateMediaPacket(latestPacket)
            is ByeByeMediaPacket -> parseByeByeMediaPacket(latestPacket)
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
     * @return A modified snapshot of [deviceList] with updated information from ALIVE packet
     */
    private fun parseAliveMediaPacket(latestPacket: AliveMediaPacket): List<AbridgedMediaDevice> {
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
                secureLocation = latestPacket.secureLocation,
                latestTimestamp = System.currentTimeMillis()
            )
            baseDevice.updateEmbeddedComponent(latestPacket.bootId, targetComponent)
            deviceList.add(baseDevice)
        } else {
            targetDevice.updateEmbeddedComponent(latestPacket.bootId, targetComponent)
        }
        return deviceList
    }

    private fun parseUpdateMediaPacket(latestPacket: UpdateMediaPacket): List<AbridgedMediaDevice> {
        val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.usn.uuid }
        val targetComponent = latestPacket.usn
        if (targetDevice == null) {
            // Edge-case 1, where UPDATE came before ALIVE - build full device, but specify
            // empty fields
            val baseDevice = AbridgedMediaDevice(
                uuid = targetComponent.uuid,
                host = latestPacket.host,
                cache = NOT_AVAILABLE_NUM, // TODO: this can't be -1, some other default
                bootId = latestPacket.bootId,
                server = null,
                configId = latestPacket.configId,
                location = latestPacket.location,
                searchPort = latestPacket.searchPort,
                secureLocation = latestPacket.secureLocation,
                latestTimestamp = System.currentTimeMillis()
            )
            when (targetComponent) {
                is RootDeviceInformation -> { /* No-op */ }
                else -> baseDevice.updateEmbeddedComponent(latestPacket.bootId, targetComponent)
            }
        } else {
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
                    deviceList.remove(targetDevice)
                    deviceList.add(updatedDevice)
                }
                else -> targetDevice.updateEmbeddedComponent(latestPacket.bootId, targetComponent)
            }
        }
        return deviceList
    }

    /**
     * This is done
     */
    private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket): List<AbridgedMediaDevice> {
        val targetComponent = latestPacket.usn
        val targetDevice = deviceList.firstOrNull { it.uuid == targetComponent.uuid }?.copy() ?: return deviceList

        when (targetComponent) {
            is RootDeviceInformation -> deviceList.remove(targetDevice)
            else -> targetDevice.removeEmbeddedComponent(targetComponent)
        }

        return deviceList
    }

    /**
     * Iterates over all current instances of [MediaDevice] and removes any that have not received
     * advertising packets in the last [AbridgedMediaDevice.cache] milliseconds. First the root
     * device components are removed to save on further processing, then embedded devices and
     * services are pruned
     *
     * @return A device list snapshot with stale root and embedded devices/services removed
     */
    fun parseStaleDevices(): List<AbridgedMediaDevice> {
        return deviceList.filter {
            System.currentTimeMillis() - it.latestTimestamp > it.cache
        }
    }
}
