package br.com.wgc.core.di

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Service Locator leve, thread-safe e puro para injeção e resolução de dependências
 * em módulos de domínio e código agnóstico de plataforma (commonMain).
 */
object CoreServiceLocator {
    private val singletons = ConcurrentHashMap<KClass<*>, Any>()
    private val factories = ConcurrentHashMap<KClass<*>, () -> Any>()

    fun <T : Any> registerSingleton(
        kClass: KClass<T>,
        instance: T,
    ) {
        singletons[kClass] = instance
    }

    fun <T : Any> registerFactory(
        kClass: KClass<T>,
        factory: () -> T,
    ) {
        factories[kClass] = factory
    }

    inline fun <reified T : Any> registerSingleton(instance: T) {
        registerSingleton(T::class, instance)
    }

    inline fun <reified T : Any> registerFactory(noinline factory: () -> T) {
        registerFactory(T::class, factory)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(kClass: KClass<T>): T {
        val singleton = singletons[kClass]
        if (singleton != null) return singleton as T

        val factory = factories[kClass]
        if (factory != null) return factory() as T

        error("No dependency registered for class: ${kClass.qualifiedName}")
    }

    inline fun <reified T : Any> resolve(): T {
        return resolve(T::class)
    }

    fun clear() {
        singletons.clear()
        factories.clear()
    }
}
