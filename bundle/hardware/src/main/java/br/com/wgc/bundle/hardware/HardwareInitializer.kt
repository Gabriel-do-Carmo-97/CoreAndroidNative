package br.com.wgc.bundle.hardware

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HardwareInitializer {
    @Provides
    @Singleton
    fun provideHardwareFacade(
        @ApplicationContext context: Context,
    ): HardwareFacade = HardwareFacade(context)
}

class HardwareFacade(
    private val context: Context,
) {
    fun getStatus(): String = "Hardware Bundle initialized successfully for package ${context.packageName}"
}
