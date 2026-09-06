package br.com.wgc.core.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import br.com.wgc.core.database.dao.BaseDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Entity(tableName = "test_items")
data class TestItemEntity(
    @PrimaryKey val id: Long,
    val title: String
)

@Dao
interface TestItemDao : BaseDao<TestItemEntity> {
    @Query("SELECT * FROM test_items WHERE id = :id")
    suspend fun getById(id: Long): TestItemEntity?
}

@Database(entities = [TestItemEntity::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase() {
    abstract fun testItemDao(): TestItemDao
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BaseDaoTest {

    private lateinit var db: TestDatabase
    private lateinit var dao: TestItemDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.testItemDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndGetById_returnsInsertedEntity() = runTest {
        val entity = TestItemEntity(id = 1L, title = "Item 1")
        dao.insert(entity)

        val retrieved = dao.getById(1L)
        assertEquals(entity, retrieved)
    }

    @Test
    fun delete_removesEntity() = runTest {
        val entity = TestItemEntity(id = 2L, title = "Item 2")
        dao.insert(entity)

        dao.delete(entity)

        val retrieved = dao.getById(2L)
        assertNull(retrieved)
    }
}
