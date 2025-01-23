package hu.ait.connect.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.ait.connect.data.category.Category
import hu.ait.connect.data.category.CategoryDAO
import hu.ait.connect.data.configuration.Configuration
import hu.ait.connect.data.configuration.ConfigurationDAO
import hu.ait.connect.data.person.Person
import hu.ait.connect.data.person.PersonDAO

@Database(entities = [Person::class, Configuration::class, Category::class], version = 11, exportSchema = false)
@TypeConverters(Converters::class) // Register the Converters class
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDAO
    abstract fun configurationDao(): ConfigurationDAO
    abstract fun categoryDao(): CategoryDAO

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java,
                    "people_database.db")
                    // Setting this option in your app's database builder means that Room
                    // permanently deletes all data from the tables in your database when it
                    // attempts to perform a migration with no defined migration path.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}