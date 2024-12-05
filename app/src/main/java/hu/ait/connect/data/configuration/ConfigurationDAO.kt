package hu.ait.connect.data.configuration

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigurationDAO {
    @Query("SELECT * FROM configurationtable WHERE id = 1")
    fun getConfiguration() : Flow<Configuration>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Configuration)

    @Update
    suspend fun update(item: Configuration)
}
