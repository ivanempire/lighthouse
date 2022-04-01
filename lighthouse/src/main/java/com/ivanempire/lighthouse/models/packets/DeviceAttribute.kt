package com.ivanempire.lighthouse.models.packets

import java.util.UUID

sealed class DeviceAttribute

object RootDeviceAttribute : DeviceAttribute()

data class UuidDeviceAttribute(
    val uuid: UUID
) : DeviceAttribute()

data class EmbeddedDeviceAttribute(
    val domain: String? = null,
    val deviceType: String,
    val deviceVersion: String
) : DeviceAttribute()

data class ServiceAttribute(
    val domain: String? = null,
    val serviceType: String,
    val serviceVersion: String
) : DeviceAttribute()
