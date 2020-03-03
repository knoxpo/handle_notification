package com.knoxpo.handle_notification_example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor, "handle_notification_method")
                .setMethodCallHandler { methodCall, result ->
                    when (methodCall.method) {
                        "notification" -> {
                            showNotification("Pratik", "pratik.sherdiwala@knoxpo.com", "Hello")
                        }
                        else -> {
                            result.notImplemented()
                        }
                    }
                }
    }

    private fun showNotification(name: String, email: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                    NotificationChannel("CHANNEL_ID", name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager?.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentInfo(email)
                .setTicker(name)
                .setContentText(message)
                .setStyle(NotificationCompat.MessagingStyle(name))
                .setSmallIcon(android.R.drawable.star_big_on)
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                .setContentIntent(setPendingIntent())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("CHANNEL_ID") // Channel ID
        }
        notificationManager?.notify(0, builder.build())
    }

    private fun setPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = "ACTION_NOTIFICATION"

        intent.putExtra("name", "Pratik")
        intent.putExtra("email", "Pratik.sherdiwala@gmail.com")
        intent.putExtra("message", "Hello flutter.. I m calling from native..")

        return PendingIntent
                .getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
    }
}
