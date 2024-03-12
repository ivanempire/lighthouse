package com.ivanempire.lighthouse.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivanempire.lighthouse.LighthouseClient

class MainActivityViewModelFactory(private val lighthouseClient: LighthouseClient) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
    ): T {
        return MainActivityViewModel(
            lighthouseClient,
        )
            as T
    }
}
