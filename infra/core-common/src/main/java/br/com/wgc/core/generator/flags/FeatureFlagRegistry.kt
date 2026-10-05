package br.com.wgc.core.generator.flags

/**
 * Enterprise typed feature flag definition.
 */
data class FeatureFlagDefinition<T>(
    val key: String,
    val defaultValue: T,
    val description: String,
)

/**
 * Registry holding strongly-typed feature flag definitions across modules.
 */
class FeatureFlagRegistry {
    private val flags = mutableMapOf<String, FeatureFlagDefinition<*>>()

    fun <T> register(flag: FeatureFlagDefinition<T>) {
        flags[flag.key] = flag
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): FeatureFlagDefinition<T>? {
        return flags[key] as? FeatureFlagDefinition<T>
    }

    fun allKeys(): Set<String> = flags.keys
}
