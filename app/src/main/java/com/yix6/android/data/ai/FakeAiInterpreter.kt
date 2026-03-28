package com.yix6.android.data.ai

import com.yix6.android.domain.contract.AiInterpreter
import com.yix6.android.domain.contract.InterpretStyle
import kotlinx.coroutines.delay

/**
 * Fake implementation of [AiInterpreter] that returns template-based text.
 * No network calls are made.
 */
class FakeAiInterpreter : AiInterpreter {

    override suspend fun interpret(
        question: String,
        hexagramName: String,
        changedName: String?,
        changingLines: List<Int>,
        style: InterpretStyle,
    ): String {
        // Simulate network latency
        delay(800L)

        val intro = buildIntro(question, hexagramName)
        val core = buildCore(hexagramName, changedName, changingLines)
        val advice = buildAdvice(hexagramName)

        return when (style) {
            InterpretStyle.SHORT -> "$intro\n\n$advice"
            InterpretStyle.NORMAL -> "$intro\n\n$core\n\n$advice"
            InterpretStyle.LONG -> "$intro\n\n$core\n\n${buildExtended(hexagramName, changingLines)}\n\n$advice"
        }
    }

    private fun buildIntro(question: String, hexagramName: String): String {
        val q = question.trim().ifEmpty { "your situation" }
        return "Regarding "$q", the oracle reveals **$hexagramName**.\n" +
                "This hexagram speaks to the essential nature of your inquiry and offers guidance " +
                "from the ancient wisdom of the I Ching."
    }

    private fun buildCore(
        hexagramName: String,
        changedName: String?,
        changingLines: List<Int>,
    ): String {
        val sb = StringBuilder()
        sb.append("**Primary Hexagram – $hexagramName**\n")
        sb.append(
            "The energy present is one of dynamic transformation. Remain attentive to the " +
                    "currents at play and align your actions with the natural flow of events."
        )
        if (changedName != null && changingLines.isNotEmpty()) {
            val linesStr = changingLines.joinToString(", ") { "Line $it" }
            sb.append("\n\n**Changing Lines – $linesStr**\n")
            sb.append(
                "These lines signal pivotal moments of transition. Pay close attention to " +
                        "the areas they represent in your question."
            )
            sb.append("\n\n**Resulting Hexagram – $changedName**\n")
            sb.append(
                "After the transformation, the situation moves toward $changedName. " +
                        "This represents the future potential once the changes have unfolded."
            )
        }
        return sb.toString()
    }

    private fun buildExtended(hexagramName: String, changingLines: List<Int>): String {
        val sb = StringBuilder()
        sb.append("**Deeper Reflection**\n")
        sb.append(
            "The wisdom of $hexagramName reminds us that every moment contains the seed of change. " +
                    "The superior person observes without attachment, acts without forcing, and trusts " +
                    "the natural unfolding of events."
        )
        if (changingLines.isNotEmpty()) {
            sb.append(
                "\n\nWith ${changingLines.size} changing line(s), this reading carries heightened " +
                        "significance. Each changing line is an invitation to reflect deeply on a " +
                        "specific aspect of your path."
            )
        }
        sb.append(
            "\n\nConsider journaling your thoughts, meditating on the imagery of this hexagram, " +
                    "and returning to this reading over the coming days to notice how its meaning evolves."
        )
        return sb.toString()
    }

    private fun buildAdvice(hexagramName: String): String =
        "**Guidance**\n" +
                "Reflect on the essence of $hexagramName as you move forward. " +
                "Trust the process, maintain inner stillness, and let clarity emerge naturally."
}
