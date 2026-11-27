package com.depu.vocabapp.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MarkdownVocabularyImporterTest {
    @Test
    fun exportedMarkdownRoundTripsVocabularyFields() {
        val original = VocabWord(
            word = "A|B",
            normalizedWord = "a|b",
            banglaMeaning = "অর্থ",
            englishDefinition = "First line\nsecond | line",
            partOfSpeech = "noun",
            exampleSentence = "A sentence",
            synonym = "near | same",
            antonym = "opposite",
            collocation = "strong tea",
            examSourceRef = "BCS 42",
            tags = "study, exam"
        )
        val exported = MarkdownVocabularyExporter.export(listOf(original))
        val imported = MarkdownVocabularyImporter.parse(exported).single()
        assertEquals(original.word, imported.word)
        assertEquals(original.banglaMeaning, imported.banglaMeaning)
        assertEquals(original.englishDefinition, imported.englishDefinition)
        assertEquals(original.partOfSpeech, imported.partOfSpeech)
        assertEquals(original.exampleSentence, imported.exampleSentence)
        assertEquals(original.synonym, imported.synonym)
        assertEquals(original.antonym, imported.antonym)
        assertEquals(original.collocation, imported.collocation)
        assertEquals(original.examSourceRef, imported.examSourceRef)
        assertEquals(original.tags, imported.tags)
    }

    @Test
    fun rejectsMarkdownWithoutExelixExportHeader() {
        assertThrows(IllegalArgumentException::class.java) {
            MarkdownVocabularyImporter.parse("# Other markdown\n\n## word")
        }
    }

    @Test
    fun deduplicatesEntriesByNormalizedWord() {
        val markdown = "# Exelix Vocabulary Export\n\n## apple\n- **Definition:** fruit\n\n## Apple\n- **Definition:** another\n"
        val result = MarkdownVocabularyImporter.parse(markdown)
        assertEquals(1, result.size)
        assertEquals("fruit", result.single().englishDefinition)
    }
}
