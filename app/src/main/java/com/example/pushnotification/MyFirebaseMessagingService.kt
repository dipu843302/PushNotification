package com.example.pushnotification

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelID = "notification_channel"
const val channelName = "com.example.pushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val arrayList = ArrayList<MessageData>()
    var value = 0

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

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("message", messageData)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.d("show", "${messageData.body} ${messageData.title}")

        val newMessageNotification1 = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(messageData.title)
            .setContentText(messageData.body)
            .setGroup(channelName)
            .build()

        val newMessageNotification2 = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(messageData.title)
            .setContentText(messageData.body)
            .setGroup(channelName)
            .build()

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(channelName)
                .setContentText("Two new messages")
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .addLine(messageData.title).addLine(messageData.body)
                        .setBigContentTitle("Tittle")
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
      //  notificationManager.notify(0, builder.build())
        NotificationManagerCompat.from(this).apply {
            notify(1, newMessageNotification1)
            notify(2, newMessageNotification2)
            notify(0, builder.build())
        }
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