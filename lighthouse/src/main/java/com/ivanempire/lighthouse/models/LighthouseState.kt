package com.ivanempire.lighthouse.models

import java.lang.IllegalStateException

// BOOTID.UPNP.ORG     ==> changes, means device will reboot
// CONFIGID.UPNP.ORG   ==> changes, pull new XML description
// NEXTBOOTID.UPNP.ORG ==> next bootId to use
object LighthouseState {

  private val deviceList = mutableListOf<AbridgedMediaDevice>()

  fun parseMediaPacket(latestPacket: MediaPacket): List<MediaDevice> {
    return when (latestPacket) {
      is AliveMediaPacket -> parseAliveMediaPacket(latestPacket)
      is UpdateMediaPacket -> parseUpdateMediaPacket(latestPacket)
      is ByeByeMediaPacket -> parseByeByeMediaPacket(latestPacket)
      else -> throw IllegalStateException("")
    }
  }

  private fun parseByeByeMediaPacket(latestPacket: ByeByeMediaPacket): List<MediaDevice> {
    val targetDevice = deviceList.firstOrNull { it.uuid == latestPacket.uuid } ?: return deviceList


  }


}
