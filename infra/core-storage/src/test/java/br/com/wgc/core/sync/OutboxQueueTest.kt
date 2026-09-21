package br.com.wgc.core.sync

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class OutboxQueueTest {
    private lateinit var queue: OutboxQueue

    @Before
    fun setUp() {
        queue = DefaultOutboxQueue()
    }

    @Test
    fun `enqueue and dequeue should maintain FIFO order and update counts`() =
        runTest {
            val req1 = OutboxRequest(endpoint = "/api/v1/orders", payload = "{\"orderId\": 1}")
            val req2 = OutboxRequest(endpoint = "/api/v1/payments", payload = "{\"paymentId\": 2}")

            queue.enqueue(req1)
            queue.enqueue(req2)

            assertEquals(2, queue.observePendingCount().first())
            assertEquals(2, queue.getAllPending().size)

            val dequeued1 = queue.dequeue()
            assertNotNull(dequeued1)
            assertEquals(req1.id, dequeued1?.id)
            assertEquals(1, queue.observePendingCount().first())

            val dequeued2 = queue.dequeue()
            assertNotNull(dequeued2)
            assertEquals(req2.id, dequeued2?.id)
            assertEquals(0, queue.observePendingCount().first())

            assertNull(queue.dequeue())
        }

    @Test
    fun `remove and clear should discard targeted requests`() =
        runTest {
            val req = OutboxRequest(endpoint = "/api/v1/profile")
            queue.enqueue(req)
            assertEquals(1, queue.observePendingCount().first())

            queue.remove(req.id)
            assertEquals(0, queue.observePendingCount().first())
            assertNull(queue.peek())

            queue.enqueue(OutboxRequest(endpoint = "/api/v1/items"))
            queue.enqueue(OutboxRequest(endpoint = "/api/v1/checkout"))
            assertEquals(2, queue.observePendingCount().first())

            queue.clear()
            assertEquals(0, queue.observePendingCount().first())
            assertEquals(0, queue.getAllPending().size)
        }
}
