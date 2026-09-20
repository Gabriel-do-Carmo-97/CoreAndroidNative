package br.com.wgc.core.testing.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

/**
 * Utilitários auxiliares para simplificar o uso de [MockWebServer] em testes de integração de rede.
 */
object MockWebServerHelper {
    /**
     * Enfileira uma resposta HTTP com corpo em formato JSON.
     */
    fun enqueueJsonResponse(
        server: MockWebServer,
        code: Int = 200,
        jsonBody: String = "{}",
    ) {
        server.enqueue(
            MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(jsonBody),
        )
    }

    /**
     * Enfileira uma resposta HTTP de erro genérico.
     */
    fun enqueueError(
        server: MockWebServer,
        code: Int = 500,
        errorMessage: String = "Internal Server Error",
    ) {
        server.enqueue(
            MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("""{"error":"$errorMessage"}"""),
        )
    }
}
