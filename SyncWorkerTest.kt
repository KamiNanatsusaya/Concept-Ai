package com.manusai.android.test

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.manusai.android.sync.service.SyncService
import com.manusai.android.sync.service.SyncStatus
import com.manusai.android.sync.worker.SyncWorker
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SyncWorkerTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var workerParams: WorkerParameters

    @Mock
    private lateinit var syncService: SyncService

    private lateinit var syncWorker: SyncWorker

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        syncWorker = SyncWorker(context, workerParams, syncService)
    }

    @Test
    fun `doWork returns success when sync completes successfully`() = runBlockingTest {
        // Given
        `when`(syncService.isOnline()).thenReturn(true)
        `when`(syncService.syncData()).thenReturn(
            flowOf(
                SyncStatus.Started,
                SyncStatus.InProgress(50),
                SyncStatus.Success
            )
        )

        // When
        val result = syncWorker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork returns retry when device is offline`() = runBlockingTest {
        // Given
        `when`(syncService.isOnline()).thenReturn(false)

        // When
        val result = syncWorker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `doWork returns retry when sync fails`() = runBlockingTest {
        // Given
        `when`(syncService.isOnline()).thenReturn(true)
        `when`(syncService.syncData()).thenReturn(
            flowOf(
                SyncStatus.Started,
                SyncStatus.InProgress(50),
                SyncStatus.Error("Test error")
            )
        )

        // When
        val result = syncWorker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `doWork returns retry when exception is thrown`() = runBlockingTest {
        // Given
        `when`(syncService.isOnline()).thenReturn(true)
        `when`(syncService.syncData()).thenThrow(RuntimeException("Test exception"))

        // When
        val result = syncWorker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
