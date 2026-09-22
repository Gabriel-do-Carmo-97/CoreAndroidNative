package br.com.wgc.core.analytics.breadcrumbs

import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Níveis de severidade para rastreamento de breadcrumbs de diagnóstico.
 */
enum class BreadcrumbLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
}

/**
 * Registro individual de evento/ação executada pelo usuário ou sistema antes de falhas ou transições.
 *
 * @property message Mensagem descritiva do evento.
 * @property category Categoria lógica (ex: "navigation", "ui_click", "network", "auth").
 * @property level Gravidade do evento ([BreadcrumbLevel]).
 * @property timestamp Horário em milissegundos em que o evento ocorreu.
 * @property data Metadados adicionais chave-valor associados ao evento.
 */
data class Breadcrumb(
    val message: String,
    val category: String = "general",
    val level: BreadcrumbLevel = BreadcrumbLevel.INFO,
    val timestamp: Long = System.currentTimeMillis(),
    val data: Map<String, String> = emptyMap(),
)

/**
 * Gerenciador thread-safe de Breadcrumbs em ring-buffer com capacidade limitada.
 *
 * Mantém em memória os últimos $N$ passos do usuário (telas visitadas, botões clicados, requisições de rede)
 * para anexar automaticamente a relatórios de erros, exceções não tratadas e diagnósticos.
 *
 * @param maxCapacity Número máximo de registros mantidos no buffer antes do descarte dos mais antigos (default: 50).
 */
@Singleton
class BreadcrumbManager(
    private val maxCapacity: Int = DEFAULT_CAPACITY,
) {
    @Inject
    constructor() : this(DEFAULT_CAPACITY)

    private val lock = Any()
    private val buffer = ArrayDeque<Breadcrumb>(maxCapacity)

    /**
     * Adiciona um novo breadcrumb ao buffer circular.
     * Se a capacidade máxima for atingida, o registro mais antigo é removido.
     */
    fun addBreadcrumb(breadcrumb: Breadcrumb) {
        synchronized(lock) {
            if (buffer.size >= maxCapacity) {
                buffer.pollFirst()
            }
            buffer.addLast(breadcrumb)
        }
    }

    /**
     * Adiciona um novo breadcrumb ao buffer de forma simplificada.
     */
    fun addBreadcrumb(
        message: String,
        category: String = "general",
        level: BreadcrumbLevel = BreadcrumbLevel.INFO,
        data: Map<String, String> = emptyMap(),
    ) {
        addBreadcrumb(
            Breadcrumb(
                message = message,
                category = category,
                level = level,
                timestamp = System.currentTimeMillis(),
                data = data,
            ),
        )
    }

    /**
     * Retorna uma cópia imutável de todos os breadcrumbs armazenados, ordenados cronologicamente.
     */
    fun getBreadcrumbs(): List<Breadcrumb> =
        synchronized(lock) {
            buffer.toList()
        }

    /**
     * Retorna a quantidade atual de breadcrumbs armazenados.
     */
    fun size(): Int =
        synchronized(lock) {
            buffer.size
        }

    /**
     * Limpa todos os breadcrumbs armazenados no buffer.
     */
    fun clear() {
        synchronized(lock) {
            buffer.clear()
        }
    }

    companion object {
        const val DEFAULT_CAPACITY: Int = 50
    }
}
