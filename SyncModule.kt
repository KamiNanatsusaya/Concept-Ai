package com.manusai.android.sync.di

import android.content.Context
import androidx.work.WorkManager
import com.manusai.android.sync.conflict.ConflictResolver
import com.manusai.android.sync.manager.SyncManager
import com.manusai.android.sync.service.SyncService
import com.manusai.android.sync.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
    
    @Provides
    @Singleton
    fun provideConflictResolver(): ConflictResolver {
        return ConflictResolver()
    }
    
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    @Provides
    @Singleton
    fun provideSyncManager(
        workManager: WorkManager,
        syncService: SyncService,
        networkMonitor: NetworkMonitor,
        applicationScope: CoroutineScope
    ): SyncManager {
        return SyncManager(workManager, syncService, networkMonitor, applicationScope)
    }
}
