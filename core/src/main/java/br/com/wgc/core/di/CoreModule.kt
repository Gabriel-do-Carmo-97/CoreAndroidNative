package br.com.wgc.core.di

import android.content.Context
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.network.NetworkMonitor
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import br.com.wgc.core.sharedPreferences.SharedPreferencesCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    const val DATASTORE_NAME_QUALIFIER = "DataStoreName"
    const val SHARED_PREFERENCES_NAME_QUALIFIER = "SharedPreferencesName"

    const val DEFAULT_DATASTORE_NAME = "wgc_core_datastore"
    const val DEFAULT_SHARED_PREFERENCES_NAME = "wgc_core_preferences"

    @Provides
    @Singleton
    fun provideDataStorePreferencesCore(
        @ApplicationContext context: Context
    ): DataStorePreferencesCore {
        return DataStorePreferencesCore(context, DEFAULT_DATASTORE_NAME)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesCore(
        @ApplicationContext context: Context
    ): SharedPreferencesCore {
        return SharedPreferencesCore(context, DEFAULT_SHARED_PREFERENCES_NAME)
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferencesCore(
        @ApplicationContext context: Context
    ): EncryptedSharedPreferencesCore {
        return EncryptedSharedPreferencesCore(context)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }
}
