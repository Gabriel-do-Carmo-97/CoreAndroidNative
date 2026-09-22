package br.com.wgc.bundle.hardware

import android.content.Context
import br.com.wgc.core.device.DefaultDeviceInfo
import br.com.wgc.core.device.DefaultHapticFeedbackHelper
import br.com.wgc.core.device.DeviceInfo
import br.com.wgc.core.device.HapticFeedbackHelper
import br.com.wgc.core.device.hardware.BleScannerHelper
import br.com.wgc.core.device.hardware.NfcHelper
import br.com.wgc.core.device.notification.NotificationManagerHelper
import br.com.wgc.core.location.DefaultLocationClient
import br.com.wgc.core.location.LocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Inicializador e provedor de dependências Hilt para o Bundle de Hardware e Localização.
 */
@Module
@InstallIn(SingletonComponent::class)
object HardwareInitializer {
    @Provides
    @Singleton
    fun provideHardwareFacade(
        @ApplicationContext context: Context,
    ): HardwareFacade = HardwareFacade(context)
}

/**
 * Ponto de entrada unificado para sensores de hardware, NFC, BLE, vibração e localização GPS.
 *
 * @property context O contexto da aplicação consumidora.
 */
class HardwareFacade(
    private val context: Context,
) {
    /** Metadados do aparelho e sistema operacional. */
    val deviceInfo: DeviceInfo by lazy { DefaultDeviceInfo(context) }

    /** Utilitário de feedback tátil háptico. */
    val hapticFeedbackHelper: HapticFeedbackHelper by lazy { DefaultHapticFeedbackHelper(context) }

    /** Utilitário de leitura e suporte a tags NFC. */
    val nfcHelper: NfcHelper by lazy { NfcHelper(context) }

    /** Scanner reativo de periféricos Bluetooth Low Energy. */
    val bleScannerHelper: BleScannerHelper by lazy { BleScannerHelper(context) }

    /** Central de canais e construção de notificações corporativas. */
    val notificationManagerHelper: NotificationManagerHelper by lazy { NotificationManagerHelper(context) }

    /** Cliente reativo de geolocalização por FusedLocation. */
    val locationClient: LocationClient by lazy { DefaultLocationClient(context) }

    /**
     * Retorna o status de inicialização do bundle de hardware.
     */
    fun getStatus(): String = "Hardware Bundle initialized successfully for package ${context.packageName}"
}
