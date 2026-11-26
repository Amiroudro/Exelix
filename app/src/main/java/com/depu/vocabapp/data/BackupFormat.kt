package com.depu.vocabapp.data

import org.json.JSONArray
import org.json.JSONObject

/** Portable JSON backup format for the reconstructed app. */
object BackupFormat {
    fun encode(words: List<VocabWord>): String {
        val items = JSONArray()
        words.forEach { word ->
            items.put(JSONObject().apply {
                put("word", word.word)
                put("partOfSpeech", word.partOfSpeech)
                put("difficultyLevel", word.difficultyLevel)
                put("banglaMeaning", word.banglaMeaning)
                put("englishDefinition", word.englishDefinition)
                put("synonym", word.synonym ?: JSONObject.NULL)
                put("antonym", word.antonym ?: JSONObject.NULL)
                put("collocation", word.collocation ?: JSONObject.NULL)
                put("exampleSentence", word.exampleSentence ?: JSONObject.NULL)
                put("examSourceRef", word.examSourceRef ?: JSONObject.NULL)
                put("dateAdded", word.dateAdded)
                put("masteryStatus", word.masteryStatus)
                put("correctStreak", word.correctStreak)
                put("nextReviewDate", word.nextReviewDate ?: JSONObject.NULL)
                put("incorrectStreak", word.incorrectStreak)
                put("normalizedWord", word.normalizedWord)
                put("tags", word.tags)
            })
        }
        return JSONObject()
            .put("format", "Exelix Android backup")
            .put("version", 1)
            .put("exportedAt", System.currentTimeMillis())
            .put("words", items)
            .toString(2)
    }

    fun decode(json: String): List<VocabWord> {
        val root = JSONObject(json)
        val array = root.optJSONArray("words") ?: throw IllegalArgumentException("Backup has no words array")
        val output = mutableListOf<VocabWord>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val word = item.optString("word").trim()
            if (word.isEmpty()) continue
            output += VocabWord(
                word = word,
                normalizedWord = item.optString("normalizedWord", word.trim().lowercase()).ifBlank { word.trim().lowercase() },
                partOfSpeech = item.optString("partOfSpeech", ""),
                difficultyLevel = item.optString("difficultyLevel", "Unknown"),
                banglaMeaning = item.optString("banglaMeaning", ""),
                englishDefinition = item.optString("englishDefinition", ""),
                synonym = item.optString("synonym").takeIf { it.isNotBlank() && it != "null" },
                antonym = item.optString("antonym").takeIf { it.isNotBlank() && it != "null" },
                collocation = item.optString("collocation").takeIf { it.isNotBlank() && it != "null" },
                exampleSentence = item.optString("exampleSentence").takeIf { it.isNotBlank() && it != "null" },
                examSourceRef = item.optString("examSourceRef").takeIf { it.isNotBlank() && it != "null" },
                dateAdded = item.optLong("dateAdded", System.currentTimeMillis()),
                masteryStatus = item.optString("masteryStatus", "New"),
                correctStreak = item.optInt("correctStreak", 0),
                nextReviewDate = item.optLong("nextReviewDate").takeIf { it > 0 },
                incorrectStreak = item.optInt("incorrectStreak", 0),
                tags = item.optString("tags", "")
            )
        }
        return output
    }
}
