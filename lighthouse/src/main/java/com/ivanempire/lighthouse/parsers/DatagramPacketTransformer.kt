package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.DatagramPacket
import java.nio.charset.Charset

/**
 * Parses each instance of a [DatagramPacket] from the socket and transforms that into a [HashMap]
 * representing each SSDP packet's header-value pairs. If the incoming [StartLine] is invalid, or
 * the [HeaderKeys.LOCATION] is null, then this datagram is not processed further
 */
class DatagramPacketTransformer {

    companion object {
        operator fun invoke(datagramPacket: DatagramPacket): HashMap<String, String>? {
            val cleanedDatagram = datagramPacket.cleanPacket()
            val packetFields = cleanedDatagram.split(KEY_VALUE_PAIR_DELIMITER)

            if (StartLine.getByRawValue(packetFields[0]) == null) {
                return null
            }

            // Start building the packet headers, starting with item 1 since item 0 is the StartLine
            val packetHeaders = hashMapOf<String, String>()
            packetFields.subList(1, packetFields.size).forEach {
                val splitField = it.split(HEADER_FIELD_DELIMITER, ignoreCase = false, limit = 2)
                packetHeaders[splitField[0].trim().uppercase()] = splitField[1].trim()
            }

            if (packetHeaders[HeaderKeys.LOCATION] == null) {
                return null
            }

            return packetHeaders
        }

        private const val KEY_VALUE_PAIR_DELIMITER = "\r\n"
        private const val HEADER_FIELD_DELIMITER = ":"
    }
}

/**
 * Transforms a [DatagramPacket] into a string for further processing - removes all null bytes from
 * the data array as well
 *
 * @return String representation of the given [DatagramPacket]
 */
internal fun DatagramPacket.cleanPacket(): String {
    val cleanedData = this.data.filter { it != 0.toByte() }.toByteArray()
    return String(cleanedData, Charset.defaultCharset()).trim()
}
