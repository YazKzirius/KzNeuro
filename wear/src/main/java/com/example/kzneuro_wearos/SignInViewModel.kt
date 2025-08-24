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

// The UI states remain the same
sealed class PhoneAppState {
    object Idle : PhoneAppState()
    object Requesting : PhoneAppState()
    object PhoneFound : PhoneAppState() // We won't use this state directly anymore
    object PhoneNotFound : PhoneAppState()
}

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val capabilityClient: CapabilityClient = Wearable.getCapabilityClient(application)
    private val messageClient = Wearable.getMessageClient(application)

    private val _phoneAppState = MutableStateFlow<PhoneAppState>(PhoneAppState.Idle)
    val phoneAppState: StateFlow<PhoneAppState> = _phoneAppState

    /**
     * Sends a message to the phone asking it to start the Google Sign-In flow.
     */
    fun startSignInOnPhone() {
        viewModelScope.launch {
            _phoneAppState.value = PhoneAppState.Requesting
            try {
                val capabilityInfo = capabilityClient
                    .getCapability("mobile", CapabilityClient.FILTER_REACHABLE)
                    .await()
                val phoneNode = capabilityInfo.nodes.firstOrNull()

                if (phoneNode != null) {
                    // Send the specific "start sign-in" message
                    messageClient.sendMessage(phoneNode.id, "/start-sign-in", byteArrayOf())
                        .await()
                    Log.d("SignInViewModel", "Sign-in request sent to ${phoneNode.displayName}")
                    // The UI will now wait for the broadcast from the DataLayerListenerService
                } else {
                    _phoneAppState.value = PhoneAppState.PhoneNotFound
                    Log.d("SignInViewModel", "No capable phone node found")
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Failed to send message", e)
                _phoneAppState.value = PhoneAppState.PhoneNotFound
            }
        }
    }
}