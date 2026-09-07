package br.com.wgc.core.di

import br.com.wgc.core.analytics.AnalyticsTracker
import br.com.wgc.core.analytics.di.AnalyticsModule
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.coroutines.DefaultCoroutineDispatchers
import br.com.wgc.core.logging.CoreLogger
import br.com.wgc.core.logging.DefaultCoreLogger
import br.com.wgc.core.storage.di.StorageModule
import br.com.wgc.core.ui.media.DefaultImageCompressor
import br.com.wgc.core.ui.media.ImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt agregador corporativo do CoreAndroidNative.
 *
 * Provê as instâncias centrais comuns e expõe as constantes e qualificadores de compatibilidade.
 * As dependências específicas de cada domínio agora são modularizadas em seus respectivos módulos:
 * - [StorageModule] em `:core-storage`
 * - [DeviceModule] em `:core-device`
 * - [AnalyticsModule] em `:core-analytics`
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    /** Qualifier [Named] para o nome do arquivo Preferences DataStore em disco. */
    const val DATASTORE_NAME_QUALIFIER = StorageModule.DATASTORE_NAME_QUALIFIER

    /** Qualifier [Named] para o nome do arquivo SharedPreferences em disco. */
    const val SHARED_PREFERENCES_NAME_QUALIFIER = StorageModule.SHARED_PREFERENCES_NAME_QUALIFIER

    /** Qualifier [Named] para a implementação padrão de [KeyValueStorage] (não criptografada). */
    const val STORAGE_DEFAULT_QUALIFIER = StorageModule.STORAGE_DEFAULT_QUALIFIER

    /** Qualifier [Named] para a implementação segura de [KeyValueStorage] (criptografada via KeyStore). */
    const val STORAGE_ENCRYPTED_QUALIFIER = StorageModule.STORAGE_ENCRYPTED_QUALIFIER

    /** Nome padrão do arquivo DataStore em disco. */
    const val DEFAULT_DATASTORE_NAME = StorageModule.DEFAULT_DATASTORE_NAME

    /** Nome padrão do arquivo SharedPreferences em disco. */
    const val DEFAULT_SHARED_PREFERENCES_NAME = StorageModule.DEFAULT_SHARED_PREFERENCES_NAME

    /**
     * Provê a instância singleton de [CoroutineDispatchers] padrão.
     */
    @Provides
    @Singleton
    fun provideCoroutineDispatchers(): CoroutineDispatchers {
        return DefaultCoroutineDispatchers()
    }

    /**
     * Provê a instância singleton de [CoreLogger] com mascaramento de PII ativo.
     */
    @Provides
    @Singleton
    fun provideCoreLogger(): CoreLogger {
        return DefaultCoreLogger()
    }

    /**
     * Provê a instância singleton de [ImageCompressor] para processamento assíncrono de imagens.
     */
    @Provides
    @Singleton
    fun provideImageCompressor(
        dispatchers: CoroutineDispatchers
    ): ImageCompressor {
        return DefaultImageCompressor(dispatchers)
    }

    // Métodos de conveniência para retrocompatibilidade com testes e consumers diretos:
    fun provideDataStoreName(): String = StorageModule.provideDataStoreName()
    fun provideSharedPreferencesName(): String = StorageModule.provideSharedPreferencesName()
    fun provideAnalyticsTracker(): AnalyticsTracker = AnalyticsModule.provideAnalyticsTracker()
}
