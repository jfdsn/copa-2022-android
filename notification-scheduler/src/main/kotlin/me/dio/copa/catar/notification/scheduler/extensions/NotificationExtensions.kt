package me.dio.copa.catar.notification.scheduler.extensions

import android.Manifest
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.dio.copa.catar.notification.scheduler.R

private const val CHANNEL_ID = "new_channel_video"
private const val NOTIFICATION_NAME = "Notificações"
private const val NOTIFICATION_INTENT_REQUEST_CODE = 0

fun Context.showNotification(title: String, content: String) {
    createNotificationChannel()
    val notification = getNotification(title, content)

    //Verifica se o usuario não aceita notificações e o informa da necessidade do mesmo
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestNotificationPermission()
        return
    }

    NotificationManagerCompat
        .from(this)
        .notify(content.hashCode(), notification)
}

private fun Context.createNotificationChannel() {
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, importance)

    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .createNotificationChannel(channel)
}

private fun Context.getNotification(title: String, content: String): Notification =
    NotificationCompat
        .Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_soccer)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(getOpenAppPendingIntent())
        .setAutoCancel(true)
        .build()

private fun Context.getOpenAppPendingIntent() = PendingIntent.getActivity(
    this,
    NOTIFICATION_INTENT_REQUEST_CODE,
    packageManager.getLaunchIntentForPackage(packageName),
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
)


private fun Context.requestNotificationPermission() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Permissão de Notificação")
    builder.setMessage("Para receber notificações, é necessário conceder permissão de notificação.")
    builder.setPositiveButton("OK") { _, _ ->
        openAppSettings()
    }
    builder.setNegativeButton("Cancelar") { dialog, _ ->
        dialog.dismiss()
    }
    builder.show()
}

// Redireciona o usuário para as configurações do aplicativo
private fun Context.openAppSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.data = Uri.parse("package:$packageName")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    startActivity(intent)
}