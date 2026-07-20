package com.englishapp.di

import android.content.Context
import androidx.room.Room
import com.englishapp.data.local.AppDatabase
import com.englishapp.data.remote.ApiService
import com.englishapp.data.remote.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 依赖注入模块。
 * 提供数据库、DAO、API 服务等单例。
 * Repository 使用 @Inject constructor 自动注入,无需在此声明。
 * @author englishapp
 * @since 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** 提供 Room 数据库 */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "english_app.db"
        ).build()
    }

    /** 提供 ThemeDao */
    @Provides
    @Singleton
    fun provideThemeDao(db: AppDatabase) = db.themeDao()

    /** 提供 ApiService(基于 RetrofitClient 单例) */
    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.apiService
}
