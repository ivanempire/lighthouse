package com.ivanempire.lighthouse.models.devices

import com.ivanempire.lighthouse.models.packets.MediaHost
import java.net.URL
import java.util.UUID

/**
 * Base type for a media device that will be built by this SDK from discovery information - over
 * the multicast socket, or XML
 */
abstract class MediaDevice

/**
 * A specific version of a [MediaDevice] that is built from SSDP discovery information. This is
 * what the consumers of the SDK receive after discovery has found results on the network
 * @param uuid The unique identifier of this device
 * @param location The URL which can be called to get the complete XML description of this device
 * @param server The server information of the root device
 * @param serviceList The list of services that are present on the root and embedded devices
 * @param deviceList The list of devices (embedded AND root) present on this device
 */
data class AbridgedMediaDevice(
    val uuid: UUID,
    val host: MediaHost,
    val cache: Int,
    val bootId: Int?,
    val configId: Int?,
    val searchPort: Int?,
    val location: URL?,
    val secureLocation: URL?,
    val server: MediaDeviceServer?,
    val serviceList: MutableList<AdvertisedMediaService> = mutableListOf(),
    val deviceList: MutableList<AdvertisedMediaDevice> = mutableListOf()
) : MediaDevice() {
    // TODO: Implement XML call
    val description: RootMediaDevice?
        get() {
            return null
        }
}

/**
 * A specific version of a [MediaDevice] that is built from SSDP discovery information. Used inside
 * the [AbridgedMediaDevice] to present consumers with base information about a device, without
 * querying the XML description endpoint
 * @param deviceType The device type that is obtained from the USN or NT packet fields
 * @param deviceVersion The device version
 * @param domain The device domain
 */
data class AdvertisedMediaDevice(
    val deviceType: String,
    val deviceVersion: String,
    val domain: String? = null
) : MediaDevice() {

    override fun equals(other: Any?) = (other is AdvertisedMediaDevice) &&
        deviceType == other.deviceType && deviceVersion == other.deviceVersion

    override fun hashCode(): Int {
        var result = deviceType.hashCode()
        result = 31 * result + deviceVersion.hashCode()
        return result
    }
}

/**
 * A more refined, but not concrete, version of a [MediaDevice]. This class represents common
 * properties across the root and embedded media devices, whose fields are populated when the XML
 * description endpoint is queried
 * @param deviceType The device type
 * @param friendlyName The friendly name of the specific device
 * @param manufacturer The manufacturer of the specific device
 * @param manufacturerURL The device's manufacturer's URL
 * @param modelDescription The model description of the specific device
 * @param modelName The model name of the specific device
 * @param modelNumber The model number of the specific device
 * @param modelUrl The model URL of the specific device
 * @param serialNumber The serial number of the specific device
 * @param udn TODO: Look up what this stands for
 * @param serviceList The list of services supported by the root OR embedded device
 */
open class DetailedMediaDevice(
    open val deviceType: String,
    open val friendlyName: String,
    open val manufacturer: String,
    open val manufacturerURL: URL,
    open val modelDescription: String,
    open val modelName: String,
    open val modelNumber: String,
    open val modelUrl: URL,
    open val serialNumber: String,
    open val udn: UUID,
    open val serviceList: List<DetailedMediaService>?
) : MediaDevice()

/**
 * The root media device, populated when an [AbridgedMediaDevice] calls the XML description
 * endpoint to get complete information about itself. Contains all of the information obtained
 * from the XML information in a parsed format. All fields are identical to ones described in the
 * [DetailedMediaService], except for two.
 * @param deviceList The list of embedded devices found on this root device
 * @param presentationUrl The presentation URL of this root device
 */
data class RootMediaDevice(
    override val deviceType: String,
    override val friendlyName: String,
    override val manufacturer: String,
    override val manufacturerURL: URL,
    override val modelDescription: String,
    override val modelName: String,
    override val modelNumber: String,
    override val modelUrl: URL,
    override val serialNumber: String,
    override val udn: UUID,
    override val serviceList: List<DetailedMediaService>?,
    val deviceList: List<EmbeddedMediaDevice>?,
    val presentationUrl: URL?
) : DetailedMediaDevice(
    deviceType, friendlyName, manufacturer, manufacturerURL, modelDescription, modelName, modelNumber, modelUrl, serialNumber, udn, serviceList
)

/**
 * The embedded media device, populated when an [AbridgedMediaDevice] calls the XML description
 * endpoint to get complete information about itself. Contains all of the information obtained
 * from the XML information in a parsed format. All fields are identical to ones described in the
 * [DetailedMediaService], except for one.
 * @param upc TODO: Look up what this stands for
 */
data class EmbeddedMediaDevice(
    override val deviceType: String,
    override val friendlyName: String,
    override val manufacturer: String,
    override val manufacturerURL: URL,
    override val modelDescription: String,
    override val modelName: String,
    override val modelNumber: String,
    override val modelUrl: URL,
    override val serialNumber: String,
    override val udn: UUID,
    val upc: Int?,
    override val serviceList: List<DetailedMediaService>?
) : DetailedMediaDevice(
    deviceType, friendlyName, manufacturer, manufacturerURL, modelDescription, modelName, modelNumber, modelUrl, serialNumber, udn, serviceList
)
