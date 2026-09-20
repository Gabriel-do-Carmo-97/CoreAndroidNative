package br.com.wgc.core.featureflag

/**
 * Contrato base para identificação de uma bandeira de funcionalidade (Feature Toggle / Feature Flag).
 */
interface FeatureToggle {
    /**
     * Chave única que identifica a flag no provedor remoto ou local.
     */
    val key: String

    /**
     * Valor padrão caso o provedor não possua a chave ou ocorra falha de comunicação.
     */
    val defaultValue: Boolean
}

/**
 * Exemplos de Feature Toggles padrão disponibilizados pela infraestrutura base.
 */
enum class DefaultFeatureToggle(
    override val key: String,
    override val defaultValue: Boolean,
) : FeatureToggle {
    DISTRIBUTED_TRACING("core_feature_distributed_tracing", defaultValue = true),
    NETWORK_METRICS("core_feature_network_metrics", defaultValue = true),
    SAMPLE_NEW_EXPERIENCE("sample_new_experience", defaultValue = false),
}
