package com.example.pushnotification

import android.annotation.SuppressLint
import android.app.Notification
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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


const val channelID = "notification_channel"
const val channelName = "com.example.pushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var notificationString = ""
    val arrayList = ArrayList<MessageData>()
    var value = 0

    var messageList = ArrayList<MessageData>()

    var hashMap = HashMap<Int, ArrayList<MessageData>>()

    lateinit var shrd: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        shrd = getSharedPreferences("firebaseNotification", MODE_PRIVATE)
        editor = shrd.edit()

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
        var oldNotificatinList = ArrayList<MessageData>()
        val gson = Gson()

        var checkIsFirstTime = shrd.getBoolean("isFirstTime", true)

        if (checkIsFirstTime) {
            editor.putBoolean("isFirstTime", false).apply()
            var newHasMap = HashMap<Int, ArrayList<MessageData>>()
            var newNotificationList = ArrayList<MessageData>()
            newNotificationList.add(messageData)
            newHasMap.put(messageData.id, newNotificationList)

            val hashMapString = gson.toJson(newHasMap)
            // save in shared pref..
            editor.putString("hasString", hashMapString).apply()

        } else {

            val storedHasmapString = shrd.getString("hasString", "")

            val type: Type = object : TypeToken<HashMap<Int, ArrayList<MessageData>>>() {}.type
            val testHashMap2: HashMap<Int, ArrayList<MessageData>> =
                gson.fromJson(storedHasmapString, type)

            if (testHashMap2.containsKey(messageData.id)) {

                oldNotificatinList = testHashMap2.get(messageData.id)!!
                oldNotificatinList?.add(messageData)

                testHashMap2.put(messageData.id, oldNotificatinList!!)

                val testHashMapJson2 = gson.toJson(testHashMap2)
                editor.putString("hasString", testHashMapJson2).apply()


            } else {

                var newHasMap = HashMap<Int, ArrayList<MessageData>>()
                var newNotificationList = ArrayList<MessageData>()
                newNotificationList.add(messageData)
                newHasMap.put(messageData.id, newNotificationList)

                val hashMapString = gson.toJson(newHasMap)

                // save in shared pref..
                editor.putString("hasString", hashMapString).apply()

            }
        }
        // get from shared prefs

        //   Log.d("getData", storedHasmapString.toString())

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



        val style = NotificationCompat.InboxStyle(builder)
            .setBigContentTitle(messageData.title)
            .setSummaryText("You have " + arrayList.size + " Notifications.")

        for (notification in oldNotificatinList) {
            style.addLine(notification.body)
        }
        builder.setStyle(style)
        builder.setGroupSummary(true)
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