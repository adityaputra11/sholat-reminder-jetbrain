package com.adityaputra.sholatreminder.service

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

fun showLocalNotification(prayerName: String, prayerTime: String) {
    val notification = Notification(
        "Vcs Notifications",
        "Waktu Sholat Telah Tiba",
        "Waktu sholat $prayerName telah tiba pada pukul $prayerTime.",
        NotificationType.INFORMATION
    )
    Notifications.Bus.notify(notification)
}