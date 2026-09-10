package br.com.wgc.core.database.converters

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

/**
 * Conversores de tipos padronizados para o banco de dados Room.
 *
 * Esta classe fornece mapeamentos bidirecionais entre tipos complexos da JVM (como [Date], [UUID]
 * e [List] de [String]) e representações primitivas suportadas nativamente pelo SQLite (Long, String).
 *
 * ### Exemplo de Uso no App:
 * ```kotlin
 * @Database(entities = [UserEntity::class], version = 1)
 * @TypeConverters(RoomConverters::class)
 * abstract class AppDatabase : RoomDatabase() { ... }
 * ```
 */
class RoomConverters {

    /**
     * Converte um timestamp em milissegundos ([Long]) em um objeto [Date].
     *
     * @param value O valor em milissegundos desde a época Unix (1 de janeiro de 1970).
     * @return O objeto [Date] correspondente, ou `null` caso [value] seja nulo.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converte um objeto [Date] em seu timestamp equivalente em milissegundos ([Long]).
     *
     * @param date O objeto de data a ser serializado.
     * @return O valor em milissegundos, ou `null` caso [date] seja nulo.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Converte uma [String] formatada como UUID em uma instância de [UUID].
     *
     * @param value A representação canônica em texto do UUID (ex: "123e4567-e89b-12d3-a456-426614174000").
     * @return A instância de [UUID], ou `null` caso [value] seja nulo.
     * @throws IllegalArgumentException Se a string não seguir o formato padrão de UUID.
     */
    @TypeConverter
    fun fromUUID(value: String?): UUID? {
        return value?.let { UUID.fromString(it) }
    }

    /**
     * Converte uma instância de [UUID] em sua representação canônica em [String].
     *
     * @param uuid O identificador único universal.
     * @return A string representativa do UUID, ou `null` caso [uuid] seja nulo.
     */
    @TypeConverter
    fun uuidToString(uuid: UUID?): String? {
        return uuid?.toString()
    }

    /**
     * Converte uma lista de strings delimitada por vírgula em uma [List] de [String].
     *
     * @param value A string com valores separados por vírgula.
     * @return A lista de strings extraídas, ou lista vazia se [value] for nulo ou vazio.
     */
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    /**
     * Converte uma [List] de [String] em uma única string unificada delimitada por vírgula.
     *
     * @param list A lista de strings a ser persistida.
     * @return A string delimitada, ou string vazia caso a lista seja nula.
     */
    @TypeConverter
    fun stringListToString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}
