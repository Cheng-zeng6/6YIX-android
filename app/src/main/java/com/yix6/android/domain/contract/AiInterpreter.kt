package com.yix6.android.domain.contract

/** Interpretation style requested by the user. */
enum class InterpretStyle { SHORT, NORMAL, LONG }

/** Contract for AI-powered (or stub) hexagram interpretation. */
interface AiInterpreter {
    /**
     * Generate an interpretation for the given hexagram context.
     *
     * @param question      The user's question or intention.
     * @param hexagramName  Name of the primary hexagram.
     * @param changedName   Name of the changed hexagram, or null.
     * @param changingLines 1-based line numbers that are changing.
     * @param style         Desired length/depth of the response.
     * @return A text interpretation.
     */
    suspend fun interpret(
        question: String,
        hexagramName: String,
        changedName: String?,
        changingLines: List<Int>,
        style: InterpretStyle,
    ): String
}
