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

// Sealed class to represent the UI state
sealed class PhoneAppState {
    object Idle : PhoneAppState()
    object Requesting : PhoneAppState()
    object PhoneFound : PhoneAppState()
    object PhoneNotFound : PhoneAppState()
}

class OpenAppViewModel(application: Application) : AndroidViewModel(application) {

    private val capabilityClient: CapabilityClient = Wearable.getCapabilityClient(application)
    private val messageClient = Wearable.getMessageClient(application)

    private val _phoneAppState = MutableStateFlow<PhoneAppState>(PhoneAppState.Idle)
    val phoneAppState: StateFlow<PhoneAppState> = _phoneAppState

    fun openAppOnPhone() {
        viewModelScope.launch {
            _phoneAppState.value = PhoneAppState.Requesting
            try {
                val capabilityInfo = capabilityClient
                    .getCapability("mobile", CapabilityClient.FILTER_REACHABLE)
                    .await()
                val phoneNode = capabilityInfo.nodes.firstOrNull()

                if (phoneNode != null) {
                    messageClient.sendMessage(phoneNode.id, "/open_mobile_app", byteArrayOf())
                        .await()
                    _phoneAppState.value = PhoneAppState.PhoneFound
                    Log.d("OpenAppViewModel", "Open app message sent to ${phoneNode.displayName}")
                } else {
                    _phoneAppState.value = PhoneAppState.PhoneNotFound
                    Log.d("OpenAppViewModel", "No capable phone node found")
                }
            } catch (e: Exception) {
                Log.e("OpenAppViewModel", "Failed to send message", e)
                _phoneAppState.value = PhoneAppState.PhoneNotFound
            }
        }
    }
}