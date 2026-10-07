package br.com.wgc.core.di

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class CoreServiceLocatorTest {
    interface GreetingService {
        fun greet(): String
    }

    class SimpleGreetingService(
        val id: Int,
    ) : GreetingService {
        override fun greet(): String = "Hello $id"
    }

    @After
    fun tearDown() {
        CoreServiceLocator.clear()
    }

    @Test
    fun should_register_and_resolve_singleton() {
        val instance = SimpleGreetingService(1)
        CoreServiceLocator.registerSingleton<GreetingService>(instance)

        val resolved1 = CoreServiceLocator.resolve<GreetingService>()
        val resolved2 = CoreServiceLocator.resolve<GreetingService>()

        assertSame(instance, resolved1)
        assertSame(resolved1, resolved2)
    }

    @Test
    fun should_register_and_resolve_factory() {
        var count = 0
        CoreServiceLocator.registerFactory<GreetingService> {
            SimpleGreetingService(++count)
        }

        val resolved1 = CoreServiceLocator.resolve<GreetingService>()
        val resolved2 = CoreServiceLocator.resolve<GreetingService>()

        assertNotSame(resolved1, resolved2)
        assertEquals("Hello 1", resolved1.greet())
        assertEquals("Hello 2", resolved2.greet())
    }
}
