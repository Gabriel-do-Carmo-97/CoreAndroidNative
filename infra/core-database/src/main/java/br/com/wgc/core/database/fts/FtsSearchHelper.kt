package br.com.wgc.core.database.fts

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * Entidade de busca textual de alta performance indexada via SQLite FTS4.
 */
@Entity(tableName = "core_fts_documents")
@Fts4
data class FtsDocumentEntity(
    @PrimaryKey
    val rowid: Int = 0,
    val documentId: String,
    val title: String,
    val content: String,
    val tags: String = "",
)

/**
 * Utilitário de sanitização e montagem de queries de busca FTS (Full-Text Search).
 * Previne erros de sintaxe no SQLite MATCH operator e constrói consultas com suporte a prefixos (*).
 */
object FtsSearchHelper {
    private val SPECIAL_CHARS_REGEX = Regex("""[^\p{L}\p{Nd}\s]""")

    /**
     * Sanitiza e formata termos de busca para uso seguro em queries MATCH do SQLite FTS.
     * Exemplo: "maria silva" -> "maria* silva*"
     */
    fun sanitizeFtsQuery(rawQuery: String): String {
        val cleaned = SPECIAL_CHARS_REGEX.replace(rawQuery, " ").trim()
        val tokens = cleaned.split(Regex("""\s+""")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return ""

        return tokens.joinToString(" ") { "$it*" }
    }
}
