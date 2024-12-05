package hu.ait.connect.di

import android.content.Context
import androidx.compose.ui.graphics.Color
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.ait.connect.data.AppDatabase
import hu.ait.connect.data.category.Category
import hu.ait.connect.data.category.CategoryDAO
import hu.ait.connect.data.person.PersonDAO
import hu.ait.connect.data.configuration.ConfigurationDAO
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun providePersonDao(appDatabase: AppDatabase): PersonDAO {
        return appDatabase.personDao()
    }

    @Provides
    fun provideConfigurationDao(appDatabase: AppDatabase): ConfigurationDAO {
        return appDatabase.configurationDao()
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDAO {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }
}