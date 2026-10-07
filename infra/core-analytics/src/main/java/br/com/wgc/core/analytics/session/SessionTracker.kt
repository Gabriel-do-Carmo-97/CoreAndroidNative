package br.com.wgc.core.analytics.session

import java.util.UUID

/**
 * Representation of an end-user interaction session.
 *
 * @property sessionId Unique identifier rotated on inactivity.
 * @property startedAtMs Session start timestamp.
 * @property lastActivityMs Last recorded user interaction timestamp.
 */
data class UserSession(
    val sessionId: String,
    val startedAtMs: Long,
    var lastActivityMs: Long,
)

/**
 * Manager tracking user session lifecycle and rotating session IDs upon timeout.
 */
class SessionTracker(
    private val sessionTimeoutMs: Long = DEFAULT_SESSION_TIMEOUT_MS,
) {
    private var currentSession: UserSession? = null

    /**
     * Records user activity, returning the active session ID (creating/renewing if timed out).
     */
    @Synchronized
    fun touchActivity(): String {
        val now = System.currentTimeMillis()
        val session = currentSession

        return if (session == null || (now - session.lastActivityMs) > sessionTimeoutMs) {
            val newSession =
                UserSession(
                    sessionId = UUID.randomUUID().toString(),
                    startedAtMs = now,
                    lastActivityMs = now,
                )
            currentSession = newSession
            newSession.sessionId
        } else {
            session.lastActivityMs = now
            session.sessionId
        }
    }

    @Synchronized
    fun getCurrentSessionId(): String? = currentSession?.sessionId

    companion object {
        const val DEFAULT_SESSION_TIMEOUT_MS = 30 * 60 * 1000L // 30 minutes
    }
}
