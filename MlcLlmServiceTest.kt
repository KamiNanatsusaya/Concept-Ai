package com.manusai.android.test

import com.manusai.android.llm.model.ModelManager
import com.manusai.android.llm.service.MlcLlmService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MlcLlmServiceTest {

    @Mock
    private lateinit var modelManager: ModelManager

    private lateinit var llmService: MlcLlmService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        llmService = MlcLlmService(modelManager)
    }

    @Test
    fun `generateResponse returns simulated response for greeting`() = runBlocking {
        // When
        val response = llmService.generateResponse("Hallo")

        // Then
        assertTrue(response.contains("Hallo"))
        assertTrue(response.contains("helfen"))
    }

    @Test
    fun `generateResponse returns simulated response for weather query`() = runBlocking {
        // When
        val response = llmService.generateResponse("Wie ist das Wetter heute?")

        // Then
        assertTrue(response.contains("Wetter"))
        assertTrue(response.contains("offline"))
    }

    @Test
    fun `generateResponse returns simulated response for name query`() = runBlocking {
        // When
        val response = llmService.generateResponse("Wie heißt du?")

        // Then
        assertTrue(response.contains("Manus AI"))
    }

    @Test
    fun `generateResponse returns simulated response for offline query`() = runBlocking {
        // When
        val response = llmService.generateResponse("Funktionierst du offline?")

        // Then
        assertTrue(response.contains("offline"))
        assertTrue(response.contains("Gerät"))
    }

    @Test
    fun `generateResponse returns simulated response for model query`() = runBlocking {
        // Given
        `when`(modelManager.getCurrentModel().name).thenReturn("TestModel")

        // When
        val response = llmService.generateResponse("Welches Modell verwendest du?")

        // Then
        assertTrue(response.contains("TestModel"))
    }

    @Test
    fun `getActiveModelName returns current model name`() {
        // Given
        `when`(modelManager.getCurrentModel().name).thenReturn("TestModel")

        // When
        val modelName = llmService.getActiveModelName()

        // Then
        assertEquals("TestModel", modelName)
    }

    @Test
    fun `isReady returns model loaded status`() = runBlocking {
        // When
        val initialStatus = llmService.isReady()
        llmService.loadModel("SmolLM2-135M")
        val afterLoadStatus = llmService.isReady()

        // Then
        assertFalse(initialStatus)
        assertTrue(afterLoadStatus)
    }

    @Test
    fun `loadModel returns true for successful load`() = runBlocking {
        // When
        val result = llmService.loadModel("SmolLM2-135M")

        // Then
        assertTrue(result)
    }
}
