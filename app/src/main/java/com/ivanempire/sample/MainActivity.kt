package com.ivanempire.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ivanempire.lighthouse.LighthouseClient
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myClient = LighthouseClient.Builder(this).build()

        lifecycleScope.launch {
            myClient.discoverDevices().collect {
                Log.d("MAIN-ACTIVITY", "Emitted device list: $it")
            }
        }
    }
}