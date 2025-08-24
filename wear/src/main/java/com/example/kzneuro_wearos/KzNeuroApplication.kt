package com.example.kzneuro_wearos

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Custom Application class to initialize Firebase.
 * This ensures Firebase is ready before any Activity is created.
 */
class KzNeuroApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase for the entire application
        Log.d("KzNeuroApp", "!!!!!!!!! KzNeuroApplication onCreate IS RUNNING !!!!!!!!!")
        FirebaseApp.initializeApp(this)
    }
}