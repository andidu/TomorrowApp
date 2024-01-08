package com.adorastudios.tomorrowapp.presentation.screens.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.adorastudios.tomorrowapp.MainActivity
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.model.Todo

fun Context.sendNotification(amount: Int, random3: List<Todo>) {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    val notificationManager =
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val titleNotification =
        this.getString(if (amount != 0) R.string.notification_titleNotEmpty else R.string.notification_titleEmpty)
    val subtitleNotification = if (amount == 0) {
        this.getString(R.string.notification_descriptionEmpty)
    } else {
        if (amount <= 3) {
            this.getString(R.string.notification_descriptionLow) +
                random3.joinToString(separator = ", ") { it.title }
        } else {
            this.getString(R.string.notification_descriptionLow) +
                random3.joinToString(separator = ", ") { it.title } +
                this.getString(
                    R.string.notification_descriptionMany,
                    amount - 3,
                )
        }
    }

    val pendingIntent =
        PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val notification = NotificationCompat.Builder(
        this,
        NOTIFICATION_CHANNEL,
    )
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(titleNotification)
        .setContentText(subtitleNotification)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    val channel =
        NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        )

    channel.enableLights(true)
    channel.lightColor = Color.BLUE
    channel.enableVibration(true)
    channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
    channel.setSound(ringtoneManager, audioAttributes)
    notificationManager.createNotificationChannel(channel)

    notificationManager.notify(NOTIFICATION_ID, notification.build())
}

const val NOTIFICATION_ID = 0
const val NOTIFICATION_NAME = "General notifications"
const val NOTIFICATION_CHANNEL = "periodicNotifications_channel"
