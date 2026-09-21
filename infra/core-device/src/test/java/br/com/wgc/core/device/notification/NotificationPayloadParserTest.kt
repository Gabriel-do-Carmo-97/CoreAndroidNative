package br.com.wgc.core.device.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationPayloadParserTest {
    @Test
    fun `parse should extract title, body and security channel successfully`() {
        val raw =
            mapOf(
                "title" to "Código de Verificação",
                "body" to "Seu código 2FA é 982312",
                "channel" to "SECURITY",
                "deep_link" to "wgc://auth/verify",
            )

        val payload = NotificationPayloadParser.parse(raw)

        assertEquals("Código de Verificação", payload.title)
        assertEquals("Seu código 2FA é 982312", payload.body)
        assertEquals(NotificationChannelConfig.SECURITY, payload.channel)
        assertEquals("wgc://auth/verify", payload.deepLink)
    }

    @Test
    fun `parse should fallback to GENERAL channel and defaults when missing attributes`() {
        val raw = emptyMap<String, String>()

        val payload = NotificationPayloadParser.parse(raw)

        assertEquals("Notificação", payload.title)
        assertEquals("", payload.body)
        assertEquals(NotificationChannelConfig.GENERAL, payload.channel)
        assertNull(payload.deepLink)
    }
}
