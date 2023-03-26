package com.ivanempire.lighthouse.models.devices

import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.MediaHost
import java.net.URL
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * A media device description that is built from SSDP discovery information. This is
 * what Lighthouse will send to users after it has discovered devices
 *
 * @param uuid The unique identifier of this device
 * @param location The URL which can be called to get the complete XML description of this device
 * @param mediaDeviceServer The server information of the root device
 * @param serviceList The list of services that are present on the root and embedded devices
 * @param deviceList The list of devices (embedded AND root) present on this device
 */
data class AbridgedMediaDevice(
    val uuid: String,
    val host: MediaHost,
    val cache: Int,
    val bootId: Int?,
    val configId: Int?,
    val searchPort: Int?,
    val location: URL,
    val secureLocation: URL?,
    val mediaDeviceServer: MediaDeviceServer?,
    val serviceList: MutableList<EmbeddedService> = mutableListOf(),
    val deviceList: MutableList<EmbeddedDevice> = mutableListOf(),
    val latestTimestamp: Long,
    val extraHeaders: HashMap<String, String> = hashMapOf(),
)

/**
 * The media device, populated when an [AbridgedMediaDevice] calls the XML description
 * endpoint to get complete information about itself. Contains all of the information obtained
 * from the XML information in a parsed format.
 *
 * @param deviceType The device type
 * @param friendlyName The friendly name of the specific device
 * @param manufacturer The manufacturer of the specific device
 * @param manufacturerUrl The device's manufacturer's URL
 * @param modelDescription The model description of the specific device
 * @param modelName The model name of the specific device
 * @param modelNumber The model number of the specific device
 * @param modelUrl The model URL of the specific device
 * @param serialNumber The serial number of the specific device
 * @param udn The unique device name ==> UUID or string
 * @param upc Universal product code
 * @param serviceList The list of services supported by the root OR embedded device
 * @param deviceList The list of embedded devices found on this root device
 * @param presentationUrl The presentation URL of this root device
 */
@Serializable
@XmlSerialName("device", "urn:schemas-upnp-org:device-1-0", "")
data class DetailedMediaDevice(
    @XmlElement(true)
    val deviceType: String,
    @XmlElement(true)
    val friendlyName: String,
    @XmlElement(true)
    val manufacturer: String,
    @XmlElement(true)
    @XmlSerialName("manufacturerURL", "urn:schemas-upnp-org:device-1-0", "")
    val manufacturerUrl: String?,
    @XmlElement(true)
    val modelDescription: String?,
    @XmlElement(true)
    val modelName: String,
    @XmlElement(true)
    val modelNumber: String?,
    @XmlElement(true)
    @XmlSerialName("modelURL", "urn:schemas-upnp-org:device-1-0", "")
    val modelUrl: String?,
    @XmlElement(true)
    val serialNumber: String?,
    @XmlElement(true)
    @XmlSerialName("UDN", "urn:schemas-upnp-org:device-1-0", "")
    val udn: String,
    @XmlElement(true)
    @XmlSerialName("UPC", "urn:schemas-upnp-org:device-1-0", "")
    val upc: Int?,
    val serviceList: List<DetailedEmbeddedMediaService>?,
    val deviceList: List<DetailedMediaDevice>?,
    val presentationUrl: String?,
)
