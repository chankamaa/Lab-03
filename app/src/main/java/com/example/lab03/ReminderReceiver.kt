package com.example.lab03

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "reminder_notifications"

    override fun onReceive(context: Context, intent: Intent) {
        val reminderTitle = intent.getStringExtra("reminderTitle") ?: "Reminder"

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Notifications"
            val descriptionText = "Notification for scheduled reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app's icon here
            .setContentTitle("Reminder")
            .setContentText(reminderTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(101, notification)
        }
    }
}
