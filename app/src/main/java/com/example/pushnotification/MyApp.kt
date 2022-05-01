package com.example.pushnotification

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MyApp :Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("TAG", "onCreate: subscribeToTopic", )
                }else{
                    Log.i("TAG", "onCreate: subscribeToTopic failed", )
                }
            }
    }
}