package br.com.wgc.core.generator.ksp

/**
 * Annotation marking repository interfaces for automatic caching decorator code generation via KSP.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateCachedDecorator(
    val cacheTtlSeconds: Long = 300L,
)
