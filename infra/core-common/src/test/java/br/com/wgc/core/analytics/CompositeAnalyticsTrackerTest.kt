package br.com.wgc.core.analytics

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class CompositeAnalyticsTrackerTest {
    @Test
    fun `logEvent should fan out to all registered trackers with masked PII`() {
        val tracker1 = mockk<AnalyticsTracker>(relaxed = true)
        val tracker2 = mockk<AnalyticsTracker>(relaxed = true)
        val composite = CompositeAnalyticsTracker(listOf(tracker1, tracker2), enablePiiMasking = true)

        val params =
            mapOf(
                "cpf" to "123.456.789-00",
                "card" to "4111 2222 3333 4444",
                "auth" to "Bearer abcdef123456",
                "email" to "user@example.com",
                "item_count" to 5,
            )

        composite.logEvent("purchase_attempt", params)

        val expectedParams =
            mapOf(
                "cpf" to "***.***.***-**",
                "card" to "****-****-****-****",
                "auth" to "Bearer [MASKED_TOKEN]",
                "email" to "***@***.***",
                "item_count" to 5,
            )

        verify(exactly = 1) { tracker1.logEvent("purchase_attempt", expectedParams) }
        verify(exactly = 1) { tracker2.logEvent("purchase_attempt", expectedParams) }
    }

    @Test
    fun `setUserProperty should mask PII when enabled`() {
        val tracker = mockk<AnalyticsTracker>(relaxed = true)
        val composite = CompositeAnalyticsTracker(listOf(tracker), enablePiiMasking = true)

        composite.setUserProperty("document", "12345678901")
        verify(exactly = 1) { tracker.setUserProperty("document", "***.***.***-**") }
    }

    @Test
    fun `setUserId should propagate identifier to all trackers`() {
        val tracker1 = mockk<AnalyticsTracker>(relaxed = true)
        val tracker2 = mockk<AnalyticsTracker>(relaxed = true)
        val composite = CompositeAnalyticsTracker(listOf(tracker1, tracker2))

        composite.setUserId("user_98765")
        verify(exactly = 1) { tracker1.setUserId("user_98765") }
        verify(exactly = 1) { tracker2.setUserId("user_98765") }
    }

    @Test
    fun `maskPii should return original text when masking is disabled`() {
        val composite = CompositeAnalyticsTracker(emptyList(), enablePiiMasking = false)
        val raw = "user@test.com"
        assertEquals(raw, composite.maskPii(raw))
    }
}
