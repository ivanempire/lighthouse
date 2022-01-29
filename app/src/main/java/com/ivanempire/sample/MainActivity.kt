package com.ivanempire.sample

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ivanempire.lighthouse.socket.MulticastSocketListener
import com.ivanempire.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.nio.charset.Charset

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var discoveryJob: Job? = null

    private val ioDispatcher = Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val multicastSocketListener = MulticastSocketListener(wifiManager)

        binding.startScan.setOnClickListener {
            Log.d("MainActivity", "#setOnClickListener")
            discoveryJob = lifecycleScope.launch {
                multicastSocketListener.listenForPackets()
                    .onCompletion { multicastSocketListener.releaseResources() }
                    .flowOn(ioDispatcher)
                    .onEach { latestPacket ->
                        val cleanedData = latestPacket.data.filter { it != 0.toByte() }.toByteArray()
                        Log.d("MainActivity", "Cleaned data:")
                        Log.d("MainActivity", String(cleanedData, Charset.defaultCharset()))
                    }.collect()
            }
        }

        binding.stopScan.setOnClickListener {
            discoveryJob?.cancel()
        }
    }
}