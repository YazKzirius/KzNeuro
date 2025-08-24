package com.example.kzneuro_wearos


import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.launch

class DataLayerListenerService : WearableListenerService() {

    private val authenticator = FirebaseAuthenticator()

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        dataEvents.forEach { event ->
            if (event.dataItem.uri.path == "/sign-in-success") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val idToken = dataMap.getString("id_token")
                if (idToken != null) {
                    signInOnWatch(idToken)
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        if (messageEvent.path == "/sign-in-failure") {
            broadcastAuthResult(false)
        }
    }

    private fun signInOnWatch(idToken: String) {
        // THIS IS THE KEY CHANGE: Use the global ApplicationScope
        ApplicationScope.scope.launch {
            val success = authenticator.signInWithIdToken(idToken)
            broadcastAuthResult(success)
        }
    }

    private fun broadcastAuthResult(success: Boolean) {
        val intent = Intent("com.example.kzneuro_wearos.AUTH_RESULT").apply {
            putExtra("AUTH_SUCCESS", success)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // No need for onDestroy() anymore, as we are not managing a local scope.
}