package br.com.wgc.core.serialization

/**
 * Envelope para dados serializados com versão de schema embutida.
 * Permite que clientes e servidores evoluam payloads sem quebrar compatibilidade reversa.
 */
data class VersionedPayload(
    val schemaVersion: Int,
    val payload: String,
    val timestampMs: Long = System.currentTimeMillis(),
)

/**
 * Contrato para migração e desserialização de schemas versionados.
 */
interface SchemaMigrator<T> {
    fun migrate(
        fromVersion: Int,
        targetVersion: Int,
        rawPayload: String,
    ): String

    fun deserialize(currentVersionPayload: String): T
}

/**
 * Utilitário de coordenação de migração de esquemas versionados.
 */
class SchemaVersionedSerializer<T>(
    private val currentVersion: Int,
    private val migrator: SchemaMigrator<T>,
) {
    fun parse(versionedPayload: VersionedPayload): T {
        val normalizedPayload =
            if (versionedPayload.schemaVersion < currentVersion) {
                migrator.migrate(
                    fromVersion = versionedPayload.schemaVersion,
                    targetVersion = currentVersion,
                    rawPayload = versionedPayload.payload,
                )
            } else {
                versionedPayload.payload
            }
        return migrator.deserialize(normalizedPayload)
    }

    fun wrap(payload: String): VersionedPayload {
        return VersionedPayload(
            schemaVersion = currentVersion,
            payload = payload,
        )
    }
}
