package com.g0g4n.icecream

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null
    private var notificationIdCounter = 1 // Initialize a counter for unique notification IDs

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "onReceive() called")
        createNotificationChannel(context)
        sendNotification(context)
        showToast(context)
        playSound(context)
    }

    private fun createNotificationChannel(context: Context) {
        Log.d("NotificationReceiver", "createNotificationChannel() called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ice Cream Channel"
            val descriptionText = "Channel for Ice Cream notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ice_cream_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(context: Context) {
        Log.d("NotificationReceiver", "sendNotification() called")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            notificationIdCounter,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "ice_cream_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentTitle("ICE CREAM!!!!!")
            .setContentText("ICE CREAM!!!!!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        // Check for permission before sending the notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                with(NotificationManagerCompat.from(context)) {
                    notify(notificationIdCounter, builder.build()) // Use a unique ID
                }
            } catch (e: SecurityException) {
                Log.e("NotificationReceiver", "SecurityException: Notification permission denied", e)
                // Handle the exception (e.g., log it, show a message to the user)
            }
        } else {
            Log.w("NotificationReceiver", "Notification permission not granted")
            // Handle the case where the permission is not granted (e.g., log it, don't send the notification)
        }
        notificationIdCounter++
    }

    private fun showToast(context: Context) {
        Toast.makeText(context, "ICE CREAM!!!!!!", Toast.LENGTH_SHORT).show()
    }

    private fun playSound(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.biden_soda)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}