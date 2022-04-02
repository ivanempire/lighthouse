package com.ivanempire.lighthouse.models.packets

sealed class DeviceAttribute

object RootDeviceAttribute : DeviceAttribute()

data class EmbeddedDeviceAttribute(
    val domain: String? = null,
    val deviceType: String,
    val deviceVersion: String
) : DeviceAttribute()

data class EmbeddedServiceAttribute(
    val domain: String? = null,
    val serviceType: String,
    val serviceVersion: String
) : DeviceAttribute()
