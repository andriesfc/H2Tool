package h2tool.utils

import h2tool.utils.Paragraph.TrimmedAs.*
import java.io.File
import java.util.*

inline fun <reified T> props(): Map<String, String?> {
    val resId = T::class.java.run { "$simpleName.properties" }
    val res = T::class.java.getResource(resId)
        ?.openStream()
        ?.reader()
        ?.use { Properties().apply { load(it) } } ?: return emptyMap()
    return buildMap(res.size) {
        res.entries.forEach { (k, v) -> put(k as String, v as String?) }
    }
}

fun File.write(props: Map<String, String>, comment: String? = null): File = apply {
    outputStream().writer().use {
        Properties().apply {
            putAll(props)
            store(it, comment)
        }
    }
}


/**
 * Converts text to a single line of text based on the [Paragraph.TrimmedAs] values.
 *
 * @see [of]
 */
object Paragraph {

    /**
     * Determine how the current text is trimmed. There are 4 possibilities: [ByIndent], [ByMargin], [Always] and [Ignore].
     * Note the default is to [Always]
     *
     */
    sealed interface TrimmedAs {

        /**
         * Assume each line is prefixed by an indented whitespace
         */
        data object ByIndent : TrimmedAs

        /**
         * Ignore any trimming, just keep it as is.
         */
        data object Ignore : TrimmedAs

        /**
         * Remove all white space from the beginning and each the lines
         */
        data object Always : TrimmedAs

        /**
         *  Removes each line indented by white space followed by the specified [ByMargin.prefix] value.
         */
        interface ByMargin : TrimmedAs {

            val prefix: String

            companion object : ByMargin by MarginPrefix("|") {
                fun of(margin: Char): ByMargin = MarginPrefix(margin.toString())
            }

            private data class MarginPrefix(override val prefix: String) : ByMargin
        }

        data object KDoc : TrimmedAs
    }


    private fun lines(trimmed: TrimmedAs, multiLineText: String): Sequence<String> {
        return when (trimmed) {
            ByIndent -> multiLineText.trimIndent().lineSequence()
            is ByMargin -> multiLineText.trimMargin(trimmed.prefix).lineSequence()
            Ignore -> multiLineText.lineSequence()
            Always -> multiLineText.lineSequence().map(String::trim)
            KDoc -> multiLineText.trimMargin("*").lineSequence().map(String::trim)
        }
    }
    fun of(trimmed: TrimmedAs, multiLineText: String): String {
        return when {
            multiLineText.isEmpty() -> multiLineText
            multiLineText.trim().isEmpty() -> ""
            else -> lines(trimmed, multiLineText).joinToString(" ")
        }
    }
}

fun String.p(margin: Paragraph.TrimmedAs): String = Paragraph.of(margin, this)
fun String.p(): String = Paragraph.of(Always, this)

fun <K,V> Map<K,V?>.valueOf(key: K):V = get(key) ?: throw IllegalArgumentException("Unsupported key: $key")
