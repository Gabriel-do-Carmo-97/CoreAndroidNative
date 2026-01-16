package br.com.wgc.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/**
 * Classe responsável por criar canais de notificação do Android.
 *
 * Utiliza o padrão Builder para simplificar e flexibilizar a criação de canais,
 * exigindo um Build.VERSION_CODES.O ou superior.
 */
class CreateChannelNotification(private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Inicia a construção de um novo canal de notificação.
     *
     * @param channelId O ID único para este canal.
     * @param name O nome do canal que será visível ao usuário nas configurações.
     */
    fun with(channelId: String, name: String): Builder {
        return Builder(channelId, name)
    }

    inner class Builder(
        private val channelId: String,
        private val name: String
    ) {
        // Valores padrão para as configurações do canal
        private var description: String = ""
        private var importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        private var enableLight: Boolean = true
        private var enableVibration: Boolean = true

        /**
         * Define a descrição do canal, visível ao usuário nas configurações.
         */
        fun withDescription(description: String): Builder {
            this.description = description
            return this // Retorna a própria instância para encadear chamadas
        }

        /**
         * Define a importância do canal. Use as constantes de NotificationManager.
         * Ex: NotificationManager.IMPORTANCE_HIGH
         */
        fun withImportance(importance: Int): Builder {
            this.importance = importance
            return this
        }

        /**
         * Habilita ou desabilita a luz de notificação para este canal.
         */
        fun withLight(enabled: Boolean): Builder {
            this.enableLight = enabled
            return this
        }

        /**
         * Habilita ou desabilita a vibração para este canal.
         */
        fun withVibration(enabled: Boolean): Builder {
            this.enableVibration = enabled
            return this
        }

        /**
         * Constrói e registra o NotificationChannel no sistema.
         * Este é o passo final da construção.
         */
        fun create() {
            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = this@Builder.description
                this.enableLights(this@Builder.enableLight)
                this.enableVibration(this@Builder.enableVibration)
                // Outras configurações (como som) poderiam ser adicionadas aqui
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
