package br.com.wgc.core.database.maintenance

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DatabaseMaintenanceHelperTest {
    private lateinit var helper: DatabaseMaintenanceHelper
    private lateinit var database: RoomDatabase
    private lateinit var openHelper: SupportSQLiteOpenHelper
    private lateinit var sqLiteDatabase: SupportSQLiteDatabase

    @Before
    fun setUp() {
        helper = DatabaseMaintenanceHelper()
        database = mockk()
        openHelper = mockk()
        sqLiteDatabase = mockk(relaxed = true)

        every { database.openHelper } returns openHelper
        every { openHelper.writableDatabase } returns sqLiteDatabase
    }

    @Test
    fun `checkpoint executes pragma wal_checkpoint truncate`() {
        helper.checkpoint(database)
        verify { sqLiteDatabase.execSQL("PRAGMA wal_checkpoint(TRUNCATE);") }
    }

    @Test
    fun `vacuum executes VACUUM`() {
        helper.vacuum(database)
        verify { sqLiteDatabase.execSQL("VACUUM;") }
    }

    @Test
    fun `analyze executes ANALYZE`() {
        helper.analyze(database)
        verify { sqLiteDatabase.execSQL("ANALYZE;") }
    }

    @Test
    fun `runFullMaintenance executes all routines successfully`() {
        val report = helper.runFullMaintenance(database)

        verify { sqLiteDatabase.execSQL("PRAGMA wal_checkpoint(TRUNCATE);") }
        verify { sqLiteDatabase.execSQL("VACUUM;") }
        verify { sqLiteDatabase.execSQL("ANALYZE;") }

        assertTrue(report.isSuccessful)
        assertEquals(null, report.error)
    }

    @Test
    fun `runFullMaintenance reports failure if pragma throws`() {
        every { sqLiteDatabase.execSQL("VACUUM;") } throws IllegalStateException("Database locked")

        val report = helper.runFullMaintenance(database)

        assertFalse(report.isSuccessful)
        assertNotNull(report.error)
        assertEquals("Database locked", report.error?.message)
    }
}
