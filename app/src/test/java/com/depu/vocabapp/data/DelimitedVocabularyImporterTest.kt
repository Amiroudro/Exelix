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
        try { DelimitedVocabularyImporter.parse("Word,Meaning\\na,mis\\"quoted") ; fail("Expected parse error") }
        catch (_: IllegalArgumentException) { }
    }
    @Test fun rejectsUnclosedQuote() {
        try { DelimitedVocabularyImporter.parse("Word,Meaning\na,\"bad") ; fail("Expected parse error") }
        catch (_: IllegalArgumentException) { }
    }
}
