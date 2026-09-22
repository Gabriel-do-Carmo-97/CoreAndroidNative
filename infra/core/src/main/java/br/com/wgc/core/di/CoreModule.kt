package br.com.wgc.core.di

import android.content.Context
import br.com.wgc.core.analytics.AnalyticsTracker
import br.com.wgc.core.analytics.CompositeAnalyticsTracker
import br.com.wgc.core.analytics.consent.LgpdConsentManager
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.coroutines.DefaultCoroutineDispatchers
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.dataStorePreferences.KeyValueDataStore
import br.com.wgc.core.database.security.DatabaseEncrypter
import br.com.wgc.core.device.DefaultDeviceInfo
import br.com.wgc.core.device.DefaultDeviceSecurityHelper
import br.com.wgc.core.device.DefaultHapticFeedbackHelper
import br.com.wgc.core.device.DefaultPermissionManager
import br.com.wgc.core.device.DeviceInfo
import br.com.wgc.core.device.DeviceSecurityHelper
import br.com.wgc.core.device.HapticFeedbackHelper
import br.com.wgc.core.device.PermissionManager
import br.com.wgc.core.device.hardware.BleScannerHelper
import br.com.wgc.core.device.hardware.NfcHelper
import br.com.wgc.core.device.notification.NotificationManagerHelper
import br.com.wgc.core.device.security.SecureClipboardManager
import br.com.wgc.core.device.security.SecurityIntegrityHelper
import br.com.wgc.core.featureflag.FeatureToggleManager
import br.com.wgc.core.file.DefaultFileManager
import br.com.wgc.core.file.FileManager
import br.com.wgc.core.location.DefaultLocationClient
import br.com.wgc.core.location.LocationClient
import br.com.wgc.core.logging.CoreLogger
import br.com.wgc.core.logging.DefaultCoreLogger
import br.com.wgc.core.network.NetworkClientFactory
import br.com.wgc.core.network.NetworkMonitor
import br.com.wgc.core.network.security.DynamicCertificatePinner
import br.com.wgc.core.network.websocket.CoreWebSocketClient
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.com.wgc.core.security.biometric.BiometricCryptoHelper
import br.com.wgc.core.security.biometric.DefaultBiometricAuthHelper
import br.com.wgc.core.session.DefaultSessionManager
import br.com.wgc.core.session.SessionManager
import br.com.wgc.core.sharedPreferences.KeyValueStorage
import br.com.wgc.core.sharedPreferences.SharedPreferencesCore
import br.com.wgc.core.sync.DefaultOutboxQueue
import br.com.wgc.core.sync.OutboxQueue
import br.com.wgc.core.sync.SyncManager
import br.com.wgc.core.ui.media.DefaultImageCompressor
import br.com.wgc.core.ui.media.ImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo Hilt que provê as instâncias Singleton dos utilitários de infraestrutura do CoreAndroidNative.
 *
 * Suporta injeção direta de contratos de interface ([KeyValueDataStore], [KeyValueStorage])
 * e de classes concretas, permitindo que aplicações consumidoras customizem os nomes de arquivo
 * de persistência substituindo os providers qualificados com [Named].
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("TooManyFunctions")
object CoreModule {
    /** Qualifier [Named] para o nome do arquivo Preferences DataStore em disco. */
    const val DATASTORE_NAME_QUALIFIER = "DataStoreName"

    /** Qualifier [Named] para o nome do arquivo SharedPreferences em disco. */
    const val SHARED_PREFERENCES_NAME_QUALIFIER = "SharedPreferencesName"

    /** Qualifier [Named] para a implementação padrão de [KeyValueStorage] (não criptografada). */
    const val STORAGE_DEFAULT_QUALIFIER = "DefaultStorage"

    /** Qualifier [Named] para a implementação segura de [KeyValueStorage] (criptografada via KeyStore). */
    const val STORAGE_ENCRYPTED_QUALIFIER = "EncryptedStorage"

    /** Nome padrão do arquivo DataStore em disco. */
    const val DEFAULT_DATASTORE_NAME = DataStorePreferencesCore.DEFAULT_DATASTORE_NAME

    /** Nome padrão do arquivo SharedPreferences em disco. */
    const val DEFAULT_SHARED_PREFERENCES_NAME = SharedPreferencesCore.DEFAULT_SHARED_PREFERENCES_NAME

    /**
     * Provê o nome do arquivo do Preferences DataStore.
     */
    @Provides
    @Named(DATASTORE_NAME_QUALIFIER)
    fun provideDataStoreName(): String = DEFAULT_DATASTORE_NAME

    /**
     * Provê o nome do arquivo de preferências padrão.
     */
    @Provides
    @Named(SHARED_PREFERENCES_NAME_QUALIFIER)
    fun provideSharedPreferencesName(): String = DEFAULT_SHARED_PREFERENCES_NAME

