package com.aiService.llm;

/**
 * Pluggable LLM backend: stub, Ollama, OpenAI-compatible APIs (OpenAI, Groq), or Gemini.
 */
public interface LlmClient {

    String analyze(String prompt);

    /** Short label stored on incidents (e.g. "ollama:llama3.2"). */
    String modelLabel();
}
