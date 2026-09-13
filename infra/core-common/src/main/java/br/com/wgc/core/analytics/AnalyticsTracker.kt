package br.com.wgc.core.analytics

/**
 * Interface contract for logging domain analytics events, screens, and user properties.
 */
interface AnalyticsTracker {
    /**
     * Records a tracked domain event with key-value contextual parameters.
     *
     * @param name Unique event identifier (e.g., "checkout_completed").
     * @param params Key-value map representing event payload.
     */
    fun logEvent(
        name: String,
        params: Map<String, Any> = emptyMap(),
    )

    /**
     * Associates a user-level characteristic with the active analytics session.
     *
     * @param name Property key (e.g., "subscription_tier").
     * @param value Property value.
     */
    fun setUserProperty(
        name: String,
        value: String,
    )

    /**
     * Associates an authenticated user identifier across all active trackers.
     *
     * @param userId Unique user identifier, or null when logging out.
     */
    fun setUserId(userId: String?)
}
