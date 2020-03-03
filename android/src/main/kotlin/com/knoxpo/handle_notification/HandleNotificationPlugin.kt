package com.knoxpo.handle_notification

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** HandleNotificationPlugin */
class HandleNotificationPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private val TAG = HandleNotificationPlugin::class.java.simpleName
    private val METHOD_CHANNEL = "handle_notification_method"
    private val EVENT_CHANNEL = "handle_notification_event"
    private val CHANNEL_ID = "$TAG.CHANNEL_ID"
    private val ACTION_NOTIFICATION = "ACTION_NOTIFICATION"

    private var context: Context? = null
    private var activity: Activity? = null

    private val METHOD_OPEN_SCREEN = "openScreen"

    private var methodChannel: MethodChannel? = null
    private var eventChannel: EventChannel? = null
    private var eventChannelSink: EventChannel.EventSink? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext

        flutterPluginBinding.applicationContext.let {
            this.context = it
        }

        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, METHOD_CHANNEL)
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, EVENT_CHANNEL)
        eventChannel?.setStreamHandler(
                object : EventChannel.StreamHandler {
                    override fun onListen(arguments: Any?, event: EventChannel.EventSink?) {
                        Log.d(TAG, "on Listen")
                        eventChannelSink = event

                        val isFromHistory = (activity!!.intent!!.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0

                        if (activity?.intent?.action == ACTION_NOTIFICATION && !isFromHistory) {
                            event?.success("Hello World")
                        }
                    }

                    override fun onCancel(arguments: Any?) {
                        Log.e(TAG, "onCancel")
                    }
                }
        )
        methodChannel?.setMethodCallHandler(this)

    }

    override fun onDetachedFromEngine(p0: FlutterPlugin.FlutterPluginBinding) {
        Log.e(TAG, "onDetachedFromEngine")
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            METHOD_OPEN_SCREEN -> {
                val isFromHistory = (activity!!.intent!!.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0

                if (this.activity?.intent?.action == ACTION_NOTIFICATION && !isFromHistory) {
                    result.success(true)
                } else {
                    result.success(false)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun showNotification(name: String, email: String, message: String) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                    NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager?.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentInfo(email)
                .setTicker(name)
                .setContentText(message)
                .setStyle(NotificationCompat.MessagingStyle(name))
                .addAction(0, "ACCEPT", null)
                .addAction(0, "REJECT", null)
                .setSmallIcon(android.R.drawable.star_big_on)
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                //.setContentIntent(setPendingIntent())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        notificationManager?.notify(0, builder.build())
    }

    override fun onDetachedFromActivity() {}

    override fun onReattachedToActivityForConfigChanges(p0: ActivityPluginBinding) {}

    override fun onAttachedToActivity(activityBinding: ActivityPluginBinding) {
        this.activity=activityBinding.activity

        activityBinding.addOnNewIntentListener {
            if (it.action == ACTION_NOTIFICATION) {
                Log.e(TAG, "${it.action}")
                try {
                    if (it.extras != null) {
                        val dataMap = mutableMapOf<String, Any>()

                        it.extras!!.keySet().map { key ->
                            dataMap[key] = it.extras?.get(key) as Any
                        }

                        eventChannelSink?.success(dataMap)
                    } else {
                        eventChannelSink?.success("No result to be set")
                    }
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "Error ", e)
                }
                true
            } else {
                false
            }
        }
    }
    override fun onDetachedFromActivityForConfigChanges() {}
}

/***
 * This 'plugin_name' is used to handle action of notification on Android platform.
 * extends the PluginMixin class with your StatefulWidget class
 *
 *  @override
 *  void onNotificationReceived(Object data)
 *
 *  by overriding this method you can get data
 *
 */

