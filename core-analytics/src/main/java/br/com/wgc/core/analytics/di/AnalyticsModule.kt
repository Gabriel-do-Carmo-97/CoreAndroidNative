package br.com.wgc.core.analytics.di

import br.com.wgc.core.analytics.AnalyticsTracker
import br.com.wgc.core.analytics.CompositeAnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt específico para provimento de dependências de analytics e rastreamento de eventos.
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    /**
     * Provê a instância singleton de [AnalyticsTracker] com sanitização de PII ativa.
     */
    @Provides
    @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker {
        return CompositeAnalyticsTracker(trackers = emptyList(), enablePiiMasking = true)
    }
}
