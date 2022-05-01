package com.example.pushnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var TAG = "dipu"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("com.example.pushnotification_FCM-MESSAGE"))

    }

    override fun onDestroy() {
        super.onDestroy()
      unregisterReceiver(receiver)
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null && intent.action == "com.example.pushnotification_FCM-MESSAGE") {

                val title = intent.getStringExtra("title")
                val message = intent.getStringExtra("body")
                val image = intent.getStringExtra("image")
                tvTitle.text = title
                tvBody.text = message
                Glide.with(imageView).load(image).into(imageView)

                Log.d(TAG, message.toString())
                Log.d(TAG, title.toString())
            }
        }
    }
}




/*
for(String key: getIntent().getExtras().keySet()){

}

private BroadcastReceiver mHandler=new BroadcastReceiver(){
@override
public void onReceive(Context context , Intent intent){
}
}


 */


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

