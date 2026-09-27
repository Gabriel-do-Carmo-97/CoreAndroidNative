package br.com.wgc.core.network.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BidirectionalSyncEngineTest {
    private val engine = BidirectionalSyncEngine<String>()

    @Test
    fun `equal clocks result in InSync`() {
        val clock = VectorClock(mapOf("client" to 1L, "server" to 2L))
        val local = SyncEntity("1", "data", clock)
        val remote = SyncEntity("1", "data", clock)

        val action = engine.reconcile(local, remote)
        assertTrue(action is SyncAction.InSync)
    }

    @Test
    fun `remote clock strictly ahead results in ApplyRemote`() {
        val localClock = VectorClock(mapOf("client" to 1L, "server" to 1L))
        val remoteClock = VectorClock(mapOf("client" to 1L, "server" to 2L))

        val local = SyncEntity("1", "old_local", localClock)
        val remote = SyncEntity("1", "new_remote", remoteClock)

        val action = engine.reconcile(local, remote)
        assertTrue(action is SyncAction.ApplyRemote)
        assertEquals("new_remote", (action as SyncAction.ApplyRemote).entity.data)
    }

    @Test
    fun `local clock strictly ahead results in PushLocal`() {
        val localClock = VectorClock(mapOf("client" to 2L, "server" to 1L))
        val remoteClock = VectorClock(mapOf("client" to 1L, "server" to 1L))

        val local = SyncEntity("1", "new_local", localClock)
        val remote = SyncEntity("1", "old_remote", remoteClock)

        val action = engine.reconcile(local, remote)
        assertTrue(action is SyncAction.PushLocal)
        assertEquals("new_local", (action as SyncAction.PushLocal).entity.data)
    }

    @Test
    fun `divergent concurrent clocks trigger conflict resolution`() {
        // Client updated locally, server updated independently
        val localClock = VectorClock(mapOf("client" to 2L, "server" to 1L))
        val remoteClock = VectorClock(mapOf("client" to 1L, "server" to 2L))

        val local = SyncEntity("1", "local_edit", localClock, timestamp = 100L)
        val remote = SyncEntity("1", "remote_edit", remoteClock, timestamp = 200L)

        val action = engine.reconcile(local, remote)
        assertTrue(action is SyncAction.ConflictResolved)
        // By LastWriteWins, remote wins since 200 > 100
        val resolved = (action as SyncAction.ConflictResolved).resolved
        assertEquals("remote_edit", resolved.data)
        // Clock is merged
        assertEquals(2L, resolved.clock.clocks["client"])
        assertEquals(2L, resolved.clock.clocks["server"])
    }
}