    /**
     * Provê a instância singleton de [DataStorePreferencesCore].
     */
    @Provides
    @Singleton
    fun provideDataStorePreferencesCore(
        @ApplicationContext context: Context,
        @Named(DATASTORE_NAME_QUALIFIER) dataStoreName: String,
    ): DataStorePreferencesCore = DataStorePreferencesCore(context, dataStoreName)

    /**
     * Provê o binding para a abstração [KeyValueDataStore].
     */
    @Provides
    @Singleton
    fun provideKeyValueDataStore(dataStorePreferencesCore: DataStorePreferencesCore): KeyValueDataStore =
        dataStorePreferencesCore

    /**
     * Provê a instância singleton de [SharedPreferencesCore].
     */
    @Provides
    @Singleton
    fun provideSharedPreferencesCore(
        @ApplicationContext context: Context,
        @Named(SHARED_PREFERENCES_NAME_QUALIFIER) sharedPreferencesName: String,
    ): SharedPreferencesCore = SharedPreferencesCore(context, sharedPreferencesName)

    /**
     * Provê o binding de [KeyValueStorage] para a implementação de [SharedPreferencesCore].
     */
    @Provides
    @Singleton
    @Named(STORAGE_DEFAULT_QUALIFIER)
    fun provideDefaultKeyValueStorage(sharedPreferencesCore: SharedPreferencesCore): KeyValueStorage =
        sharedPreferencesCore

    /**
     * Provê o binding padrão de [KeyValueStorage] sem qualificador para injeção direta.
     */
    @Provides
    @Singleton
    fun provideKeyValueStorage(sharedPreferencesCore: SharedPreferencesCore): KeyValueStorage = sharedPreferencesCore

    /**
     * Provê a instância singleton de [EncryptedSharedPreferencesCore].
     */
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferencesCore(
        @ApplicationContext context: Context,
    ): EncryptedSharedPreferencesCore = EncryptedSharedPreferencesCore(context)

    /**
     * Provê o binding de [KeyValueStorage] para a implementação de [EncryptedSharedPreferencesCore].
     */
    @Provides
    @Singleton
    @Named(STORAGE_ENCRYPTED_QUALIFIER)
    fun provideEncryptedKeyValueStorage(
        encryptedSharedPreferencesCore: EncryptedSharedPreferencesCore,
    ): KeyValueStorage = encryptedSharedPreferencesCore

    /**
     * Provê a instância singleton do monitor de conectividade de rede [NetworkMonitor].
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
    ): NetworkMonitor = NetworkMonitor(context)

    /**
     * Provê a instância singleton de [CoroutineDispatchers] padrão.
     */
    @Provides
    @Singleton
    fun provideCoroutineDispatchers(): CoroutineDispatchers = DefaultCoroutineDispatchers()

    /**
     * Provê a instância singleton de [CoreLogger] com mascaramento de PII ativo.
     */
    @Provides
    @Singleton
    fun provideCoreLogger(): CoreLogger = DefaultCoreLogger()

    /**
     * Provê a instância singleton de [DeviceInfo] baseada no contexto da aplicação.
     */
    @Provides
    @Singleton
    fun provideDeviceInfo(
        @ApplicationContext context: Context,
    ): DeviceInfo = DefaultDeviceInfo(context)

    /**
     * Provê a instância singleton de [HapticFeedbackHelper] para vibração tátil.
     */
    @Provides
    @Singleton
    fun provideHapticFeedbackHelper(
        @ApplicationContext context: Context,
    ): HapticFeedbackHelper = DefaultHapticFeedbackHelper(context)

    /**
     * Provê a instância singleton de [FileManager] para manipulação de arquivos e cache.
     */
    @Provides
    @Singleton
    fun provideFileManager(
        @ApplicationContext context: Context,
    ): FileManager = DefaultFileManager(context)

    /**
     * Provê a instância singleton de [SessionManager] orquestrando a limpeza atômica da sessão.
     */
    @Provides
    @Singleton
    fun provideSessionManager(
        keyValueDataStore: KeyValueDataStore,
        @Named(STORAGE_DEFAULT_QUALIFIER) defaultStorage: KeyValueStorage,
        @Named(STORAGE_ENCRYPTED_QUALIFIER) encryptedStorage: KeyValueStorage,
        fileManager: FileManager,
        dispatchers: CoroutineDispatchers,
    ): SessionManager =
        DefaultSessionManager(
            keyValueDataStore = keyValueDataStore,
            defaultStorage = defaultStorage,
            encryptedStorage = encryptedStorage,
            fileManager = fileManager,
            dispatchers = dispatchers,
        )

    /**
     * Provê a instância singleton de [BiometricAuthHelper] para autenticação biométrica.
     */
    @Provides
    @Singleton
    fun provideBiometricAuthHelper(
        @ApplicationContext context: Context,
    ): BiometricAuthHelper = DefaultBiometricAuthHelper(context)

