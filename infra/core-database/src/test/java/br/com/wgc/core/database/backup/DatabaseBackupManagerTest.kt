package br.com.wgc.core.database.backup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DatabaseBackupManagerTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val backupManager = DatabaseBackupManager()
    private val key = "super-secret-backup-passphrase".toByteArray()

    @Test
    fun `backup and restore encrypts and recovers original database contents`() {
        val originalDb = File(tempFolder.root, "test_original.db")
        val backupFile = File(tempFolder.root, "backup.enc")
        val restoredDb = File(tempFolder.root, "restored.db")

        val expectedContent = "SQLite format 3\u0000 -- Sample room table data and indexes"
        originalDb.writeText(expectedContent)

        val backupSize = backupManager.backupDatabase(originalDb, backupFile, key)
        assertTrue(backupSize > 12)
        assertTrue(backupFile.exists())

        val success = backupManager.restoreDatabase(backupFile, restoredDb, key)
        assertTrue(success)
        assertTrue(restoredDb.exists())
        assertEquals(expectedContent, restoredDb.readText())
    }

    @Test
    fun `restore with incorrect key fails authentication`() {
        val originalDb = File(tempFolder.root, "test_original.db")
        val backupFile = File(tempFolder.root, "backup.enc")
        val restoredDb = File(tempFolder.root, "restored.db")

        originalDb.writeText("Sensitive data")
        backupManager.backupDatabase(originalDb, backupFile, key)

        val wrongKey = "wrong-passphrase".toByteArray()
        val success = backupManager.restoreDatabase(backupFile, restoredDb, wrongKey)

        assertFalse(success)
        assertFalse(restoredDb.exists())
    }

    @Test
    fun `restore on non-existent file returns false`() {
        val missingBackup = File(tempFolder.root, "non_existent.enc")
        val restoredDb = File(tempFolder.root, "restored.db")

        val success = backupManager.restoreDatabase(missingBackup, restoredDb, key)
        assertFalse(success)
    }
}
