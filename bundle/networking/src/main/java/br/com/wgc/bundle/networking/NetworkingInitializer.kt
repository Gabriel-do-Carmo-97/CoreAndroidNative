package br.com.wgc.bundle.networking

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingInitializer {

    @Provides
    @Singleton
    fun provideNetworkingFacade(
        @ApplicationContext context: Context
    ): NetworkingFacade {
        return NetworkingFacade(context)
    }
}

class NetworkingFacade(
    private val context: Context
) {
    fun getStatus(): String = "Networking Bundle initialized successfully for package ${context.packageName}"
}
