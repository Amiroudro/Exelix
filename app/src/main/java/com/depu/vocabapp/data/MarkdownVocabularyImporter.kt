package com.depu.vocabapp.data

/**
 * Parses the portable Markdown format emitted by MarkdownVocabularyExporter.
 * It is intentionally not presented as compatible with the original APK.
 */
object MarkdownVocabularyImporter {
    private val labels = mapOf(
        "bangla meaning" to "meaning",
        "definition" to "definition",
        "part of speech" to "pos",
        "example" to "example",
        "synonyms" to "synonyms",
        "antonyms" to "antonyms",
        "collocations" to "collocations",
        "source" to "source",
        "tags" to "tags"
    )

    fun parse(text: String): List<VocabWord> {
        if (text.length > 2_000_000) throw IllegalArgumentException("Markdown file is too large (maximum 2 MB of text).")
        val source = text.trimStart('\uFEFF')
        if (!source.contains("# Exelix Vocabulary Export")) {
            throw IllegalArgumentException("This does not look like an Exelix Markdown export.")
        }
        val sections = Regex("(?m)^##\\s+(.+?)\\s*$").findAll(source).toList()
        if (sections.size > 10_000) throw IllegalArgumentException("Import contains too many entries (maximum 10,000).")
        return sections.mapNotNull { heading ->
            val start = heading.range.last + 1
            val next = sections.firstOrNull { it.range.first > heading.range.first }?.range?.first ?: source.length
            val rawWord = decode(heading.groupValues[1].trim()).trim()
            if (rawWord.isBlank()) return@mapNotNull null
            val fields = mutableMapOf<String, String>()
            source.substring(start, next).lineSequence().forEach { line ->
                val match = Regex("^\\s*-\\s*\\*\\*([^:]+):\\*\\*\\s*(.*)$").matchEntire(line) ?: return@forEach
                val key = match.groupValues[1].trim().lowercase()
                val mapped = labels[key] ?: return@forEach
                fields[mapped] = decode(match.groupValues[2].trim())
            }
            VocabWord(
                word = rawWord,
                normalizedWord = rawWord.trim().lowercase(),
                banglaMeaning = fields["meaning"].orEmpty(),
                englishDefinition = fields["definition"].orEmpty(),
                partOfSpeech = fields["pos"].orEmpty(),
                exampleSentence = fields["example"].orEmpty().ifBlank { null },
                synonym = fields["synonyms"].orEmpty().ifBlank { null },
                antonym = fields["antonyms"].orEmpty().ifBlank { null },
                collocation = fields["collocations"].orEmpty().ifBlank { null },
                examSourceRef = fields["source"].orEmpty().ifBlank { null },
                tags = fields["tags"].orEmpty()
            )
        }.distinctBy { it.normalizedWord }
    }

    private fun decode(value: String): String {
        val out = StringBuilder()
        var i = 0
        while (i < value.length) {
            if (value[i] == '\\' && i + 1 < value.length) {
                val next = value[i + 1]
                when (next) {
                    '\\' -> { out.append('\\'); i += 2; continue }
                    '|' -> { out.append('|'); i += 2; continue }
                }
            }
            out.append(value[i])
            i++
        }
        return out.toString().replace("<br>", "\n")
    }
}
