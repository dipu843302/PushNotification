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

                                    // To receive the messages, use a service that extends FirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {

    //generate the notification
    // attach the notification created with custom layout
    // show the notification

    // when the  app is in the background. In this case, the notification is delivered to the device’s system tray
    // A user tap on a notification opens the app launcher by default.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
       if (remoteMessage.notification!=null){
           generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
       }
    }

    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews("com.example.pushnotification", R.layout.notification)
        remoteViews.setTextViewText(R.id.tittle, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.app_logo, R.drawable.app_icon)
        return remoteViews
    }

    // navigate to activity
    fun generateNotification(tittle: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                                     // after clicking the notification , it will be destroy
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)


        // channel id, channel name
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
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
// image url
// https://freeiconshop.com/wp-content/uploads/edd/notification-flat.png