package com.example.kzneuro_wearos

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DataLayerListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        // Check if the message path matches our navigation command
        if (messageEvent.path == "/navigate_to_home") {
            // Create an Intent with a unique action
            val navigationIntent = Intent("com.example.kzneuro_wearos.NAVIGATE_TO_HOME")

            // Use a LocalBroadcastManager to send the intent only within your app
            LocalBroadcastManager.getInstance(this).sendBroadcast(navigationIntent)
        }
    }
}