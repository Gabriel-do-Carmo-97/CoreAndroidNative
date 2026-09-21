package br.com.wgc.core.sync

import java.util.UUID

/**
 * Representa uma operação enfileirada no padrão Outbox para execução resiliente offline.
 *
 * @property id Identificador único da requisição.
 * @property endpoint Rota ou identificador do serviço de destino.
 * @property method Método de operação (ex: POST, PUT, DELETE, PATCH).
 * @property payload Conteúdo serializado da operação.
 * @property headers Cabeçalhos adicionais associados à operação.
 * @property retryCount Quantidade de tentativas de reexecução já realizadas.
 * @property timestamp Epoch timestamp em milissegundos de quando a requisição foi criada.
 */
data class OutboxRequest(
    val id: String = UUID.randomUUID().toString(),
    val endpoint: String,
    val method: String = "POST",
    val payload: String = "",
    val headers: Map<String, String> = emptyMap(),
    val retryCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
)
