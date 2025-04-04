package com.manusai.android.llm.service

interface LlmService {
    /**
     * Generates a response to the given user input using the currently selected LLM model.
     * 
     * @param userInput The user's input text
     * @return The generated response from the LLM
     */
    suspend fun generateResponse(userInput: String): String
    
    /**
     * Gets the name of the currently active model.
     * 
     * @return The name of the active model
     */
    fun getActiveModelName(): String
    
    /**
     * Checks if the LLM service is ready to process requests.
     * 
     * @return True if the service is ready, false otherwise
     */
    suspend fun isReady(): Boolean
    
    /**
     * Loads a specific model by name.
     * 
     * @param modelName The name of the model to load
     * @return True if the model was loaded successfully, false otherwise
     */
    suspend fun loadModel(modelName: String): Boolean
}
