package com.ivanempire.lighthouse.models.devices

import kotlinx.serialization.Serializable

/**
 * Represents a service that a media device supports
 *
 * @param serviceType The service type that is obtained from SSDP packets or from the XML endpoint
 * @param bootId
 */
interface MediaService {
    val serviceType: String
    val bootId: Int
}

/**
 * A specific version of a [MediaService], populated exclusively from the XML description endpoint
 *
 * @param serviceType The service type that is obtained from the XML field
 * @param serviceId The service identifier
 * @param descriptionUrl The partial endpoint to call for the service description
 * @param controlUrl The partial endpoint to call for the service control
 * @param eventUrl The partial endpoint to call for service event subscriptions
 */
@Serializable
data class DetailedEmbeddedMediaService(
    override val serviceType: String,
    override val bootId: Int,
    val serviceId: String,
    val descriptionUrl: String,
    val controlUrl: String,
    val eventUrl: String,
) : MediaService
