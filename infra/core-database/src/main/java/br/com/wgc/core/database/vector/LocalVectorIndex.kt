package br.com.wgc.core.database.vector

import kotlin.math.sqrt

/**
 * An embedding vector record for on-device similarity search.
 *
 * @property id Identifier of the vector item.
 * @property embedding The high-dimensional float array.
 * @property metadata Arbitrary metadata payload.
 */
data class VectorRecord(
    val id: String,
    val embedding: FloatArray,
    val metadata: Map<String, String> = emptyMap(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VectorRecord
        return id == other.id &&
            embedding.contentEquals(other.embedding) &&
            metadata == other.metadata
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + embedding.contentHashCode()
        result = 31 * result + metadata.hashCode()
        return result
    }
}

/**
 * Result match from vector nearest neighbor search.
 */
data class VectorMatch(
    val id: String,
    val score: Float,
    val metadata: Map<String, String> = emptyMap(),
)

/**
 * In-memory / SQLite-vec contract for embedded semantic search and RAG.
 */
class LocalVectorIndex(
    val dimension: Int,
) {
    private val records = mutableListOf<VectorRecord>()

    /**
     * Inserts or replaces a vector embedding.
     */
    fun insert(record: VectorRecord) {
        require(record.embedding.size == dimension) {
            "Embedding dimension mismatch: expected $dimension, got ${record.embedding.size}"
        }
        records.removeAll { it.id == record.id }
        records.add(record)
    }

    /**
     * Searches the top [limit] vectors most similar to [queryEmbedding] using cosine similarity.
     */
    fun search(
        queryEmbedding: FloatArray,
        limit: Int = DEFAULT_SEARCH_LIMIT,
    ): List<VectorMatch> {
        require(queryEmbedding.size == dimension) {
            "Query dimension mismatch: expected $dimension, got ${queryEmbedding.size}"
        }

        return records
            .map { record ->
                val similarity = cosineSimilarity(queryEmbedding, record.embedding)
                VectorMatch(record.id, similarity, record.metadata)
            }.sortedByDescending { it.score }
            .take(limit)
    }

    fun size(): Int = records.size

    fun clear() {
        records.clear()
    }

    companion object {
        const val DEFAULT_SEARCH_LIMIT = 5

        /**
         * Computes cosine similarity between two float vectors.
         */
        fun cosineSimilarity(
            v1: FloatArray,
            v2: FloatArray,
        ): Float {
            var dot = 0.0f
            var norm1 = 0.0f
            var norm2 = 0.0f
            for (i in v1.indices) {
                dot += v1[i] * v2[i]
                norm1 += v1[i] * v1[i]
                norm2 += v2[i] * v2[i]
            }
            val denominator = sqrt(norm1) * sqrt(norm2)
            return if (denominator > 0.0f) dot / denominator else 0.0f
        }
    }
}
