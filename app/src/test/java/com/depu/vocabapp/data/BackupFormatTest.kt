package com.depu.vocabapp.data

import org.junit.Assert.*
import org.junit.Test

class BackupFormatTest {
    @Test fun roundTripPreservesVocabularyFieldsAndBangla() {
        val source = VocabWord(
            word = "Resilient",
            partOfSpeech = "adjective",
            difficultyLevel = "Advanced",
            banglaMeaning = "সহনশীল",
            englishDefinition = "Able to recover quickly",
            synonym = "tough",
            antonym = "fragile",
            collocation = "resilient mindset",
            exampleSentence = "A resilient person adapts.",
            examSourceRef = "BCS",
            dateAdded = 123456L,
            masteryStatus = "Learning",
            correctStreak = 2,
            nextReviewDate = 987654L,
            incorrectStreak = 1,
            tags = "exam,important"
        )
        val restored = BackupFormat.decode(BackupFormat.encode(listOf(source))).single()
        assertEquals(source.word, restored.word)
        assertEquals(source.normalizedWord, restored.normalizedWord)
        assertEquals(source.partOfSpeech, restored.partOfSpeech)
        assertEquals(source.difficultyLevel, restored.difficultyLevel)
        assertEquals(source.banglaMeaning, restored.banglaMeaning)
        assertEquals(source.englishDefinition, restored.englishDefinition)
        assertEquals(source.synonym, restored.synonym)
        assertEquals(source.antonym, restored.antonym)
        assertEquals(source.collocation, restored.collocation)
        assertEquals(source.exampleSentence, restored.exampleSentence)
        assertEquals(source.examSourceRef, restored.examSourceRef)
        assertEquals(source.dateAdded, restored.dateAdded)
        assertEquals(source.masteryStatus, restored.masteryStatus)
        assertEquals(source.correctStreak, restored.correctStreak)
        assertEquals(source.nextReviewDate, restored.nextReviewDate)
        assertEquals(source.incorrectStreak, restored.incorrectStreak)
        assertEquals(source.tags, restored.tags)
    }

    @Test fun decodeSkipsBlankWordRowsAndNormalizesWord() {
        val json = """{"words":[{"word":"  Mixed CASE  "},{"word":" "},{"word":"Bangla"}]}"""
        val decoded = BackupFormat.decode(json)
        assertEquals(2, decoded.size)
        assertEquals("Mixed CASE", decoded[0].word)
        assertEquals("mixed case", decoded[0].normalizedWord)
        assertEquals("Bangla", decoded[1].word)
    }

    @Test fun decodeRejectsMissingWordsArray() {
        try {
            BackupFormat.decode("""{"format":"other"}""")
            fail("Expected missing words array to be rejected")
        } catch (_: IllegalArgumentException) { }
    }

    @Test fun nullOptionalFieldsRemainNull() {
        val decoded = BackupFormat.decode(BackupFormat.encode(listOf(VocabWord(word = "simple")))).single()
        assertNull(decoded.synonym)
        assertNull(decoded.antonym)
        assertNull(decoded.collocation)
        assertNull(decoded.exampleSentence)
        assertNull(decoded.examSourceRef)
        assertNull(decoded.nextReviewDate)
    }
    @Test fun rejectsUnsupportedBackupVersion() {
        try {
            BackupFormat.decode("""{"format":"Exelix Android backup","version":2,"words":[]}""")
            fail("Expected unsupported version to be rejected")
        } catch (_: IllegalArgumentException) { }
    }

    @Test fun rejectsForeignBackupFormat() {
        try {
            BackupFormat.decode("""{"format":"Other app backup","version":1,"words":[]}""")
            fail("Expected foreign backup format to be rejected")
        } catch (_: IllegalArgumentException) { }
    }

    @Test fun rejectsBackupOverSizeLimit() {
        try {
            BackupFormat.decode("""{"words":[]}""" + " ".repeat(10_000_001))
            fail("Expected oversized backup to be rejected")
        } catch (_: IllegalArgumentException) { }
    }

}
