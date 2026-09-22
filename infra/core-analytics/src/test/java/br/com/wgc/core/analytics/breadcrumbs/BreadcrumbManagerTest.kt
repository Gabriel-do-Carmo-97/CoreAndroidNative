package br.com.wgc.core.analytics.breadcrumbs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreadcrumbManagerTest {
    @Test
    fun `addBreadcrumb stores breadcrumbs up to capacity and drops oldest on overflow`() {
        val manager = BreadcrumbManager(maxCapacity = 3)

        manager.addBreadcrumb("Step 1", "nav")
        manager.addBreadcrumb("Step 2", "nav")
        manager.addBreadcrumb("Step 3", "nav")

        assertEquals(3, manager.size())

        // Add 4th item, oldest ("Step 1") should be dropped
        manager.addBreadcrumb("Step 4", "nav")

        val breadcrumbs = manager.getBreadcrumbs()
        assertEquals(3, breadcrumbs.size)
        assertEquals("Step 2", breadcrumbs[0].message)
        assertEquals("Step 3", breadcrumbs[1].message)
        assertEquals("Step 4", breadcrumbs[2].message)
    }

    @Test
    fun `clear empties the breadcrumbs buffer`() {
        val manager = BreadcrumbManager(maxCapacity = 5)
        manager.addBreadcrumb("Crash imminent", "error", BreadcrumbLevel.ERROR)
        assertEquals(1, manager.size())

        manager.clear()
        assertEquals(0, manager.size())
        assertTrue(manager.getBreadcrumbs().isEmpty())
    }
}
