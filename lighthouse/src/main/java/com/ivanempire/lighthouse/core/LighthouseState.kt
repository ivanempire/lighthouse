package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_CACHE
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.SearchResponseMediaPacket
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.removeEmbeddedComponent
import com.ivanempire.lighthouse.updateEmbeddedComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The one stateful component of Lighthouse - contains the list of all devices known to the library
 * at any given time
 */
internal class LighthouseState(private val logger: LighthouseLogger? = null) {

    private val backingDeviceList = MutableStateFlow<List<AbridgedMediaDevice>>(emptyList())
    val deviceList: StateFlow<List<AbridgedMediaDevice>> = backingDeviceList.asStateFlow()

    /**
     * Delegates all the latest incoming [MediaPacket] instances to the relevant parsing methods
     *
     * @param latestPacket The latest parsed instance of a [MediaPacket]
     * @return A new version of [deviceList] with the appropriate [AbridgedMediaDevice] updated
     */
    fun parseMediaPacket(latestPacket: MediaPacket) {
        logger?.logStateMessage(TAG, "Parsing packet for state: $latestPacket")
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
     * said packet may target. Since ALIVE packets follow the sending equation of 3+2d+k (where a
     * root device has d embedded devices, and k embedded services) the follow-up root packets are
     * ignored.
     *
     * @param latestPacket Latest instance of an [AliveMediaPacket]
     * @return A new version of [deviceList] with updated information from ALIVE packet
     */
    private fun parseAliveMediaPacket(latestPacket: AliveMediaPacket) {
        val updatedList = backingDeviceList.value.toMutableList()
        val targetIndex = updatedList.indexOfFirst { it.uuid == latestPacket.usn.uuid }

        val targetComponent = latestPacket.usn
        //        logger?.logStateMessage(TAG, "Parsing ALIVE packet targeting $targetComponent on
        // device $targetDevice")

        // Create a new device since we haven't seen it yet
        if (targetIndex != -1) {
            val updatedDevice =
                updatedList[targetIndex]
                    .copy(latestTimestamp = System.currentTimeMillis())
                    .updateEmbeddedComponent(targetComponent)
            updatedList[targetIndex] = updatedDevice
        } else {
            updatedList.add(
                AbridgedMediaDevice(
                        uuid = targetComponent.uuid,
                        host = latestPacket.host,
                        cache = latestPacket.cache,
                        bootId = latestPacket.bootId,
                        mediaDeviceServer = latestPacket.server,
                        configId = latestPacket.configId,
                        location = latestPacket.location,
                        searchPort = latestPacket.searchPort,
                        secureLocation = latestPacket.secureLocation,
                        latestTimestamp = System.currentTimeMillis(),
                    )
                    .updateEmbeddedComponent(targetComponent),
            )
        }

        backingDeviceList.value = updatedList
    }

    /**
     * Handles the latest parsed instance of an [UpdateMediaPacket] and either creates a new device
     * (since UDP does not guarantee packet order), or updates the corresponding component that said
     * packet may target
     *
     * @param latestPacket Latest instance of an [UpdateMediaPacket]
     * @return A new version of [deviceList] with updated information from UPDATE packet
     */
    private fun parseUpdateMediaPacket(latestPacket: UpdateMediaPacket) {
        val targetComponent = latestPacket.usn
        val updatedList = backingDeviceList.value.toMutableList()
        val targetIndex = updatedList.indexOfFirst { it.uuid == latestPacket.usn.uuid }

        // logger?.logStateMessage(TAG, "Parsing UPDATE packet targeting $targetComponent on device
        // ${updatedDeviceList.getOrNull(targetIndex)}")

        val updatedDevice =
            if (targetIndex == -1) {
                // Edge-case 1: UPDATE packet came before ALIVE - build full device, but specify
                // empty fields
                AbridgedMediaDevice(
                        uuid = targetComponent.uuid,
                        host = latestPacket.host,
                        cache = NOT_AVAILABLE_CACHE,
                        bootId = latestPacket.bootId,
                        mediaDeviceServer = null,
                        configId = latestPacket.configId,
                        location = latestPacket.location,
                        searchPort = latestPacket.searchPort,
                        secureLocation = latestPacket.secureLocation,
                        latestTimestamp = System.currentTimeMillis(),
                    )
                    .updateEmbeddedComponent(targetComponent)
            } else {
                // ALIVE came first, UPDATE for root should only update certain fields (cache and
                // server are not affected)
                val existingDevice = updatedList[targetIndex]
                val baseUpdatedDevice =
                    existingDevice.copy(latestTimestamp = System.currentTimeMillis()).apply {
                        extraHeaders.putAll(latestPacket.extraHeaders)
                    }
                when (targetComponent) {
                    is RootDeviceInformation ->
                        baseUpdatedDevice.copy(
                            bootId = latestPacket.bootId,
                            configId = latestPacket.configId,
                            searchPort = latestPacket.searchPort,
                            location = latestPacket.location,
                            secureLocation = latestPacket.secureLocation,
                        )
                    else -> baseUpdatedDevice.updateEmbeddedComponent(targetComponent)
                }
            }

        if (targetIndex != -1) {
            updatedList[targetIndex] = updatedDevice
        } else {
            updatedList.add(updatedDevice)
        }

        backingDeviceList.value = updatedList
    }

    /**
     * Handles the latest parsed instance of a [ByeByeMediaPacket] and removes the embedded
     * component or root device that said packet may target
     *
     * @param latestPacket Latest instance of a [ByeByeMediaPacket]
     * @return A modified version of [deviceList] with the target device/component removed
     */
    private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket) {
        val updatedList = backingDeviceList.value.toMutableList()
        val targetIndex = updatedList.indexOfFirst { it.uuid == latestPacket.usn.uuid }

        if (targetIndex == -1) {
            return
        }

        val targetComponent = latestPacket.usn
        val targetDevice = updatedList[targetIndex]

        logger?.logStateMessage(
            TAG,
            "Parsing BYEBYE packet targeting $targetComponent on device $targetDevice"
        )

        when (latestPacket.usn) {
            is RootDeviceInformation -> updatedList.remove(targetDevice)
            else -> {
                val updatedDevice = targetDevice.removeEmbeddedComponent(targetComponent)
                updatedList[targetIndex] = updatedDevice
            }
        }

        backingDeviceList.value = updatedList
    }

    /**
     * Iterates over the entire device list and filters out any stale devices. A stale device is
     * defined as one that has not seen a media packet in the last [AbridgedMediaDevice.cache]
     * seconds
     *
     * @return A device list with stale root devices removed
     */
    fun parseStaleDevices() {
        backingDeviceList.update { currentList ->
            currentList.filterNot {
                System.currentTimeMillis() - it.latestTimestamp > it.cache * 1000
            }
        }
    }

    private companion object {
        const val TAG = "LighthouseState"
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
        secureLocation = secureLocation,
    )
}
