package com.example.pushnotification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


const val channelID = "notification_channel"
const val channelName = "com.example.pushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val arrayList = ArrayList<MessageData>()
    var value = 0

    var messageList = ArrayList<MessageData>()

    var hashMap = HashMap<Int, ArrayList<MessageData>>()


    val shrd = getSharedPreferences("Message", MODE_PRIVATE)
    val editor: SharedPreferences.Editor = shrd.edit()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var messageData = MessageData(
            message.data["title"]!!,
            message.data["body"]!!,
            message.data["image"] ?: "",
            message.data["id"]!!.toInt()
        )

        if (isAppOnForeground()) {
            val intent = Intent("com.example.pushnotification_FCM-MESSAGE")

            intent.putExtra("message", messageData)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        } else {

            showNotification(messageData)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(messageData: MessageData) {

//        if (hashMap.containsKey(messageData.id)) {
//
//            messageList.add(messageData.body)
//            hashMap.put(messageData.id,messageList)
//
//        } else {
//
//            messageList.add(messageData.body)
//            hashMap.put(messageData.id,messageList)
//        }

        messageList.add(messageData)
        hashMap.put(messageData.id,messageList)

         editor.putString("notificationKey",ObjectMapper().writeValueAsString(hashMap))
         editor.apply()

        var hasmapString=shrd.getString("notificationKey","")
     //   var notificationHasmap:HashMap<Int, ArrayList<MessageData>>=ObjectMapper().readValue(hasmapString, object : TypeReference<HashMap<Int, ArrayList<MessageData>>>() {})

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("message", messageData)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.d("show", "${messageData.body} ${messageData.title}")

        arrayList.add(messageData)

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(messageData.id.toString())
                .setContentText("${arrayList.size} new messages")
                .setStyle(
                    NotificationCompat.InboxStyle()

                        .addLine(messageData.body)

                        .setBigContentTitle(messageData.title)
                        .setSummaryText("You have " + arrayList.size + " Notifications.")
                )
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(messageData.id, builder.build())

//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notificationManager.activeNotifications.forEach { sbNotification ->
//                if (sbNotification.id == 0) {
//                    Log.d("dipu", sbNotification.notification.extras.getString("android.text")!!)
//
//                }
//            }
//        }

    }

    /*Check if the application is in foreground or not*/
    private fun isAppOnForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState
            .isAtLeast(Lifecycle.State.STARTED)
    }

}


// image url
// https://freeiconshop.com/wp-content/uploads/edd/notification-flat.png