package com.example.pushnotification

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelID = "notification_channel"
const val channelName = "com.example.pushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["title"]
        val body = message.data["body"]
        val image = message.data["image"]

        if (isAppOnForeground()) {
            Log.d("TAG", "onMessageReceived: onForGround $title $body $image")
            val intent = Intent("com.example.pushnotification_FCM-MESSAGE")

            val messageData = MessageData(title.toString(), body.toString(), image.toString())
            intent.putExtra("message", messageData)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        } else {
            showNotification(title, body, image)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(title: String?, body: String?, image: String?) {
        Log.d("show", "$title $body $image")

        val intent = Intent(this, MainActivity::class.java)

        val messageData = MessageData(title.toString(), body.toString(), image.toString())
        intent.putExtra("message", messageData)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(channelName)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }

    /*Check if the application is in foreground or not*/
    private fun isAppOnForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState
            .isAtLeast(Lifecycle.State.STARTED)
    }
}


//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("dipu","itCalled")
//        if (remoteMessage.data.isNotEmpty()){
//            Log.d("dipu","itCalled2")
//
//            val title= remoteMessage.data["title"]
//            val message= remoteMessage.data["message"]
//
//          val intent=Intent("com.example.pushnotification_FCM-MESSAGE")
//          intent.putExtra("title",title)
//          intent.putExtra("message",message)
//          Log.d("dipu",title.toString())
//            val localBroadcastManager=LocalBroadcastManager.getInstance(this)
//            localBroadcastManager.sendBroadcast(intent)
//        }
//    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("message", "xyz")
//        if (remoteMessage.notification != null) {
//            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//            if (!isAppInForeground(activityManager)) {
//               // generateNotification(
//                    remoteMessage.notification!!.title!!
//                    remoteMessage.notification!!.body!!
//              //  )
//            }
//        }
//    }
//
//    // navigate to activity
//    @RequiresApi(Build.VERSION_CODES.O)
//    @SuppressLint("UnspecifiedImmutableFlag")
//    fun generateNotification(tittle: String, message: String) {
//
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        // after clicking the notification , it will be destroy
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        // channel id, channel name
//        var builder: NotificationCompat.Builder =
//            NotificationCompat.Builder(applicationContext, channelID)
//                .setSmallIcon(R.drawable.app_icon)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
//                .setGroup(channelName)
//                .setContentIntent(pendingIntent)
//                .setStyle(NotificationCompat.InboxStyle()
//                    .addLine("title2"+" "+"message2")
//                    .addLine("title1"+" "+"message1")
//                    .setBigContentTitle("2 new messages")
//                    .setSummaryText("user@gmail.com"))
//                .setGroupSummary(true)
//
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val notificationChannel =
//                    NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
//                notificationManager.createNotificationChannel(notificationChannel)
//            }
//            notificationManager.notify(0, builder.build())
//        }
//
//        fun isAppInForeground(activityManager: ActivityManager): Boolean {
//            val appProcesses = activityManager.runningAppProcesses ?: return false
//            for (appProcess in appProcesses) {
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
//                    return true
//                }
//            }
//            return false
//        }


// image url
// https://freeiconshop.com/wp-content/uploads/edd/notification-flat.png