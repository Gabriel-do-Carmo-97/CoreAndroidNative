package br.com.wgc.coreandroidnative

import br.com.wgc.core.di.CoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SampleAppModule {

    @Provides
    @Singleton
    @Named(CoreModule.DATASTORE_NAME_QUALIFIER) // <-- Usando a constante da biblioteca para garantir que o nome seja o mesmo.
    fun provideDataStoreName(): String {
        // Este é o valor específico para este aplicativo.
        // Se outro aplicativo usasse sua lib :core, ele poderia fornecer um nome diferente.
        return "wgc_core_app_preferences"
    }

    @Provides
    @Singleton
    @Named(CoreModule.SHARED_PREFERENCES_NAME_QUALIFIER) // <-- Usando a constante da biblioteca para garantir que o nome seja o mesmo.
    fun provideSharedPreferencesName(): String {
        // Este é o valor específico para este aplicativo.
        // Se outro aplicativo usasse sua lib :core, ele poderia fornecer um nome diferente.
        return "wgc_core_app_preferences"
    }
}