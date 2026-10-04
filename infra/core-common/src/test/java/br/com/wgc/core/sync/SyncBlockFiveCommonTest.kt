package br.com.wgc.core.sync

import br.com.wgc.core.sync.crdt.LwwRegister
import br.com.wgc.core.sync.crdt.PnCounter
import br.com.wgc.core.sync.crdt.TwoPhaseSet
import br.com.wgc.core.sync.delta.DeltaOperation
import br.com.wgc.core.sync.delta.DeltaSyncEngine
import br.com.wgc.core.sync.delta.EntityDelta
import br.com.wgc.core.sync.lamport.LamportClock
import br.com.wgc.core.sync.priority.MutationPriority
import br.com.wgc.core.sync.priority.PrioritizedMutation
import br.com.wgc.core.sync.resilience.ExponentialBackoffWithJitter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncBlockFiveCommonTest {
    @Test
    fun testLwwRegisterConvergence() {
        val regA = LwwRegister("Initial", timestamp = 100L, peerId = "peer1")
        val regB = LwwRegister("Updated", timestamp = 200L, peerId = "peer2")

        val merged = regA.merge(regB)
        assertEquals("Updated", merged.value)
        assertEquals(200L, merged.timestamp)
    }

    @Test
    fun testTwoPhaseSetTombstone() {
        val set =
            TwoPhaseSet<String>()
                .add("item1")
                .add("item2")
                .remove("item1")

        assertEquals(setOf("item2"), set.elements)
        assertFalse(set.elements.contains("item1"))
    }

    @Test
    fun testPnCounterConcurrentOperations() {
        val counter1 = PnCounter().increment("nodeA", 5).decrement("nodeA", 2)
        val counter2 = PnCounter().increment("nodeB", 10).decrement("nodeB", 4)

        val merged = counter1.merge(counter2)
        // nodeA: 5 - 2 = 3; nodeB: 10 - 4 = 6; Total = 9
        assertEquals(9L, merged.value)
    }

    @Test
    fun testLamportClockOrdering() {
        val clock = LamportClock(10L)
        assertEquals(11L, clock.tick())

        // Receive remote event with timestamp 20
        assertEquals(21L, clock.update(20L))
        assertEquals(22L, clock.tick())
    }

    @Test
    fun testDeltaSyncCompaction() {
        val engine = DeltaSyncEngine()
        engine.recordDelta(EntityDelta("user_1", "User", DeltaOperation.INSERT, "v1", 1L))
        engine.recordDelta(EntityDelta("user_1", "User", DeltaOperation.UPDATE, "v2", 2L))
        engine.recordDelta(EntityDelta("user_2", "User", DeltaOperation.INSERT, "v1", 1L))

        val compacted = engine.compact()
        assertEquals(2, compacted.size)
        val user1Delta = compacted.first { it.entityId == "user_1" }
        assertEquals("v2", user1Delta.serializedPayload)
        assertEquals(2L, user1Delta.logicalTimestamp)
    }

    @Test
    fun testPrioritizedMutationOrdering() {
        val low = PrioritizedMutation("t1", MutationPriority.LOW, 1000L)
        val critical = PrioritizedMutation("t2", MutationPriority.CRITICAL, 2000L)

        val list = listOf(low, critical).sorted()
        assertEquals("t2", list[0].taskId)
        assertEquals("t1", list[1].taskId)
    }

    @Test
    fun testExponentialBackoffBounds() {
        val backoff = ExponentialBackoffWithJitter(baseDelayMs = 100L, maxDelayMs = 1000L)
        val delay0 = backoff.calculateDelay(0)
        assertEquals(0L, delay0)

        val delay1 = backoff.calculateDelay(1)
        assertTrue(delay1 in 0L..100L)
    }
}
