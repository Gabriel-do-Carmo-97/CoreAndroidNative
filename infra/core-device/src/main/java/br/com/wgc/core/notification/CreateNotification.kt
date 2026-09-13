package br.com.wgc.core.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.reflect.KClass

/**
 * Classe responsável por construir e exibir notificações.
 *
 * Utiliza o padrão Builder para permitir a criação passo a passo
 * de diferentes tipos de notificações (Padrão, BigText, BigPicture, Progress, etc.).
 */
class CreateNotification(
    private val context: Context,
) {
    /**
     * Inicia a construção de uma nova notificação.
     *
     * @param channelId O ID do canal de notificação onde ela será exibida.
     * @param textTitle O título principal da notificação.
     */
    fun with(
        channelId: String,
        textTitle: String,
    ): Builder = Builder(channelId, textTitle)

    inner class Builder(
        channelId: String,
        textTitle: String,
    ) {
        private val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId).setContentTitle(textTitle)

        /**
         * Adiciona os elementos básicos e obrigatórios da notificação.
         *
         * @param icon O ícone pequeno que aparece na barra de status.
         * @param textContent O texto principal do corpo da notificação.
         */
        fun withContent(
            @DrawableRes icon: Int,
            textContent: String,
        ): Builder {
            notificationBuilder
                .setSmallIcon(icon)
                .setContentText(textContent)
            return this
        }

        /**
         * Define a prioridade da notificação.
         * Ex: NotificationCompat.PRIORITY_HIGH, PRIORITY_DEFAULT.
         */
        fun withPriority(priority: Int): Builder {
            notificationBuilder.priority = priority
            return this
        }

        /**
         * Torna a notificação autocancelável (some ao ser tocada).
         */
        fun autoCancel(isAutoCancel: Boolean): Builder {
            notificationBuilder.setAutoCancel(isAutoCancel)
            return this
        }

        /**
         * Adiciona um estilo de texto longo (BigTextStyle) à notificação,
         * permitindo que ela seja expandida para mostrar mais texto.
         *
         * @param bigText O texto completo a ser exibido na notificação expandida.
         */
        fun withBigTextStyle(bigText: String): Builder {
            notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            return this
        }

        /**
         * Adiciona um estilo de imagem grande (BigPictureStyle) à notificação.
         *
         * @param largeImage A imagem em formato Bitmap.
         * @param imageTitle Título que aparece sobre a imagem quando expandido.
         * @param imageDescription Texto de resumo que aparece abaixo da imagem.
         */
        fun withBigPictureStyle(
            largeImage: Bitmap,
            imageTitle: String? = null,
            imageDescription: String? = null,
        ): Builder {
            notificationBuilder
                .setLargeIcon(largeImage)
                .setStyle(
                    NotificationCompat
                        .BigPictureStyle()
                        .bigPicture(largeImage)
                        .setBigContentTitle(imageTitle)
                        .setSummaryText(imageDescription),
                )
            return this
        }

        /**
         * Adiciona um estilo de barra de progresso à notificação.
         *
         * @param max O valor máximo do progresso (ex: 100).
         * @param progress O valor atual do progresso.
         * @param indeterminate Se o progresso é indeterminado (animação contínua).
         * @param isOngoing Se a notificação não pode ser dispensada pelo usuário.
         */
        fun withProgressStyle(
            max: Int,
            progress: Int,
            indeterminate: Boolean,
            isOngoing: Boolean = true,
        ): Builder {
            notificationBuilder
                .setProgress(max, progress, indeterminate)
                .setOngoing(isOngoing)
            return this
        }

        /**
         * Configura a notificação para atualizações silenciosas após a primeira exibição.
         */
        fun asLiveUpdate(isLiveUpdate: Boolean): Builder {
            notificationBuilder.setOnlyAlertOnce(isLiveUpdate)
            return this
        }

        /** Adiciona uma ação (botão) à notificação. **/
        fun withAction(action: NotificationCompat.Action): Builder {
            notificationBuilder.addAction(action)
            return this
        }

        /**
         * Adiciona uma ação (botão) à notificação, como "Cancelar".
         *
         * @param icon Ícone para a ação.
         * @param text Título da ação (ex: "Cancelar").
         * @param pendingIntent A ação a ser executada quando o botão for tocado.
         */
        fun withAction(
            @DrawableRes icon: Int,
            text: String,
            pendingIntent: PendingIntent,
        ): Builder {
            notificationBuilder.addAction(icon, text, pendingIntent)
            return this
        }

        /**
         * Define a ação principal através de um [PendingIntent] customizado.
         */
        fun withTapAction(pendingIntent: PendingIntent): Builder {
            notificationBuilder.setContentIntent(pendingIntent)
            return this
        }

        /**
         * Define a ação principal através de uma [Intent] customizada.
         */
        fun withTapAction(
            intent: Intent,
            requestCode: Int = 0,
            flags: Int = PendingIntent.FLAG_IMMUTABLE,
        ): Builder {
            val pendingIntent = PendingIntent.getActivity(context, requestCode, intent, flags)
            return withTapAction(pendingIntent)
        }

        /**
         * Define a ação principal a ser executada quando a notificação é tocada.
         * Abre a Activity indicada por [kClass].
         *
         * @param kClass A classe da Activity a ser aberta.
         */
        fun withTapAction(kClass: KClass<*>): Builder {
            val intent =
                Intent(context, kClass.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            return withTapAction(intent)
        }

        /**
         * Retorna a instância de [Notification] construída.
         * Útil para testes ou para uso em serviços de primeiro plano (Foreground Services).
         */
        fun build(): Notification = notificationBuilder.build()

        /**
         * Constrói e exibe a notificação no sistema.
         * Requer a permissão POST_NOTIFICATIONS.
         *
         * @param notificationId Um ID único para esta notificação.
         */
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        fun show(notificationId: Int) {
            NotificationManagerCompat.from(context).notify(notificationId, build())
        }
    }
}
