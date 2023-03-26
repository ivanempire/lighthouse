package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice
import com.ivanempire.lighthouse.models.devices.RootContainer
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML

object DetailedMediaDeviceParser {
    @OptIn(ExperimentalXmlUtilApi::class)
    private val xml = XML {
        xmlDeclMode = XmlDeclMode.Minimal
        unknownChildHandler = UnknownChildHandler { _, _, _, _, _ -> emptyList() }
    }

    private val serializer = serializer<RootContainer>()

    fun parse(input: String): DetailedMediaDevice {
        return xml.decodeFromString(serializer, input).device
    }
}