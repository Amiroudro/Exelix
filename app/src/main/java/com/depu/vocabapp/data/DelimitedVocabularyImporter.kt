package com.depu.vocabapp.data

/** CSV/TSV vocabulary parser with quoted fields and flexible column aliases. */
object DelimitedVocabularyImporter {
    fun parse(text: String): List<VocabWord> {
        val source = text.trimStart('\uFEFF').trim()
        if (source.length > 2_000_000) throw IllegalArgumentException("Import file is too large (maximum 2 MB of text).")
        if (source.isBlank()) return emptyList()
        val firstLine = source.lineSequence().firstOrNull().orEmpty()
        val tabCount = firstLine.count { it == '\t' }
        val commaCount = firstLine.count { it == ',' }
        val delimiter = if (tabCount > commaCount) '\t' else ','
        val rows = parseRows(source, delimiter).filter { row -> row.any { it.isNotBlank() } }
        if (rows.size > 10_001) throw IllegalArgumentException("Import contains too many rows (maximum 10,000 vocabulary entries).")
        if (rows.size < 2) return emptyList()
        val headers = rows.first().map { normalizeHeader(it) }
        rows.drop(1).forEachIndexed { index, row ->
            if (row.size > headers.size && row.drop(headers.size).any { it.isNotBlank() }) {
                throw IllegalArgumentException("Row ${index + 2} has more columns than the header. Check the delimiter or quoting.")
            }
        }
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
        var afterQuote = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                quoted && c == '"' && i + 1 < text.length && text[i + 1] == '"' -> {
                    cell.append('"')
                    i++
                }
                quoted && c == '"' -> {
                    quoted = false
                    afterQuote = true
                }
                !quoted && c == '"' && cell.isEmpty() && !afterQuote -> quoted = true
                !quoted && c == '"' && !cell.isEmpty() -> throw IllegalArgumentException("A quote must begin a quoted field.")
                !quoted && c == delimiter -> {
                    row.add(cell.toString())
                    cell.clear()
                    afterQuote = false
                }
                !quoted && (c == '\\n' || c == '\\r') -> {
                    row.add(cell.toString())
                    cell.clear()
                    rows.add(row)
                    row = mutableListOf()
                    afterQuote = false
                    if (c == '\\r' && i + 1 < text.length && text[i + 1] == '\\n') i++
                }
                !quoted && afterQuote && !c.isWhitespace() -> throw IllegalArgumentException("Unexpected character after a quoted field.")
                else -> cell.append(c)
            }
            i++
        }
        if (quoted) throw IllegalArgumentException("A quoted CSV field was not closed.")
        row.add(cell.toString())
        if (row.any { it.isNotBlank() }) rows.add(row)
        return rows
    }
}