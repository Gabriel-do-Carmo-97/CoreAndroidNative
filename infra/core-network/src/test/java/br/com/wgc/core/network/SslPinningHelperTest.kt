package br.com.wgc.core.network

import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SslPinningHelperTest {
    @Test
    fun buildCertificatePinner_whenPinsProvided_shouldCreateConfiguredPinner() {
        val pins =
            mapOf(
                "*.example.com" to listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="),
                "api.google.com" to listOf("sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="),
            )

        val pinner = SslPinningHelper.buildCertificatePinner(pins)

        assertNotNull(pinner)
        assertEquals(2, pinner.pins.size)
    }

    @Test
    fun configureCertificatePinner_shouldAttachPinnerToBuilder() {
        val pins =
            mapOf(
                "*.example.com" to listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="),
            )
        val builder = OkHttpClient.Builder()

        val resultBuilder = SslPinningHelper.configureCertificatePinner(builder, pins)
        val client = resultBuilder.build()

        assertNotNull(client.certificatePinner)
        assertEquals(1, client.certificatePinner.pins.size)
    }
}
