package com.depu.vocabapp.data

import org.junit.Assert.*
import org.junit.Test

class DelimitedVocabularyImporterTest {
    @Test fun parsesHeaderAndQuotedComma() {
        val parsed = DelimitedVocabularyImporter.parse("Word,Meaning,Example\nhello,সালাম,\"Hello, world!\"")
        assertEquals(1, parsed.size)
        assertEquals("hello", parsed[0].word)
        assertEquals("সালাম", parsed[0].banglaMeaning)
        assertEquals("Hello, world!", parsed[0].exampleSentence)
    }
    @Test fun supportsTabSeparatedAndAliases() {
        val parsed = DelimitedVocabularyImporter.parse("term\tবাংলা\tPOS\nresilient\tদৃঢ়\tadj")
        assertEquals("resilient", parsed[0].word)
        assertEquals("দৃঢ়", parsed[0].banglaMeaning)
        assertEquals("adj", parsed[0].partOfSpeech)
    }
    @Test fun skipsBlankWordRows() {
        assertEquals(1, DelimitedVocabularyImporter.parse("Word,Meaning\na,one\n,empty").size)
    }
    @Test fun rejectsQuoteInsideUnquotedField() {
        try { DelimitedVocabularyImporter.parse("""Word,Meaning
a,mis"quoted""") ; fail("Expected parse error") }
        catch (_: IllegalArgumentException) { }
    }
    @Test fun rejectsImportOverTextSizeLimit() {
        try { DelimitedVocabularyImporter.parse("Word\n" + "a".repeat(2_000_001)); fail("Expected size limit") }
        catch (_: IllegalArgumentException) { }
    }
    @Test fun rejectsImportOverRowLimit() {
        val rows = buildString {
            append("Word\n")
            repeat(10_001) { append("word").append(it).append('\n') }
        }
        try { DelimitedVocabularyImporter.parse(rows); fail("Expected row limit") }
        catch (_: IllegalArgumentException) { }
    }
    @Test fun rejectsUnclosedQuote() {
        try { DelimitedVocabularyImporter.parse("Word,Meaning\na,\"bad") ; fail("Expected parse error") }
        catch (_: IllegalArgumentException) { }
    }
    @Test fun rejectsExtraNonblankColumns() {
        try { DelimitedVocabularyImporter.parse("Word,Meaning\na,one,unexpected"); fail("Expected column mismatch") }
        catch (_: IllegalArgumentException) { }
    }
    @Test fun allowsBlankTrailingColumns() {
        val parsed = DelimitedVocabularyImporter.parse("Word,Meaning\na,one,")
        assertEquals(1, parsed.size)
    }
    @Test fun delimiterDetectionIgnoresCommasInsideQuotedHeader() {
        val parsed = DelimitedVocabularyImporter.parse("Word,\"Meaning, context\"\nterm,\"meaning with, commas\"")
        assertEquals(1, parsed.size)
        assertEquals("meaning with, commas", parsed[0].banglaMeaning)
    }
    @Test fun supportsEscapedQuotesAndNewlinesInsideQuotedFields() {
        val parsed = DelimitedVocabularyImporter.parse("Word,Meaning,Example\nterm,meaning,\"A \"\"quoted\"\" line\ncontinues\"")
        assertEquals("A \"quoted\" line\ncontinues", parsed[0].exampleSentence)
    }

}
