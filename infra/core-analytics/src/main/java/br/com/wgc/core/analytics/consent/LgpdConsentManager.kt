package br.com.wgc.core.analytics.consent

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Categorias de consentimento do usuário em conformidade com a LGPD (Lei Geral de Proteção de Dados)
 * e o GDPR (General Data Protection Regulation).
 */
enum class ConsentType {
    /** Coleta de dados estatísticos de uso e jornada do usuário. */
    ANALYTICS,

    /** Envio automático de relatórios de falhas, exceções não tratadas e ANRs. */
    CRASH_REPORTING,

    /** Coleta de dados para campanhas publicitárias e atribuição de anúncios. */
    MARKETING,

    /** Customização de conteúdos, recomendações e preferências personalizadas. */
    PERSONALIZATION,
}

/**
 * Gerenciador centralizado de consentimento do usuário para rastreamento e telemetria.
 *
 * Garante que eventos de telemetria, relatórios de falhas ou identificadores de marketing
 * somente sejam coletados e despachados se o usuário tiver concedido permissão explícita
 * para o respectivo [ConsentType].
 *
 * ### Exemplo de Uso:
 * ```kotlin
 * // Quando o usuário aceita cookies/analytics no banner de privacidade:
 * consentManager.setConsent(ConsentType.ANALYTICS, true)
 *
 * // Antes de despachar um evento de telemetria:
 * if (consentManager.isConsentGranted(ConsentType.ANALYTICS)) {
 *     analyticsTracker.track("user_purchase", params)
 * }
 * ```
 *
 * @property storage Instância de [KeyValueStorage] utilizada para persistir as escolhas do usuário.
 */
@Singleton
class LgpdConsentManager
    @Inject
    constructor(
        private val storage: KeyValueStorage,
    ) {
        /**
         * Define ou altera o consentimento do usuário para uma categoria específica.
         *
         * @param type A categoria de consentimento ([ConsentType]).
         * @param granted `true` se o usuário concedeu permissão, `false` caso contrário.
         */
        fun setConsent(
            type: ConsentType,
            granted: Boolean,
        ) {
            storage.saveBoolean("consent_${type.name}", granted)
        }

        /**
         * Verifica se o consentimento para a categoria informada foi concedido pelo usuário.
         *
         * Por padrão de privacidade segura (Privacy by Default), retorna `false` caso o usuário
         * ainda não tenha feito uma escolha expressa.
         *
         * @param type A categoria de consentimento ([ConsentType]).
         * @return `true` se o consentimento estiver ativo; `false` se negado ou não definido.
         */
        fun isConsentGranted(type: ConsentType): Boolean = storage.getBoolean("consent_${type.name}", false)

        /**
         * Revoga imediatamente todos os consentimentos concedidos anteriormente (Opt-out total).
         */
        fun revokeAllConsents() {
            ConsentType.entries.forEach { type ->
                setConsent(type, false)
            }
        }
    }
