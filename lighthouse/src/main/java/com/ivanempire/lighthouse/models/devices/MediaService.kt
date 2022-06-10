package com.ivanempire.lighthouse.models.devices

/**
 * Base class representing a service that a media device supports
 * @param serviceType The service type that is obtained from SSDP packets or from the XML endpoint
 */
open class MediaService(
    open val serviceType: String,
    open val bootId: Int,
)

/**
 * A specific version of a [MediaService], populated exclusively from the XML description endpoint
 * @param serviceType The service type that is obtained from the XML field
 * @param serviceId The service identifier
 * @param descriptionUrl The partial endpoint to call for the service description
 * @param controlUrl The partial endpoint to call for the service control
 * @param eventUrl The partial endpoint to call for service event subscriptions
 */
data class DetailedEmbeddedMediaService(
    override val serviceType: String,
    override val bootId: Int,
    val serviceId: String,
    val descriptionUrl: String,
    val controlUrl: String,
    val eventUrl: String
) : MediaService(serviceType, bootId)
