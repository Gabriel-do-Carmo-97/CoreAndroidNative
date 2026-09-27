package br.com.wgc.core.database.fts

import org.junit.Assert.assertEquals
import org.junit.Test

class FtsSearchHelperTest {
    @Test
    fun `sanitizeFtsQuery formats terms with prefix wildcards`() {
        val query = "gabriel carmo"
        val formatted = FtsSearchHelper.sanitizeFtsQuery(query)
        assertEquals("gabriel* carmo*", formatted)
    }

    @Test
    fun `sanitizeFtsQuery strips special punctuation and symbols`() {
        val query = "item-123 & (tag:*)"
        val formatted = FtsSearchHelper.sanitizeFtsQuery(query)
        assertEquals("item* 123* tag*", formatted)
    }

    @Test
    fun `sanitizeFtsQuery returns empty for blank or symbols only`() {
        assertEquals("", FtsSearchHelper.sanitizeFtsQuery("   "))
        assertEquals("", FtsSearchHelper.sanitizeFtsQuery("!@#$%^&*()"))
    }
}
