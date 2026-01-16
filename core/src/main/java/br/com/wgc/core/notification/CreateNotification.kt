package br.com.wgc.core.notification

import android.Manifest
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
 * de diferentes tipos de notificações (Padrão, BigText, BigPicture, etc.).
 */
class CreateNotification(private val context: Context) {

    /**
     * Inicia a construção de uma nova notificação.
     *
     * @param channelId O ID do canal de notificação onde ela será exibida.
     * @param textTitle O título principal da notificação.
     */
    fun with(channelId: String, textTitle: String): Builder {
        return Builder(channelId, textTitle)
    }

    inner class Builder(channelId: String, textTitle: String) {

        // O construtor base da notificação do AndroidX. É privado para ser controlado pelo nosso Builder.
        private val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(textTitle)

        /**
         * Adiciona os elementos básicos e obrigatórios da notificação.
         *
         * @param icon O ícone pequeno que aparece na barra de status.
         * @param textContent O texto principal do corpo da notificação.
         */
        fun withContent(@DrawableRes icon: Int, textContent: String): Builder {
            builder.setSmallIcon(icon)
                .setContentText(textContent)
            return this
        }

        /**
         * Define a prioridade da notificação.
         * Ex: NotificationCompat.PRIORITY_HIGH, PRIORITY_DEFAULT.
         */
        fun withPriority(priority: Int): Builder {
            builder.priority = priority
            return this
        }

        /**
         * Torna a notificação autocancelável (some ao ser tocada).
         */
        fun autoCancel(isAutoCancel: Boolean): Builder {
            builder.setAutoCancel(isAutoCancel)
            return this
        }

        /**
         * Adiciona um estilo de texto longo (BigTextStyle) à notificação,
         * permitindo que ela seja expandida para mostrar mais texto.
         *
         * @param bigText O texto completo a ser exibido na notificação expandida.
         */
        fun withBigTextStyle(bigText: String): Builder {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
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
            builder.setLargeIcon(largeImage) // Ícone grande na visualização recolhida
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(largeImage) // Imagem na visualização expandida
                        .setBigContentTitle(imageTitle)
                        .setSummaryText(imageDescription)
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
        fun withProgressStyle(max: Int, progress: Int, indeterminate: Boolean, isOngoing: Boolean = true): Builder {
            builder.setProgress(max, progress, indeterminate)
                .setOngoing(isOngoing)
            return this
        }


        /**
         * Adiciona um estilo de mídia (MediaStyle) à notificação.
         */
//        fun withMediaStyle(token: MediaSessionCompat.Token, compactActions: IntArray): Builder {
//            builder.setStyle(
//                NotificationCompat.Style()
//                    .setMediaSession(token)
//                    .setShowActionsInCompactView(*compactActions)
//            )
//            builder.setOngoing(true)
//            return this
//        }

        /**
         * Adiciona um estilo de mensagem (MessagingStyle) à notificação.
         */
//        fun withMessagingStyle(user: NotificationCompat.Person, conversationTitle: String?, messages: List<NotificationCompat.MessagingStyle.Message>): Builder {
//            val style = NotificationCompat.MessagingStyle(user)
//                .setConversationTitle(conversationTitle)
//            messages.forEach { style.addMessage(it) }
//            builder.setStyle(style)
//            return this
//        }

        /**
         * Adiciona um estilo de chamada (CallStyle) à notificação.
         */
//        fun withCallStyle(
//            isIncoming: Boolean,
//            caller: NotificationCompat.Person,
//            declineIntent: PendingIntent,
//            answerIntent: PendingIntent? = null,
//            fullScreenIntent: PendingIntent? = null
//        ): Builder {
//            builder.setCategory(NotificationCompat.CATEGORY_CALL)
//                .setOngoing(true)
//            fullScreenIntent?.let { builder.setFullScreenIntent(it, true) }
//
//            val style = if (isIncoming && answerIntent != null) {
//                NotificationCompat.CallStyle.forIncomingCall(caller, declineIntent, answerIntent)
//            } else {
//                NotificationCompat.CallStyle.forOngoingCall(caller, declineIntent)
//            }
//            builder.setStyle(style)
//            return this
//        }

        /**
         * Configura a notificação para atualizações silenciosas após a primeira exibição.
         */
        fun asLiveUpdate(isLiveUpdate: Boolean): Builder {
            builder.setOnlyAlertOnce(isLiveUpdate)
            return this
        }

        /** Adiciona uma ação (botão) à notificação. **/
        fun withAction(action: NotificationCompat.Action): Builder {
            builder.addAction(action)
            return this
        }

        /**
         * Adiciona uma ação (botão) à notificação, como "Cancelar".
         *
         * @param icon Ícone para a ação.
         * @param text Título da ação (ex: "Cancelar").
         * @param pendingIntent A ação a ser executada quando o botão for tocado.
         */
        fun withAction(@DrawableRes icon: Int, text: String, pendingIntent: PendingIntent): Builder {
            builder.addAction(icon, text, pendingIntent)
            return this
        }

        /**
         * Define a ação principal a ser executada quando a notificação é tocada.
         * Geralmente, abre uma Activity.
         *
         * @param kClass A classe da Activity a ser aberta.
         */
        fun withTapAction(kClass: KClass<*>): Builder {
            val intent = Intent(context, kClass.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
            return this
        }

        /**
         * Constrói e exibe a notificação no sistema.
         * Requer a permissão POST_NOTIFICATIONS.
         *
         * @param notificationId Um ID único para esta notificação.
         */
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        fun show(notificationId: Int) {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }
}
