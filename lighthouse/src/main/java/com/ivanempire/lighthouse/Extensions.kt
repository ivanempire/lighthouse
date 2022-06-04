package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaService
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceInformation
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.UniqueServiceName

/**
 * Handles the latest ALIVE or UPDATE media packet and replaces the relevant
 * [EmbeddedDeviceInformation] or [EmbeddedServiceInformation]. An ALIVE packet should be the first
 * one we receive for any embedded component. In the case of an UPDATE packet, the [latestBootId] is
 * the only thing to really change. However, component versions may be updated as well, and so we
 * also just replace the entire component.
 *
 * @param latestBootId The latest bootId integer to set - indicates if a component was rebooted
 * @param latestComponent The latest packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.updateEmbeddedComponent(
    latestBootId: Int,
    latestComponent: UniqueServiceName
) {
    if (latestComponent is EmbeddedDeviceInformation) {
        val targetComponent = this.deviceList.firstOrNull { it.deviceType == latestComponent.deviceType }
        this.deviceList.remove(targetComponent)

        this.deviceList.add(
            AdvertisedMediaDevice(
                deviceType = latestComponent.deviceType,
                bootId = latestBootId,
                deviceVersion = latestComponent.deviceVersion,
                domain = latestComponent.domain,
                latestTimestamp = System.currentTimeMillis()
            )
        )
    } else if (latestComponent is EmbeddedServiceInformation) {
        val targetComponent = this.serviceList.firstOrNull { it.serviceType == latestComponent.serviceType }
        this.serviceList.remove(targetComponent)

        this.serviceList.add(
            AdvertisedMediaService(
                serviceType = latestComponent.serviceType,
                bootId = latestBootId,
                serviceVersion = latestComponent.serviceVersion,
                domain = latestComponent.domain,
                latestTimestamp = System.currentTimeMillis()
            )
        )
    }
}

/**
 * Handles the latest BYEBYE media packet which either targets an [EmbeddedDeviceInformation] or
 * an [EmbeddedServiceInformation]. Removes said embedded information from the [AbridgedMediaDevice]
 *
 * @param latestComponent The latest packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.removeEmbeddedComponent(latestComponent: UniqueServiceName) {
    if (latestComponent is EmbeddedDeviceInformation) {
        this.deviceList.removeAll { it.deviceType == latestComponent.deviceType }
    } else if (latestComponent is EmbeddedServiceInformation) {
        this.serviceList.removeAll { it.serviceType == latestComponent.serviceType }
    }
}
