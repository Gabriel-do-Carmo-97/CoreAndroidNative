package br.com.wgc.core.device.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Gerenciador corporativo para criação de canais, checagem de permissões e construção de notificações.
 *
 * @property context Contexto de aplicação para acesso a serviços de sistema.
 */
class NotificationManagerHelper(
    private val context: Context,
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelConfig.entries.forEach { config ->
                val channel =
                    NotificationChannel(
                        config.channelId,
                        config.channelName,
                        config.importance,
                    ).apply {
                        description = config.descriptionText
                    }
                notificationManager?.createNotificationChannel(channel)
            }
        }
    }

    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun buildNotification(
        payload: PushNotificationPayload,
        smallIconResId: Int,
    ): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, payload.channel.channelId)
            .setSmallIcon(smallIconResId)
            .setContentTitle(payload.title)
            .setContentText(payload.body)
            .setPriority(payload.channel.importance)
            .setAutoCancel(true)
    }
}
