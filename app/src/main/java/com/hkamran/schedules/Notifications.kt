package com.hkamran.schedules

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createNotificationChannel(
    context: Context,
    channelId: String,
    channelName: String,
    channelDescription: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}