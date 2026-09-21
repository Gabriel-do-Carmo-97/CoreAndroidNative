package br.com.wgc.core.device.notification

/**
 * Estrutura tipada de dados extraídos de um payload de notificação push.
 *
 * @property title Título exibido no alerta.
 * @property body Corpo ou descrição textual.
 * @property deepLink URI para navegação profunda no app.
 * @property channel Canal corporativo de destino.
 * @property metadata Mapa com dados extras enviados pelo backend.
 */
data class PushNotificationPayload(
    val title: String,
    val body: String,
    val deepLink: String? = null,
    val channel: NotificationChannelConfig = NotificationChannelConfig.GENERAL,
    val metadata: Map<String, String> = emptyMap(),
)

/**
 * Utilitário corporativo para conversão e higienização segura de payloads push.
 */
object NotificationPayloadParser {
    fun parse(data: Map<String, String>): PushNotificationPayload {
        val title = data["title"] ?: data["alert_title"] ?: "Notificação"
        val body = data["body"] ?: data["alert_body"] ?: data["message"] ?: ""
        val deepLink = data["deep_link"] ?: data["link"] ?: data["url"]
        val channelKey = data["channel"]?.uppercase()

        val channel =
            NotificationChannelConfig.entries.firstOrNull { it.name == channelKey }
                ?: NotificationChannelConfig.GENERAL

        return PushNotificationPayload(
            title = title,
            body = body,
            deepLink = deepLink,
            channel = channel,
            metadata = data,
        )
    }
}
