package com.englishapp.di

import android.content.Context
import androidx.room.Room
import com.englishapp.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "english_app.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideThemeDao(db: AppDatabase) = db.themeDao()
}
