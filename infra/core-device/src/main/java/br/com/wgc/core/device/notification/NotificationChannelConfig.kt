package br.com.wgc.core.device.notification

import androidx.core.app.NotificationManagerCompat

/**
 * Categorias padronizadas de canais corporativos de notificação para aplicativos WGC.
 *
 * @property channelId Identificador único do canal no sistema Android.
 * @property channelName Nome visível nas configurações de notificações.
 * @property descriptionText Descrição detalhada do objetivo do canal.
 * @property importance Nível de prioridade e interrupção conforme [NotificationManagerCompat].
 */
enum class NotificationChannelConfig(
    val channelId: String,
    val channelName: String,
    val descriptionText: String,
    val importance: Int,
) {
    SECURITY(
        channelId = "wgc_channel_security",
        channelName = "Segurança e Autenticação",
        descriptionText = "Alertas de login, confirmação 2FA e alterações de credenciais",
        importance = NotificationManagerCompat.IMPORTANCE_HIGH,
    ),
    TRANSACTIONS(
        channelId = "wgc_channel_transactions",
        channelName = "Transações e Pedidos",
        descriptionText = "Atualizações em tempo real de pedidos, compras e transferências",
        importance = NotificationManagerCompat.IMPORTANCE_DEFAULT,
    ),
    GENERAL(
        channelId = "wgc_channel_general",
        channelName = "Comunicações Gerais",
        descriptionText = "Comunicados institucionais, avisos de sistema e novidades",
        importance = NotificationManagerCompat.IMPORTANCE_LOW,
    ),
}
