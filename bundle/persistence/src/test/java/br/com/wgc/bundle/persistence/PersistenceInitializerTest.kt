package br.com.wgc.bundle.persistence

import org.junit.Assert.assertNotNull
import org.junit.Test

class PersistenceInitializerTest {

    @Test
    fun testInitializerExists() {
        assertNotNull(PersistenceInitializer)
    }
}
