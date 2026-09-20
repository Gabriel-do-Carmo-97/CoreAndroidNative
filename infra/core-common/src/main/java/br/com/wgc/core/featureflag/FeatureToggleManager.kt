package br.com.wgc.core.featureflag

import java.util.concurrent.ConcurrentHashMap

/**
 * Provedor remoto ou externo de bandeiras de funcionalidade (ex: Firebase Remote Config, LaunchDarkly).
 */
fun interface FeatureToggleProvider {
    /**
     * Retorna o valor configurado para a chave informada, ou `null` se não existir.
     */
    fun getBoolean(key: String): Boolean?
}

/**
 * Gerenciador unificado de Feature Flags.
 *
 * Suporta provedores externos, fallbacks com valores padrão e sobreposições locais (overrides)
 * para testes manuais e menus de debug.
 */
class FeatureToggleManager(
    private val remoteProvider: FeatureToggleProvider? = null,
) {
    private val localOverrides = ConcurrentHashMap<String, Boolean>()

    /**
     * Avalia se uma funcionalidade está ativa seguindo a precedência:
     * 1. Sobrescrita local em memória (útil para QA / testes);
     * 2. Provedor remoto (se disponível);
     * 3. Valor padrão configurado no [FeatureToggle].
     */
    fun isEnabled(toggle: FeatureToggle): Boolean {
        localOverrides[toggle.key]?.let { return it }
        val remoteValue = remoteProvider?.getBoolean(toggle.key)
        return remoteValue ?: toggle.defaultValue
    }

    /**
     * Força o estado de uma flag localmente em memória.
     */
    fun setOverride(
        toggle: FeatureToggle,
        enabled: Boolean,
    ) {
        localOverrides[toggle.key] = enabled
    }

    /**
     * Remove uma sobreposição específica.
     */
    fun clearOverride(toggle: FeatureToggle) {
        localOverrides.remove(toggle.key)
    }

    /**
     * Remove todas as sobreposições locais.
     */
    fun clearAllOverrides() {
        localOverrides.clear()
    }
}
