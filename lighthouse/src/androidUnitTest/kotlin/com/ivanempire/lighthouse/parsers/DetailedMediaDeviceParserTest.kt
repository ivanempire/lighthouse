package com.ivanempire.lighthouse.parsers

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [DetailedMediaDeviceParser] */
class DetailedMediaDeviceParserTest {
    @Test
    fun `given device with services correctly parses`() {
        val xmlInput = """
            <?xml version="1.0"?>
            <root xmlns="urn:schemas-upnp-org:device-1-0">
                <specVersion>
                    <major>1</major>
                    <minor>0</minor></specVersion>
                <device>
                    <deviceType>urn:schemas-upnp-org:device:InternetGatewayDevice:1</deviceType>
                    <friendlyName>Archer AX50</friendlyName>
                    <manufacturer>TP-Link</manufacturer>
                    <manufacturerURL>http://www.tp-link.com/</manufacturerURL>
                    <modelDescription>Archer AX50</modelDescription>
                    <modelName>Archer AX50</modelName>
                    <modelNumber>1.0</modelNumber>
                    <modelURL>http://www.tp-link.com/</modelURL>
                    <serialNumber>00000000</serialNumber>
                    <UDN>uuid:759c0438-56dc-459a-8e75-8d34b9a5077f</UDN>
                    <serviceList>
                        <service>
                            <serviceType>urn:schemas-upnp-org:service:Layer3Forwarding:1</serviceType>
                            <serviceId>urn:upnp-org:serviceId:Layer3Forwarding1</serviceId>
                            <controlURL>/ctl/L3F</controlURL><eventSubURL>/evt/L3F</eventSubURL>
                            <SCPDURL>/L3F.xml</SCPDURL>
                        </service>
                    </serviceList>
                    <deviceList><device><deviceType>urn:schemas-upnp-org:device:WANDevice:1</deviceType><friendlyName>WANDevice</friendlyName><manufacturer>MiniUPnP</manufacturer><manufacturerURL>http://miniupnp.free.fr/</manufacturerURL><modelDescription>WAN Device</modelDescription><modelName>WAN Device</modelName><modelNumber>20210723</modelNumber><modelURL>http://miniupnp.free.fr/</modelURL><serialNumber>00000000</serialNumber><UDN>uuid:e4c797e0-82ce-4f84-b56d-085d9d77c8cd</UDN><UPC>000000000000</UPC><serviceList><service><serviceType>urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1</serviceType><serviceId>urn:upnp-org:serviceId:WANCommonIFC1</serviceId><controlURL>/ctl/CmnIfCfg</controlURL><eventSubURL>/evt/CmnIfCfg</eventSubURL><SCPDURL>/WANCfg.xml</SCPDURL></service></serviceList><deviceList><device><deviceType>urn:schemas-upnp-org:device:WANConnectionDevice:1</deviceType><friendlyName>WANConnectionDevice</friendlyName><manufacturer>MiniUPnP</manufacturer><manufacturerURL>http://miniupnp.free.fr/</manufacturerURL><modelDescription>MiniUPnP daemon</modelDescription><modelName>MiniUPnPd</modelName><modelNumber>20210723</modelNumber><modelURL>http://miniupnp.free.fr/</modelURL><serialNumber>00000000</serialNumber><UDN>uuid:e4c797e0-82ce-4f84-b56d-085d9d77c8cd</UDN><UPC>000000000000</UPC><serviceList><service><serviceType>urn:schemas-upnp-org:service:WANIPConnection:1</serviceType><serviceId>urn:upnp-org:serviceId:WANIPConn1</serviceId><controlURL>/ctl/IPConn</controlURL><eventSubURL>/evt/IPConn</eventSubURL><SCPDURL>/WANIPCn.xml</SCPDURL></service></serviceList></device></deviceList></device></deviceList>
                    <presentationURL>http://192.168.0.1/</presentationURL>
                </device>
            </root>
            """
            .trimIndent()
        val device = DetailedMediaDeviceParser.parse(xmlInput)
        assertEquals("Archer AX50", device.friendlyName)
        assertEquals("uuid:759c0438-56dc-459a-8e75-8d34b9a5077f", device.udn)
    }

    @Test
    fun `nvidia shield description parses`() {
        val xmlInput = """
            <root xmlns="urn:schemas-upnp-org:device-1-0">
            <specVersion>
            <major>1</major>
            <minor>0</minor>
            </specVersion>
            <URLBase>http://192.168.0.211:8008</URLBase>
            <device>
            <deviceType>urn:dial-multiscreen-org:device:dial:1</deviceType>
            <friendlyName>SHIELD</friendlyName>
            <manufacturer>NVIDIA</manufacturer>
            <modelName>SHIELD Android TV</modelName>
            <UDN>uuid:8544cd4e-7256-45ac-b825-eafb91f62d29</UDN>
            <iconList>
            <icon>
            <mimetype>image/png</mimetype>
            <width>98</width>
            <height>55</height>
            <depth>32</depth>
            <url>/setup/icon.png</url>
            </icon>
            </iconList>
            <serviceList>
            <service>
            <serviceType>urn:dial-multiscreen-org:service:dial:1</serviceType>
            <serviceId>urn:dial-multiscreen-org:serviceId:dial</serviceId>
            <controlURL>/ssdp/notfound</controlURL>
            <eventSubURL>/ssdp/notfound</eventSubURL>
            <SCPDURL>/ssdp/notfound</SCPDURL>
            </service>
            </serviceList>
            </device>
            </root>
        """.trimIndent()
        val device = DetailedMediaDeviceParser.parse(xmlInput)
        assertEquals("SHIELD", device.friendlyName)
        assertEquals("uuid:8544cd4e-7256-45ac-b825-eafb91f62d29", device.udn)
    }
}