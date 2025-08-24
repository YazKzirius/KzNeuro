package com.example.kzneuro_wearos

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class WatchAppState {
    object Idle : WatchAppState()
    object Requesting : WatchAppState()
    object WatchFound : WatchAppState()
    object WatchNotFound : WatchAppState()
}

class WatchViewModel(application: Application) : AndroidViewModel(application) {

    private val capabilityClient = Wearable.getCapabilityClient(application)
    private val messageClient = Wearable.getMessageClient(application)

    private val _watchAppState = MutableStateFlow<WatchAppState>(WatchAppState.Idle)
    val watchAppState: StateFlow<WatchAppState> = _watchAppState

    fun navigateToHomeOnWatch() {
        viewModelScope.launch {
            _watchAppState.value = WatchAppState.Requesting
            try {
                // Find a connected node that has our watch app capability
                val capabilityInfo = capabilityClient
                    .getCapability("wearos", CapabilityClient.FILTER_REACHABLE)
                    .await()

                val watchNode = capabilityInfo.nodes.firstOrNull()

                if (watchNode != null) {
                    // Send the message to the found watch
                    messageClient.sendMessage(watchNode.id, "/navigate_to_home", byteArrayOf())
                        .await()
                    _watchAppState.value = WatchAppState.WatchFound
                    Log.d("WatchViewModel", "Navigate message sent to ${watchNode.displayName}")
                } else {
                    _watchAppState.value = WatchAppState.WatchNotFound
                    Log.d("WatchViewModel", "No capable watch node found")
                }
            } catch (e: Exception) {
                Log.e("WatchViewModel", "Failed to send navigation message", e)
                _watchAppState.value = WatchAppState.WatchNotFound
            }
        }
    }
}