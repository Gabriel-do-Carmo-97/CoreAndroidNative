package br.com.wgc.core.storage.di

import android.content.Context
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.dataStorePreferences.KeyValueDataStore
import br.com.wgc.core.file.DefaultFileManager
import br.com.wgc.core.file.FileManager
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.com.wgc.core.security.biometric.DefaultBiometricAuthHelper
import br.com.wgc.core.session.DefaultSessionManager
import br.com.wgc.core.session.SessionManager
import br.com.wgc.core.sharedPreferences.KeyValueStorage
import br.com.wgc.core.sharedPreferences.SharedPreferencesCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo Hilt específico para provimento de dependências de persistência, criptografia e biometria.
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

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
        @Named(DATASTORE_NAME_QUALIFIER) dataStoreName: String
    ): DataStorePreferencesCore {
        return DataStorePreferencesCore(context, dataStoreName)
    }

    /**
     * Provê o binding para a abstração [KeyValueDataStore].
     */
    @Provides
    @Singleton
    fun provideKeyValueDataStore(
        dataStorePreferencesCore: DataStorePreferencesCore
    ): KeyValueDataStore {
        return dataStorePreferencesCore
    }

    /**
     * Provê a instância singleton de [SharedPreferencesCore].
     */
    @Provides
    @Singleton
    fun provideSharedPreferencesCore(
        @ApplicationContext context: Context,
        @Named(SHARED_PREFERENCES_NAME_QUALIFIER) sharedPreferencesName: String
    ): SharedPreferencesCore {
        return SharedPreferencesCore(context, sharedPreferencesName)
    }

    /**
     * Provê o binding de [KeyValueStorage] para a implementação de [SharedPreferencesCore].
     */
    @Provides
    @Singleton
    @Named(STORAGE_DEFAULT_QUALIFIER)
    fun provideDefaultKeyValueStorage(
        sharedPreferencesCore: SharedPreferencesCore
    ): KeyValueStorage {
        return sharedPreferencesCore
    }

    /**
     * Provê a instância singleton de [EncryptedSharedPreferencesCore].
     */
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferencesCore(
        @ApplicationContext context: Context
    ): EncryptedSharedPreferencesCore {
        return EncryptedSharedPreferencesCore(context)
    }

    /**
     * Provê o binding de [KeyValueStorage] para a implementação de [EncryptedSharedPreferencesCore].
     */
    @Provides
    @Singleton
    @Named(STORAGE_ENCRYPTED_QUALIFIER)
    fun provideEncryptedKeyValueStorage(
        encryptedSharedPreferencesCore: EncryptedSharedPreferencesCore
    ): KeyValueStorage {
        return encryptedSharedPreferencesCore
    }

    /**
     * Provê a instância singleton de [FileManager] para manipulação de arquivos e cache.
     */
    @Provides
    @Singleton
    fun provideFileManager(
        @ApplicationContext context: Context
    ): FileManager {
        return DefaultFileManager(context)
    }

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
        dispatchers: CoroutineDispatchers
    ): SessionManager {
        return DefaultSessionManager(
            keyValueDataStore = keyValueDataStore,
            defaultStorage = defaultStorage,
            encryptedStorage = encryptedStorage,
            fileManager = fileManager,
            dispatchers = dispatchers
        )
    }

    /**
     * Provê a instância singleton de [BiometricAuthHelper] para autenticação biométrica.
     */
    @Provides
    @Singleton
    fun provideBiometricAuthHelper(
        @ApplicationContext context: Context
    ): BiometricAuthHelper {
        return DefaultBiometricAuthHelper(context)
    }
}
