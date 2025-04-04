package com.manusai.android.di

import android.content.Context
import androidx.room.Room
import com.manusai.android.data.local.AppDatabase
import com.manusai.android.data.local.ChatDao
import com.manusai.android.data.mapper.ChatMessageMapper
import com.manusai.android.data.remote.ApiService
import com.manusai.android.data.remote.ChatRemoteDataSource
import com.manusai.android.data.repository.ChatRepositoryImpl
import com.manusai.android.domain.repository.ChatRepository
import com.manusai.android.llm.model.ModelManager
import com.manusai.android.llm.service.LlmService
import com.manusai.android.llm.service.MlcLlmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "manus_ai_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(appDatabase: AppDatabase): ChatDao {
        return appDatabase.chatDao()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository {
        return chatRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideChatMessageMapper(): ChatMessageMapper {
        return ChatMessageMapper()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.manusai.com/") // Would be replaced with actual API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRemoteDataSource(apiService: ApiService): ChatRemoteDataSource {
        return ChatRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideModelManager(): ModelManager {
        return ModelManager()
    }

    @Provides
    @Singleton
    fun provideLlmService(modelManager: ModelManager): LlmService {
        return MlcLlmService(modelManager)
    }
}
