package com.ivanempire.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ivanempire.lighthouse.LighthouseClient
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myClient = LighthouseClient.Builder(this).build()

        val discoveryJob = lifecycleScope.launch {
            delay(5000)
            myClient.discoverDevices().collect {
                Log.d("MAIN-ACTIVITY", "Emitted device list: $it")
            }
        }

        lifecycleScope.launch {
            delay(35000)
            Log.d("MAIN-ACTIVITY", "Stopping discovery job")
            discoveryJob.cancelAndJoin()
        }
    }
}