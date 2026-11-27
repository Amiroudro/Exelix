package com.depu.vocabapp.data

/**
 * Human-readable Markdown export for vocabulary portability.
 * This is a reconstruction format, not exact original-APK compatibility.
 */
object MarkdownVocabularyExporter {
    private fun escape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("|", "\\|")
        .replace("\n", "<br>")
        .replace("\r", "")

    fun export(words: List<VocabWord>): String = buildString {
        append("# Exelix Vocabulary Export\n\n")
        append("Exported " + words.size + " words.\n\n")
        words.sortedBy { it.normalizedWord }.forEach { word ->
            append("## " + escape(word.word) + "\n\n")
            listOf(
                "Bangla meaning" to word.banglaMeaning,
                "Definition" to word.englishDefinition,
                "Part of speech" to word.partOfSpeech,
                "Example" to word.exampleSentence.orEmpty(),
                "Synonyms" to word.synonym.orEmpty(),
                "Antonyms" to word.antonym.orEmpty(),
                "Collocations" to word.collocation.orEmpty(),
                "Source" to word.examSourceRef.orEmpty(),
                "Tags" to word.tags
            ).filter { it.second.isNotBlank() }.forEach { (label, value) ->
                append("- **" + label + ":** " + escape(value) + "\n")
            }
            append("\n")
        }
    }
}
