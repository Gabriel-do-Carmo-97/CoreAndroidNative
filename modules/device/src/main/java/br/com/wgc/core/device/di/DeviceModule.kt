package br.com.wgc.core.device.di

import android.content.Context
import br.com.wgc.core.device.DefaultDeviceInfo
import br.com.wgc.core.device.DefaultDeviceSecurityHelper
import br.com.wgc.core.device.DefaultHapticFeedbackHelper
import br.com.wgc.core.device.DefaultPermissionManager
import br.com.wgc.core.device.DeviceInfo
import br.com.wgc.core.device.DeviceSecurityHelper
import br.com.wgc.core.device.HapticFeedbackHelper
import br.com.wgc.core.device.PermissionManager
import br.com.wgc.core.network.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt específico para provimento de dependências de hardware, sensores e segurança do dispositivo.
 */
@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {

    /**
     * Provê a instância singleton do monitor de conectividade de rede [NetworkMonitor].
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }

    /**
     * Provê a instância singleton de [DeviceInfo] baseada no contexto da aplicação.
     */
    @Provides
    @Singleton
    fun provideDeviceInfo(
        @ApplicationContext context: Context
    ): DeviceInfo {
        return DefaultDeviceInfo(context)
    }

    /**
     * Provê a instância singleton de [HapticFeedbackHelper] para vibração tátil.
     */
    @Provides
    @Singleton
    fun provideHapticFeedbackHelper(
        @ApplicationContext context: Context
    ): HapticFeedbackHelper {
        return DefaultHapticFeedbackHelper(context)
    }

    /**
     * Provê a instância singleton de [DeviceSecurityHelper] com detecção de root desabilitada por padrão.
     */
    @Provides
    @Singleton
    fun provideDeviceSecurityHelper(
        @ApplicationContext context: Context
    ): DeviceSecurityHelper {
        return DefaultDeviceSecurityHelper(context, isRootDetectionEnabled = false)
    }

    /**
     * Provê a instância singleton de [PermissionManager] para checagem e abertura de configurações de permissões.
     */
    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): PermissionManager {
        return DefaultPermissionManager(context)
    }
}
