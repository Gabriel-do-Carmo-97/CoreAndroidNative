package br.com.wgc.core.database.schema

import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Auxiliar corporativo para verificação de schemas e testes de integridade estrutural SQLite/Room.
 */
object SchemaVerificationHelper {
    data class ColumnInfo(
        val name: String,
        val type: String,
        val notNull: Boolean,
        val isPrimaryKey: Boolean,
    )

    /**
     * Extrai todas as colunas de uma tabela via PRAGMA table_info.
     */
    fun getTableColumns(
        db: SupportSQLiteDatabase,
        tableName: String,
    ): List<ColumnInfo> {
        val columns = mutableListOf<ColumnInfo>()
        val cursor: Cursor = db.query("PRAGMA table_info(`$tableName`);")
        cursor.use {
            val nameIndex = it.getColumnIndex("name")
            val typeIndex = it.getColumnIndex("type")
            val notNullIndex = it.getColumnIndex("notnull")
            val pkIndex = it.getColumnIndex("pk")

            while (it.moveToNext()) {
                columns.add(
                    ColumnInfo(
                        name = it.getString(nameIndex),
                        type = it.getString(typeIndex),
                        notNull = it.getInt(notNullIndex) == 1,
                        isPrimaryKey = it.getInt(pkIndex) > 0,
                    ),
                )
            }
        }
        return columns
    }

    /**
     * Valida se uma tabela contém todas as colunas esperadas.
     */
    fun assertTableContainsColumns(
        db: SupportSQLiteDatabase,
        tableName: String,
        expectedColumnNames: List<String>,
    ): Boolean {
        val existing = getTableColumns(db, tableName).map { it.name.lowercase() }.toSet()
        return expectedColumnNames.all { existing.contains(it.lowercase()) }
    }
}
