package com.manusai.android.llm.model

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the LLM models available in the application.
 * Handles model loading, unloading, and selection.
 */
@Singleton
class ModelManager @Inject constructor() {
    
    // Available models for the prototype
    private val availableModels = listOf(
        ModelInfo("SmolLM2-135M", "Sehr kleines Modell (135M Parameter), ideal für ältere Geräte", 135),
        ModelInfo("SmolLM2-360M", "Kleines Modell (360M Parameter), gute Balance zwischen Größe und Leistung", 360),
        ModelInfo("SmolLM2-1.7B", "Mittelgroßes Modell (1.7B Parameter), bessere Qualität für leistungsfähige Geräte", 1700),
        ModelInfo("Gemma-2B", "Google's Gemma Modell (2B Parameter), hohe Qualität für High-End-Geräte", 2000)
    )
    
    // Currently selected model
    private var currentModel: ModelInfo = availableModels[0]
    
    /**
     * Gets a list of all available models.
     */
    fun getAvailableModels(): List<ModelInfo> = availableModels
    
    /**
     * Gets the currently selected model.
     */
    fun getCurrentModel(): ModelInfo = currentModel
    
    /**
     * Selects a model by name.
     * 
     * @param modelName The name of the model to select
     * @return True if the model was found and selected, false otherwise
     */
    fun selectModel(modelName: String): Boolean {
        val model = availableModels.find { it.name == modelName }
        return if (model != null) {
            currentModel = model
            true
        } else {
            false
        }
    }
    
    /**
     * Recommends a model based on device specifications.
     * 
     * @param availableRamMb The available RAM in MB
     * @return The recommended model
     */
    fun recommendModelForDevice(availableRamMb: Int): ModelInfo {
        return when {
            availableRamMb >= 4000 -> availableModels.find { it.name == "SmolLM2-1.7B" } ?: availableModels[0]
            availableRamMb >= 2000 -> availableModels.find { it.name == "SmolLM2-360M" } ?: availableModels[0]
            else -> availableModels[0] // Default to smallest model
        }
    }
    
    /**
     * Checks if a model is suitable for the device.
     * 
     * @param modelName The name of the model to check
     * @param availableRamMb The available RAM in MB
     * @return True if the model is suitable, false otherwise
     */
    fun isModelSuitableForDevice(modelName: String, availableRamMb: Int): Boolean {
        val model = availableModels.find { it.name == modelName } ?: return false
        
        // Simple heuristic: model size in MB should be less than 1/3 of available RAM
        return model.sizeInMb * 3 <= availableRamMb
    }
}

/**
 * Represents information about an LLM model.
 */
data class ModelInfo(
    val name: String,
    val description: String,
    val sizeInMb: Int
)
