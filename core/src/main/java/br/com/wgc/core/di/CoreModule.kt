package br.com.wgc.core.di

import android.content.Context
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.sharedPreferences.SharedPreferencesCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
data object CoreModule {
    const val DATASTORE_NAME_QUALIFIER = "DataStoreName"
    const val SHARED_PREFERENCES_NAME_QUALIFIER = "SharedPreferencesName"

    @Provides
    @Singleton
    fun providerDataStore(
        @ApplicationContext context: Context,
        @Named(DATASTORE_NAME_QUALIFIER) dataStoreName: String
    ): DataStorePreferencesCore {
        return DataStorePreferencesCore(
            context = context,
            dataStoreName = dataStoreName
        )
    }

    @Singleton
    @Provides
    fun providerSharedPreferences(
        @ApplicationContext context: Context,
        @Named(SHARED_PREFERENCES_NAME_QUALIFIER) sharedPreferencesName: String
    ): SharedPreferencesCore {
        return SharedPreferencesCore(
            context = context,
            sharedPreferencesName = sharedPreferencesName
        )
    }

}