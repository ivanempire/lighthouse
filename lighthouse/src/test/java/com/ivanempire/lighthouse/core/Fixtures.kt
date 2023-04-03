package com.ivanempire.lighthouse.core

import java.net.DatagramPacket

object Fixtures {
    val ALIVE_PACKET_1800 = DatagramPacket(
        "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=1800\r\nLocation: http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
        386
    )
    val ALIVE_PACKET_3600 = DatagramPacket(
        "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=3600\r\nLocation: http://192.168.1.191:8091/97dd174d-75b0-41b4-b678-687f286b28f2.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:97dd174d-75b0-41b4-b678-687f286b28f2::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
        386
    )
    val ALIVE_PACKET_3601 = DatagramPacket(
        "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=3601\r\nLocation: http://192.168.1.192:8091/d7e635e0-104a-4745-95aa-d4afb459062d.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:d7e635e0-104a-4745-95aa-d4afb459062d::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
        386
    )
}
