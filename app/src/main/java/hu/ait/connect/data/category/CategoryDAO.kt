package hu.ait.connect.data.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM category_table")
    fun getAllCategories() : Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category

    @Query("SELECT * FROM category_table WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): Category?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE from category_table")
    suspend fun deleteAllItems()
}