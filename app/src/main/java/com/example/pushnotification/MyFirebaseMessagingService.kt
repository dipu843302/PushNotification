package com.example.pushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelID = "notification_channel"
const val channelName = "com.example.pushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    //generate the notification
    // attach the notification created with custom layout
    // show the notification

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
       if (remoteMessage.notification!=null){
           generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
       }
    }

    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews("com.example.pushnotification", R.layout.notification)
        remoteViews.setTextViewText(R.id.tittle, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.app_logo, R.drawable.ic_baseline_message_24)

        return remoteViews
    }

    fun generateNotification(tittle: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // channel id, channel name
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(tittle, message))
       val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(channelID, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)

        }
        notificationManager.notify(0,builder.build())

    }

}