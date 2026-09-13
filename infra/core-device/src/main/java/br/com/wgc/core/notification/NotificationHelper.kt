package br.com.wgc.core.notification

import android.app.NotificationManager
import android.content.Context

/**
 * Utilitário para criação e registro automático de canais padrão de notificação
 * do ecossistema Android (obrigatórios a partir do Android 8.0 Oreo - API 26).
 *
 * Configura canais organizados por níveis de prioridade e caso de uso:
 * - Urgente (High importance com som e heads-up pop-up).
 * - Geral (Default importance com som).
 * - Baixa prioridade (Low importance silencioso).
 * - Downloads / Tarefas em background (Min importance sem som).
 *
 * @param context Contexto da aplicação.
 */
class NotificationHelper(
    context: Context,
) {
    private val channelCreator = CreateChannelNotification(context)

    /**
     * Cria e registra no [NotificationManager] do sistema os 4 canais de notificação padrão.
     *
     * @param applicationName Nome da aplicação ou prefixo para garantir identificadores de canal únicos.
     */
    fun createNotificationChannelsDefault(applicationName: String) {
        // 1. Canal Urgente: Para alertas importantes (chamadas, alarmes)
        channelCreator
            .with(
                channelId = "$URGENT_CHANNEL_ID-$applicationName",
                name = "Notificações Urgentes",
            ).withDescription("Alertas críticos e informações que exigem atenção imediata.")
            .withImportance(NotificationManager.IMPORTANCE_HIGH) // Som e pop-up na tela
            .withVibration(true)
            .withLight(true)
            .create()

        // 2. Canal Padrão: Para a maioria das notificações
        channelCreator
            .with(
                channelId = "$DEFAULT_CHANNEL_ID-$applicationName",
                name = "Notificações Gerais",
            ).withDescription("Atualizações gerais, mensagens e lembretes.")
            .withImportance(NotificationManager.IMPORTANCE_DEFAULT) // Som, aparece na gaveta
            .create()

        // 3. Canal de Baixa Prioridade: Para promoções e sugestões
        channelCreator
            .with(
                channelId = "$LOW_PRIORITY_CHANNEL_ID-$applicationName",
                name = "Promoções e Sugestões",
            ).withDescription("Ofertas e outras informações não urgentes.")
            .withImportance(NotificationManager.IMPORTANCE_LOW) // Sem som, sem pop-up
            .withVibration(false)
            .withLight(false)
            .create()

        // 4. Canal de Downloads/Progresso: Silencioso e não intrusivo
        channelCreator
            .with(
                channelId = "$DOWNLOAD_CHANNEL_ID-$applicationName",
                name = "Atualizações de Tarefas",
            ).withDescription("Mostra o progresso de downloads e outras operações em segundo plano.")
            .withImportance(NotificationManager.IMPORTANCE_MIN) // Sem som
            .withVibration(false)
            .create()
    }

    companion object {
        /** Identificador base do canal urgente com alta prioridade. */
        const val URGENT_CHANNEL_ID = "urgent_channel"

        /** Identificador base do canal padrão. */
        const val DEFAULT_CHANNEL_ID = "default_channel"

        /** Identificador base do canal de baixa prioridade. */
        const val LOW_PRIORITY_CHANNEL_ID = "low_priority_channel"

        /** Identificador base do canal de operações em background e downloads. */
        const val DOWNLOAD_CHANNEL_ID = "download_channel"
    }
}
