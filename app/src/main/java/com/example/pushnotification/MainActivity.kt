package com.example.pushnotification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var TAG = "dipu"
    var showMessage = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras

        val messageList = extras?.getParcelableArrayList<MessageData>("message")
        if (messageList != null) {
            messageList.forEach {

                showMessage+=(it.body + "\n")

                tvTitle.text = it.title
                Glide.with(imageView).load(it.image).into(imageView)
            }
            tvBody.text = showMessage
        } else {
          //  Toast.makeText(this, "Message not found", Toast.LENGTH_SHORT).show()

        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("com.example.pushnotification_FCM-MESSAGE"))
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            //      Get new FCM registration token
            val token = task.result
            //  textView.setText(token)

            Log.d(TAG, token.toString())
           // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
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

