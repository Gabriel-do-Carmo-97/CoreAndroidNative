package br.com.wgc.bundle.presentation

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PresentationInitializer {

    @Provides
    @Singleton
    fun providePresentationFacade(
        @ApplicationContext context: Context
    ): PresentationFacade {
        return PresentationFacade(context)
    }
}

class PresentationFacade(
    private val context: Context
) {
    fun getStatus(): String = "Presentation Bundle initialized successfully for package ${context.packageName}"
}
