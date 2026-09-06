package br.com.wgc.coreandroidnative

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SampleAppModule {
    // Exemplo de módulo Hilt da aplicação consumidora para prover dependências customizadas.
}
