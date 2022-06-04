package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaService
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceInformation
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.UniqueServiceName

internal fun AbridgedMediaDevice.createEmbeddedComponent() {
}

/**
 * Creates or updates items in the [AbridgedMediaDevice.deviceList] or [AbridgedMediaDevice.serviceList]
 * in the case of ALIVE and UPDATE packets
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
                domain = latestComponent.domain
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
                domain = latestComponent.domain
            )
        )
    }
}

/**
 * This is done
 */
internal fun AbridgedMediaDevice.removeEmbeddedComponent(latestComponent: UniqueServiceName) {
    if (latestComponent is EmbeddedDeviceInformation) {
        this.deviceList.removeAll { it.deviceType == latestComponent.deviceType }
    } else if (latestComponent is EmbeddedServiceInformation) {
        this.serviceList.removeAll { it.serviceType == latestComponent.serviceType }
    }
}
