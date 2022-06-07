package com.ivanempire.lighthouse

import android.content.Context
import android.net.wifi.WifiManager
import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.core.RealDiscoveryManager
import com.ivanempire.lighthouse.core.RealLighthouseClient
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.socket.RealSocketListener
import kotlinx.coroutines.flow.Flow

interface LighthouseClient {

    class Builder(context: Context) {

        private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        private val socketListener = RealSocketListener(wifiManager)

        private val discoveryManager = RealDiscoveryManager(
            LighthouseState(),
            socketListener
        )

        fun build(): LighthouseClient {
            return RealLighthouseClient(discoveryManager)
        }
    }

    fun discoverDevices(): Flow<List<AbridgedMediaDevice>>
}
