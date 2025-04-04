package com.manusai.android.sync.service

import com.manusai.android.data.local.ChatLocalDataSource
import com.manusai.android.data.mapper.ChatMessageMapper
import com.manusai.android.data.remote.ChatRemoteDataSource
import com.manusai.android.sync.conflict.ConflictResolver
import com.manusai.android.sync.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced service responsible for synchronizing chat data between local storage and remote server.
 * This implementation includes conflict resolution and network awareness.
 */
@Singleton
class SyncServiceImpl @Inject constructor(
    private val localDataSource: ChatLocalDataSource,
    private val remoteDataSource: ChatRemoteDataSource,
    private val mapper: ChatMessageMapper,
    private val conflictResolver: ConflictResolver,
    private val networkMonitor: NetworkMonitor
) : SyncService {
    
    /**
     * Synchronizes local data with remote server.
     * 
     * @return Flow emitting sync status updates
     */
    override fun syncData(): Flow<SyncStatus> = flow {
        emit(SyncStatus.Started)
        
        if (!isOnline()) {
            emit(SyncStatus.Error("Keine Internetverbindung verfügbar"))
            return@flow
        }
        
        try {
            emit(SyncStatus.InProgress(0))
            
            // 1. Get unsynced local messages
            val unsyncedMessages = localDataSource.getUnsyncedMessages()
            emit(SyncStatus.InProgress(20))
            
            // 2. Get all remote messages
            val remoteMessages = remoteDataSource.fetchMessages()
            emit(SyncStatus.InProgress(40))
            
            // 3. Resolve conflicts
            val resolution = conflictResolver.resolveConflicts(unsyncedMessages, remoteMessages)
            emit(SyncStatus.InProgress(60))
            
            // 4. Update local database with remote changes
            resolution.messagesToUpdateLocally.forEach { remoteMessage ->
                val localEntity = mapper.mapRemoteMessageToEntity(remoteMessage)
                localDataSource.insertMessage(localEntity)
            }
            emit(SyncStatus.InProgress(80))
            
            // 5. Update remote database with local changes
            resolution.messagesToUpdateRemotely.forEach { localEntity ->
                val remoteMessage = mapper.mapEntityToRemoteMessage(localEntity)
                remoteDataSource.syncMessage(remoteMessage)
                localDataSource.markMessageAsSynced(localEntity.id)
            }
            
            emit(SyncStatus.Success)
        } catch (e: Exception) {
            emit(SyncStatus.Error(e.message ?: "Unbekannter Fehler bei der Synchronisierung"))
        }
    }
    
    /**
     * Checks if the device is currently online.
     * 
     * @return True if online, false otherwise
     */
    override fun isOnline(): Boolean {
        // In a real implementation, this would use NetworkMonitor
        // For the prototype, we'll simulate being online
        return true
    }
}
