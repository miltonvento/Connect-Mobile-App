package hu.ait.connect.data.person

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import hu.ait.connect.data.category.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDAO {
    @Query("SELECT * FROM person_table")
    fun getAllPeople() : Flow<List<Person>>

    @Query("SELECT * from person_table WHERE id = :id")
    fun getPerson(id: Int): Flow<Person>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: Person)

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("DELETE from person_table")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM person_table WHERE categoryId = :categoryId")
    fun getPeopleByCategory(categoryId: Int): Flow<List<Person>>
}
