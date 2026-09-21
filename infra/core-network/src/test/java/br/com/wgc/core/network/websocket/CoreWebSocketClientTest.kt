package br.com.wgc.core.network.websocket

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoreWebSocketClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: CoreWebSocketClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = CoreWebSocketClient()
    }

    @After
    fun tearDown() {
        client.close()
        runCatching { server.shutdown() }
    }

    @Test
    fun `connect should emit Open and Message events from mock server`() =
        runTest {
            server.enqueue(
                MockResponse().withWebSocketUpgrade(
                    object : WebSocketListener() {
                        override fun onOpen(
                            webSocket: WebSocket,
                            response: Response,
                        ) {
                            webSocket.send("Hello from server")
                        }
                    },
                ),
            )

            val wsUrl = server.url("/ws").toString()

            client.connect(wsUrl).test {
                val openEvent = awaitItem()
                assertTrue(openEvent is WebSocketEvent.Open)

                val msgEvent = awaitItem()
                assertTrue(msgEvent is WebSocketEvent.Message)
                assertEquals("Hello from server", (msgEvent as WebSocketEvent.Message).text)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
