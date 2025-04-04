package com.manusai.android.sync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.manusai.android.sync.service.SyncService
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * WorkManager worker that performs background synchronization.
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val syncService: SyncService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Only attempt sync if online
        if (!syncService.isOnline()) {
            return Result.retry()
        }

        return try {
            // Perform synchronization
            syncService.syncData().collect()
            Result.success()
        } catch (e: Exception) {
            // If sync fails, retry later
            Result.retry()
        }
    }

    /**
     * Factory for creating SyncWorker instances with dependency injection.
     */
    class Factory @Inject constructor(
        private val syncService: SyncService
    ) : ChildWorkerFactory {
        override fun create(
            context: Context,
            params: WorkerParameters
        ): CoroutineWorker {
            return SyncWorker(context, params, syncService)
        }
    }
}

/**
 * Interface for creating worker instances with dependencies.
 */
interface ChildWorkerFactory {
    fun create(context: Context, params: WorkerParameters): CoroutineWorker
}
