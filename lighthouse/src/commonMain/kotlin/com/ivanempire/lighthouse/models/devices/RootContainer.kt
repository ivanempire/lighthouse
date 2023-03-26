package com.ivanempire.lighthouse.models.devices

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("root", "urn:schemas-upnp-org:device-1-0", "")
data class RootContainer(
    @XmlElement(true)
    val specVersion: SpecVersion,
    @XmlElement(true)
    val device: DetailedMediaDevice
)

@Serializable
@XmlSerialName("specVersion", "urn:schemas-upnp-org:device-1-0", "")
data class SpecVersion(
    @XmlElement(true)
    val major: Int,
    @XmlElement(true)
    val minor: Int,
)