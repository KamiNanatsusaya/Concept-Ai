package com.manusai.android.test

import com.manusai.android.llm.model.ModelInfo
import com.manusai.android.llm.model.ModelManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ModelManagerTest {

    private lateinit var modelManager: ModelManager

    @Before
    fun setup() {
        modelManager = ModelManager()
    }

    @Test
    fun `getAvailableModels returns all available models`() {
        // When
        val models = modelManager.getAvailableModels()

        // Then
        assertEquals(4, models.size)
        assertTrue(models.any { it.name == "SmolLM2-135M" })
        assertTrue(models.any { it.name == "SmolLM2-360M" })
        assertTrue(models.any { it.name == "SmolLM2-1.7B" })
        assertTrue(models.any { it.name == "Gemma-2B" })
    }

    @Test
    fun `getCurrentModel returns default model initially`() {
        // When
        val currentModel = modelManager.getCurrentModel()

        // Then
        assertEquals("SmolLM2-135M", currentModel.name)
    }

    @Test
    fun `selectModel changes current model when valid model name provided`() {
        // When
        val result = modelManager.selectModel("SmolLM2-360M")
        val currentModel = modelManager.getCurrentModel()

        // Then
        assertTrue(result)
        assertEquals("SmolLM2-360M", currentModel.name)
    }

    @Test
    fun `selectModel returns false when invalid model name provided`() {
        // When
        val result = modelManager.selectModel("NonExistentModel")
        val currentModel = modelManager.getCurrentModel()

        // Then
        assertFalse(result)
        assertEquals("SmolLM2-135M", currentModel.name) // Should still be default
    }

    @Test
    fun `recommendModelForDevice returns appropriate model based on available RAM`() {
        // Test with low RAM
        val lowRamModel = modelManager.recommendModelForDevice(1500)
        assertEquals("SmolLM2-135M", lowRamModel.name)

        // Test with medium RAM
        val mediumRamModel = modelManager.recommendModelForDevice(3000)
        assertEquals("SmolLM2-360M", mediumRamModel.name)

        // Test with high RAM
        val highRamModel = modelManager.recommendModelForDevice(6000)
        assertEquals("SmolLM2-1.7B", highRamModel.name)
    }

    @Test
    fun `isModelSuitableForDevice correctly determines if model fits device constraints`() {
        // Small model on low-end device
        assertTrue(modelManager.isModelSuitableForDevice("SmolLM2-135M", 1000))

        // Medium model on low-end device (should not be suitable)
        assertFalse(modelManager.isModelSuitableForDevice("SmolLM2-360M", 1000))

        // Medium model on mid-range device
        assertTrue(modelManager.isModelSuitableForDevice("SmolLM2-360M", 2000))

        // Large model on high-end device
        assertTrue(modelManager.isModelSuitableForDevice("Gemma-2B", 8000))

        // Large model on mid-range device (should not be suitable)
        assertFalse(modelManager.isModelSuitableForDevice("Gemma-2B", 4000))
    }
}
