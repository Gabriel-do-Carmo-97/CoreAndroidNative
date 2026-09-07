package br.com.wgc.core.storage.di

import org.junit.Assert.assertEquals
import org.junit.Test

class StorageModuleTest {

    @Test
    fun `default qualifiers and names should be initialized correctly`() {
        assertEquals("wgc_core_datastore", StorageModule.provideDataStoreName())
        assertEquals("wgc_core_preferences", StorageModule.provideSharedPreferencesName())
    }
}