    /**
     * Provê a instância singleton de [DeviceSecurityHelper] com detecção de root ativa por padrão.
     */
    @Provides
    @Singleton
    fun provideDeviceSecurityHelper(
        @ApplicationContext context: Context,
    ): DeviceSecurityHelper = DefaultDeviceSecurityHelper(context, isRootDetectionEnabled = true)

    /**
     * Provê a instância singleton de [PermissionManager] para checagem e abertura de configurações de permissões.
     */
    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context,
    ): PermissionManager = DefaultPermissionManager(context)

    /**
     * Provê a instância singleton de [AnalyticsTracker] com sanitização de PII ativa.
     */
    @Provides
    @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker =
        CompositeAnalyticsTracker(trackers = emptyList(), enablePiiMasking = true)

    /**
     * Provê a instância singleton de [ImageCompressor] para processamento assíncrono de imagens.
     */
    @Provides
    @Singleton
    fun provideImageCompressor(dispatchers: CoroutineDispatchers): ImageCompressor = DefaultImageCompressor(dispatchers)

    /**
     * Provê a instância singleton de [LgpdConsentManager] para gestão de consentimentos de privacidade.
     */
    @Provides
    @Singleton
    fun provideLgpdConsentManager(
        @Named(STORAGE_DEFAULT_QUALIFIER) storage: KeyValueStorage,
    ): LgpdConsentManager = LgpdConsentManager(storage)

    /**
     * Provê a instância singleton de [LocationClient] para atualizações de localização geográfica.
     */
    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context,
    ): LocationClient = DefaultLocationClient(context)

    /**
     * Provê a instância singleton de [DatabaseEncrypter] para criptografia transparente Room/SQLCipher.
     */
    @Provides
    @Singleton
    fun provideDatabaseEncrypter(encryptedStorage: EncryptedSharedPreferencesCore): DatabaseEncrypter {
        return DatabaseEncrypter(encryptedStorage)
    }

    /**
     * Provê uma instância padrão e segura de [OkHttpClient] com timeouts corporativos.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = NetworkClientFactory.createOkHttpClient()

    /**
     * Provê o gerenciador corporativo de [FeatureToggleManager].
     */
    @Provides
    @Singleton
    fun provideFeatureToggleManager(): FeatureToggleManager = FeatureToggleManager()

    /**
     * Provê a fila persistente em memória [OutboxQueue] para o padrão Outbox.
     */
    @Provides
    @Singleton
    fun provideOutboxQueue(): OutboxQueue = DefaultOutboxQueue()

    /**
     * Provê o orquestrador de sincronização em segundo plano [SyncManager].
     */
    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        outboxQueue: OutboxQueue,
    ): SyncManager = SyncManager(context, outboxQueue)

    /**
     * Provê o gerenciador corporativo de canais e notificações [NotificationManagerHelper].
     */
    @Provides
    @Singleton
    fun provideNotificationManagerHelper(
        @ApplicationContext context: Context,
    ): NotificationManagerHelper = NotificationManagerHelper(context)

    /**
     * Provê o utilitário corporativo de leitura de tags NFC [NfcHelper].
     */
    @Provides
    @Singleton
    fun provideNfcHelper(
        @ApplicationContext context: Context,
    ): NfcHelper = NfcHelper(context)

    /**
     * Provê o scanner reativo de periféricos Bluetooth Low Energy [BleScannerHelper].
     */
    @Provides
    @Singleton
    fun provideBleScannerHelper(
        @ApplicationContext context: Context,
    ): BleScannerHelper = BleScannerHelper(context)

    /**
     * Provê o cliente corporativo reativo de WebSocket [CoreWebSocketClient].
     */
    @Provides
    @Singleton
    fun provideCoreWebSocketClient(okHttpClient: OkHttpClient): CoreWebSocketClient = CoreWebSocketClient(okHttpClient)

    /**
     * Provê o utilitário corporativo de criptografia biométrica [BiometricCryptoHelper].
     */
    @Provides
    @Singleton
    fun provideBiometricCryptoHelper(): BiometricCryptoHelper = BiometricCryptoHelper()

    /**
     * Provê o utilitário de auditoria e anti-tampering [SecurityIntegrityHelper].
     */
    @Provides
    @Singleton
    fun provideSecurityIntegrityHelper(): SecurityIntegrityHelper = SecurityIntegrityHelper()

    /**
     * Provê o gerenciador de área de transferência confidencial [SecureClipboardManager].
     */
    @Provides
    @Singleton
    fun provideSecureClipboardManager(
        @ApplicationContext context: Context,
    ): SecureClipboardManager = SecureClipboardManager(context)

    /**
     * Provê o gerenciador dinâmico de Certificate Pinning [DynamicCertificatePinner].
     */
    @Provides
    @Singleton
    fun provideDynamicCertificatePinner(): DynamicCertificatePinner = DynamicCertificatePinner()
}
