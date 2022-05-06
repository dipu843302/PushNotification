package com.example.pushnotification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {



    var TAG = "dipu"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        val messageData = intent.getParcelableExtra<MessageData>("message")

        if (messageData != null) {
            tvTitle.text = messageData.title
            tvBody.text = messageData.body
            Glide.with(imageView).load(messageData.image).into(imageView)
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("com.example.pushnotification_FCM-MESSAGE"))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null && intent.action == "com.example.pushnotification_FCM-MESSAGE") {

                val messageData = intent.getParcelableExtra<MessageData>("message")

                if (messageData != null) {
                    tvTitle.text = messageData.title
                    tvBody.text = messageData.body
                    Glide.with(imageView).load(messageData.image).into(imageView)
                }


            }
        }
    }
}






//FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//    if (!task.isSuccessful) {
//        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//        return@OnCompleteListener
//    }
//
//    //      Get new FCM registration token
//    val token = task.result
//    textView.setText(token)
//
//    Log.d(TAG, token.toString())
//    Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
//})

