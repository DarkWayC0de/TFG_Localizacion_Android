package com.example.localizacionInalambrica.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.localizacionInalambrica.other.Constants

object Notification {

    fun crearNotificationChannel(notificationmanager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANEL_ID,
            Constants.NOTIFICATION_CHANEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationmanager.createNotificationChannel(channel)
    }

}