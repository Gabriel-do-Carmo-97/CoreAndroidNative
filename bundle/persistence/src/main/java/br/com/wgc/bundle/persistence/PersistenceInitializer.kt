package br.com.wgc.bundle.persistence

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Inicializador e provedor de dependências Hilt para o Bundle de Persistência.
 * Fornece acesso centralizado aos recursos de Storage e Database.
 */
@Module
@InstallIn(SingletonComponent::class)
object PersistenceInitializer {

    @Provides
    @Singleton
    fun providePersistenceFacade(
        @ApplicationContext context: Context
    ): PersistenceFacade {
        return PersistenceFacade(context)
    }
}

class PersistenceFacade(
    private val context: Context
) {
    fun getStatus(): String = "Persistence Bundle initialized successfully for package ${context.packageName}"
}
