package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.UniqueServiceName

/**
 * Handles the latest ALIVE or UPDATE media packet and replaces the relevant
 * [EmbeddedDevice] or [EmbeddedService]. An ALIVE packet should be the first
 * one we receive for any embedded component. In the case of an UPDATE packet, the [latestBootId] is
 * the only thing to really change. However, component versions may be updated as well, and so we
 * also just replace the entire component.
 *
 * @param latestComponent The latest packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.updateEmbeddedComponent(latestComponent: UniqueServiceName) {
    if (latestComponent is EmbeddedDevice) {
        val targetComponent = this.deviceList.firstOrNull { it.deviceType == latestComponent.deviceType }
        this.deviceList.remove(targetComponent)
        this.deviceList.add(latestComponent)
    } else if (latestComponent is EmbeddedService) {
        val targetComponent = this.serviceList.firstOrNull { it.serviceType == latestComponent.serviceType }
        this.serviceList.remove(targetComponent)
        this.serviceList.add(latestComponent)
    }
}

/**
 * Handles the latest BYEBYE media packet which either targets an [EmbeddedDevice] or
 * an [EmbeddedService]. Removes said embedded information from the [AbridgedMediaDevice]
 *
 * @param latestComponent The latest packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.removeEmbeddedComponent(latestComponent: UniqueServiceName) {
    if (latestComponent is EmbeddedDevice) {
        this.deviceList.removeAll { it.deviceType == latestComponent.deviceType }
    } else if (latestComponent is EmbeddedService) {
        this.serviceList.removeAll { it.serviceType == latestComponent.serviceType }
    }
}
