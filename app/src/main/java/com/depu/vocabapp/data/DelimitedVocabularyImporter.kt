package com.depu.vocabapp.data

/** CSV/TSV vocabulary parser with quoted fields and flexible column aliases. */
object DelimitedVocabularyImporter {
    fun parse(text: String): List<VocabWord> {
        val source = text.trimStart('\uFEFF').trim()
        if (source.isBlank()) return emptyList()
        val delimiter = if (source.lineSequence().firstOrNull()?.count { it == '\t' } ?: 0 >
            (source.lineSequence().firstOrNull()?.count { it == ',' } ?: 0)) '\t' else ','
        val rows = parseRows(source, delimiter).filter { row -> row.any { it.isNotBlank() } }
        if (rows.size < 2) return emptyList()
        val headers = rows.first().map { normalizeHeader(it) }
        fun index(vararg names: String): Int = headers.indexOfFirst { it in names.map(::normalizeHeader) }
        val wordIndex = index("word", "vocabulary", "term", "english", "english word")
        if (wordIndex < 0) throw IllegalArgumentException("A header column named Word is required.")
        fun value(row: List<String>, idx: Int): String = if (idx >= 0 && idx < row.size) row[idx].trim() else ""
        val meaning = index("bangla meaning", "meaning", "bangla", "বাংলা", "বাংলা অর্থ")
        val definition = index("definition", "english definition", "explanation")
        val pos = index("part of speech", "pos", "type")
        val example = index("example", "example sentence", "sentence")
        val synonyms = index("synonym", "synonyms")
        val antonyms = index("antonym", "antonyms")
        val collocations = index("collocation", "collocations")
        val sourceRef = index("source", "exam", "exam source")
        val tags = index("tags", "tag", "category")
        return rows.drop(1).mapNotNull { row ->
            val word = value(row, wordIndex)
            if (word.isBlank()) null else VocabWord(
                word = word,
                normalizedWord = word.lowercase().trim(),
                banglaMeaning = value(row, meaning),
                englishDefinition = value(row, definition),
                partOfSpeech = value(row, pos),
                exampleSentence = value(row, example).ifBlank { null },
                synonym = value(row, synonyms).ifBlank { null },
                antonym = value(row, antonyms).ifBlank { null },
                collocation = value(row, collocations).ifBlank { null },
                examSourceRef = value(row, sourceRef).ifBlank { null },
                tags = value(row, tags)
            )
        }
    }

    private fun normalizeHeader(s: String) = s.trim().lowercase().replace('_', ' ').replace('-', ' ')
    private fun parseRows(text: String, delimiter: Char): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val cell = StringBuilder()
        var quoted = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' && quoted && i + 1 < text.length && text[i + 1] == '"' -> { cell.append('"'); i++ }
                c == '"' -> quoted = !quoted
                c == delimiter && !quoted -> { row.add(cell.toString()); cell.clear() }
                (c == '\n' || c == '\r') && !quoted -> {
                    row.add(cell.toString()); cell.clear()
                    rows.add(row); row = mutableListOf()
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                }
                else -> cell.append(c)
            }
            i++
        }
        row.add(cell.toString())
        if (row.any { it.isNotBlank() }) rows.add(row)
        if (quoted) throw IllegalArgumentException("A quoted CSV field was not closed.")
        return rows
    }
}
