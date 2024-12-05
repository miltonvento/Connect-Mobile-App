package hu.ait.connect.data.person

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDAO {
    @Query("SELECT * FROM persontable")
    fun getAllPeople() : Flow<List<Person>>

    @Query("SELECT * from persontable WHERE id = :id")
    fun getPerson(id: Int): Flow<Person>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Person)

    @Update
    suspend fun update(item: Person)

    @Delete
    suspend fun delete(item: Person)

    @Query("DELETE from persontable")
    suspend fun deleteAllItems()
}





