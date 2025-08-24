package com.example.kzneuro_wearos

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService


class DataLayerListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Check if the message path matches the command sent from the watch
        if (messageEvent.path == "/start-sign-in") {
            // Create an Intent to launch your mobile app's MainActivity
            val startAppIntent = Intent(this, MainActivity::class.java).apply {
                // This flag is required to start an Activity from a background service
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // Add an "action" extra to tell MainActivity why it's being launched
                putExtra("ACTION", "SIGN_IN")
            }
            startActivity(startAppIntent)
        }
    }
}