package br.com.wgc.core.database.vector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalVectorIndexTest {
    @Test
    fun testVectorCosineSimilarityAndSearch() {
        val index = LocalVectorIndex(dimension = 3)

        val v1 = VectorRecord(id = "doc1", embedding = floatArrayOf(1.0f, 0.0f, 0.0f))
        val v2 = VectorRecord(id = "doc2", embedding = floatArrayOf(0.0f, 1.0f, 0.0f))
        val v3 = VectorRecord(id = "doc3", embedding = floatArrayOf(0.9f, 0.1f, 0.0f))

        index.insert(v1)
        index.insert(v2)
        index.insert(v3)

        assertEquals(3, index.size())

        val query = floatArrayOf(1.0f, 0.0f, 0.0f)
        val results = index.search(query, limit = 2)

        assertEquals(2, results.size)
        assertEquals("doc1", results[0].id)
        assertEquals(1.0f, results[0].score, 0.001f)
        assertEquals("doc3", results[1].id)
        assertTrue(results[1].score > 0.9f)
    }
}
