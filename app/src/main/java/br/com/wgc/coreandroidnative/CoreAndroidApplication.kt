package br.com.wgc.coreandroidnative

import android.app.Application
import android.app.NotificationManager
import br.com.wgc.core.notification.CreateChannelNotification
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreAndroidApplication(): Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }


    private fun createNotificationChannels() {
        // Canais só existem a partir do Android Oreo (API 26)
        val channelCreator = CreateChannelNotification(this)

        // 1. Canal Urgente: Para alertas importantes (chamadas, alarmes)
        channelCreator.with(
            channelId = URGENT_CHANNEL_ID,
            name = "Notificações Urgentes"
        )
            .withDescription("Alertas críticos e informações que exigem atenção imediata.")
            .withImportance(NotificationManager.IMPORTANCE_HIGH) // Som e pop-up na tela
            .withVibration(true)
            .withLight(true)
            .create()

        // 2. Canal Padrão: Para a maioria das notificações
        channelCreator.with(
            channelId = DEFAULT_CHANNEL_ID,
            name = "Notificações Gerais"
        )
            .withDescription("Atualizações gerais, mensagens e lembretes.")
            .withImportance(NotificationManager.IMPORTANCE_DEFAULT) // Som, aparece na gaveta
            .create() // Usa vibração e luz padrão

        // 3. Canal de Baixa Prioridade: Para promoções e sugestões
        channelCreator.with(
            channelId = LOW_PRIORITY_CHANNEL_ID,
            name = "Promoções e Sugestões"
        )
            .withDescription("Ofertas e outras informações não urgentes.")
            .withImportance(NotificationManager.IMPORTANCE_LOW) // Sem som, sem pop-up
            .withVibration(false)
            .withLight(false)
            .create()

        // 4. Canal de Downloads/Progresso: Silencioso e não intrusivo
        channelCreator.with(
            channelId = DOWNLOAD_CHANNEL_ID,
            name = "Atualizações de Tarefas"
        )
            .withDescription("Mostra o progresso de downloads e outras operações em segundo plano.")
            .withImportance(NotificationManager.IMPORTANCE_MIN) // Sem som
            .withVibration(false)
            .create()
    }

    companion object {
        const val URGENT_CHANNEL_ID = "urgent_channel"
        const val DEFAULT_CHANNEL_ID = "default_channel"
        const val LOW_PRIORITY_CHANNEL_ID = "low_priority_channel"
        const val DOWNLOAD_CHANNEL_ID = "download_channel"
    }
}