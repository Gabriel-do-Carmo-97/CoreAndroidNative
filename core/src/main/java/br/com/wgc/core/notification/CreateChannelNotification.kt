package br.com.wgc.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.wgc.core.R
import kotlin.jvm.java
import kotlin.reflect.KClass

class CreateChannelNotification {


    fun createChannel(
        context: Context,
        channelID: String,
        name: String,
        description: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    ) {
        val mChannel = NotificationChannel(channelID, name, importance).apply {
            this.description = description
        }
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    fun createNotification(
        context: Context,
        channelID: String,
        @DrawableRes icon: Int,
        textTitle: String,
        textContent: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    ): NotificationCompat.Builder = NotificationCompat.Builder(context, channelID)
        .setSmallIcon(icon)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)


    fun createLongNotification(
        context: Context,
        channelID: String,
        @DrawableRes icon: Int,
        textTitle: String,
        textContent: String,
        bigText: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    ): NotificationCompat.Builder = NotificationCompat.Builder(context, channelID)
        .setSmallIcon(icon)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(bigText)
        )
        .setPriority(priority)

    fun createReactUser(
        context: Context,
        kClass: KClass<*>,
        channelID: String,
        @DrawableRes icon: Int,
        textTitle: String,
        textContent: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    ): NotificationCompat.Builder {
        val intent = Intent(context, kClass::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, channelID)
            .setSmallIcon(icon)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }


    fun showNotification(
        context: Context,
        notificationID: Int,
        builder: NotificationCompat.Builder,
    ){
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array&lt;out String&gt;,
                //                                        grantResults: IntArray)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return@with
            }
            notify(notificationID, builder.build())
        }

    }
}