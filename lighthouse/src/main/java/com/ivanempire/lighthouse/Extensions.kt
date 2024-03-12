package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.UniqueServiceName

/**
 * Handles the latest [UniqueServiceName] instance from an ALIVE packet to update an embedded
 * component - be that a device or a service. We replace the entire component because versions may
 * change in the case of an UPDATE packet.
 *
 * @param latestComponent The latest ALIVE or BYEBYE packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.updateEmbeddedComponent(
    latestComponent: UniqueServiceName
): AbridgedMediaDevice {
    return when (latestComponent) {
        is EmbeddedDevice -> {
            val updatedDeviceList =
                this.deviceList.filterNot { it.deviceType == latestComponent.deviceType } +
                    latestComponent
            this.copy(deviceList = updatedDeviceList)
        }
        is EmbeddedService -> {
            val updatedServiceList =
                this.serviceList.filterNot { it.serviceType == latestComponent.serviceType } +
                    latestComponent
            this.copy(serviceList = updatedServiceList)
        }
        else -> this
    }
}

/**
 * Handles the latest [UniqueServiceName] instance from a BYEBYE packet to remove an embedded
 * component - be that a device or a service - from the root device
 *
 * @param latestComponent The latest BYEBYE packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.removeEmbeddedComponent(
    latestComponent: UniqueServiceName
): AbridgedMediaDevice {
    return when (latestComponent) {
        is EmbeddedDevice ->
            this.copy(
                deviceList = deviceList.filterNot { it.deviceType == latestComponent.deviceType },
            )
        is EmbeddedService ->
            this.copy(
                serviceList =
                    serviceList.filterNot { it.serviceType == latestComponent.serviceType },
            )
        else -> this
    }
}

/**
 * Returns and removes an entry from the receiving [HashMap]. This is used to remove all standard
 * fields from an SSDP packet, so that the only ones left at the end of parsing would be the extra
 * headers specified by the manufacturer.
 *
 * @param targetKey The key of the target value to look up, remove, and return from the [HashMap]
 * @return The lookup value from [targetKey], null otherwise
 */
internal fun HashMap<String, String>.getAndRemove(targetKey: String): String? {
    val targetValue = this[targetKey]
    this.remove(targetKey)
    return targetValue
}
