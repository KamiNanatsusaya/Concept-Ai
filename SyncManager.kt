package com.manusai.android.sync.manager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.manusai.android.sync.service.SyncService
import com.manusai.android.sync.util.NetworkMonitor
import com.manusai.android.sync.worker.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class that coordinates synchronization operations.
 */
@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
    private val syncService: SyncService,
    private val networkMonitor: NetworkMonitor,
    private val applicationScope: CoroutineScope
) {
    companion object {
        private const val SYNC_WORK_NAME = "sync_work"
        private const val SYNC_INTERVAL_HOURS = 1L
    }

    /**
     * Initializes the sync manager and sets up periodic sync.
     */
    fun initialize() {
        // Monitor network connectivity
        applicationScope.launch {
            networkMonitor.isOnline().collectLatest { isOnline ->
                if (isOnline) {
                    // When coming online, perform immediate sync
                    syncNow()
                }
            }
        }

        // Schedule periodic sync
        setupPeriodicSync()
    }

    /**
     * Performs an immediate synchronization if online.
     */
    fun syncNow() {
        if (syncService.isOnline()) {
            applicationScope.launch {
                syncService.syncData().collect()
            }
        }
    }

    /**
     * Sets up periodic background synchronization using WorkManager.
     */
    private fun setupPeriodicSync() {
        // Define constraints - only run when connected to network
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create periodic work request
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            SYNC_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        // Enqueue the work, replacing any existing work
        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

    /**
     * Cancels all pending synchronization work.
     */
    fun cancelSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
    }
}
