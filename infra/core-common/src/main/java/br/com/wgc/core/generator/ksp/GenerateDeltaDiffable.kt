package br.com.wgc.core.generator.ksp

/**
 * Annotation marking domain models for automatic deep-copy and delta diff code generation via KSP.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateDeltaDiffable
