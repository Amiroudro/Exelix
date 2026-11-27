package com.depu.vocabapp.data

import org.json.JSONArray
import org.json.JSONObject

/** Portable backup format. Version 1 backups containing only vocabulary remain supported. */
data class BackupHistoryEntry(val word: String, val remembered: Boolean, val reviewedAt: Long)
data class BackupSnapshot(val words: List<VocabWord>, val history: List<BackupHistoryEntry>, val dailyGoal: Int = 20, val theme: String = "System")

object BackupFormat {
    fun encode(words: List<VocabWord>, history: List<StudyHistoryEvent> = emptyList(), dailyGoal: Int = 20, theme: String = "System"): String {
        val items = JSONArray()
        words.forEach { word ->
            items.put(JSONObject().apply {
                put("word", word.word); put("partOfSpeech", word.partOfSpeech); put("difficultyLevel", word.difficultyLevel)
                put("banglaMeaning", word.banglaMeaning); put("englishDefinition", word.englishDefinition)
                put("synonym", word.synonym ?: JSONObject.NULL); put("antonym", word.antonym ?: JSONObject.NULL)
                put("collocation", word.collocation ?: JSONObject.NULL); put("exampleSentence", word.exampleSentence ?: JSONObject.NULL)
                put("examSourceRef", word.examSourceRef ?: JSONObject.NULL); put("dateAdded", word.dateAdded)
                put("masteryStatus", word.masteryStatus); put("correctStreak", word.correctStreak)
                put("nextReviewDate", word.nextReviewDate ?: JSONObject.NULL); put("incorrectStreak", word.incorrectStreak)
                put("normalizedWord", word.normalizedWord); put("tags", word.tags)
            })
        }
        val historyItems = JSONArray()
        history.forEach { event ->
            historyItems.put(JSONObject().apply { put("word", event.word); put("remembered", event.remembered); put("reviewedAt", event.reviewedAt) })
        }
        val safeTheme = theme.takeIf { it in listOf("System", "Light", "Dark") } ?: "System"
        return JSONObject().put("format", "Exelix Android backup").put("version", 2).put("exportedAt", System.currentTimeMillis())
            .put("words", items).put("history", historyItems)
            .put("settings", JSONObject().put("dailyGoal", dailyGoal.coerceIn(5, 100)).put("theme", safeTheme)).toString(2)
    }

    fun decodeSnapshot(json: String): BackupSnapshot {
        if (json.length > 10_000_000) throw IllegalArgumentException("Backup is too large (maximum 10 MB of text).")
        val root = JSONObject(json)
        val format = root.optString("format", "")
        if (format.isNotBlank() && format != "Exelix Android backup") throw IllegalArgumentException("This is not an Exelix backup.")
        val version = root.optInt("version", 1)
        if (version !in 1..2) throw IllegalArgumentException("Unsupported backup version: $version")
        val array = root.optJSONArray("words") ?: throw IllegalArgumentException("Backup has no words array")
        if (array.length() > 100_000) throw IllegalArgumentException("Backup contains too many vocabulary entries.")
        val output = mutableListOf<VocabWord>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val word = item.optString("word").trim()
            if (word.isEmpty()) continue
            output += VocabWord(
                word = word, normalizedWord = word.lowercase(), partOfSpeech = item.optString("partOfSpeech", ""),
                difficultyLevel = item.optString("difficultyLevel", "Unknown"), banglaMeaning = item.optString("banglaMeaning", ""),
                englishDefinition = item.optString("englishDefinition", ""),
                synonym = item.optString("synonym").takeIf { it.isNotBlank() && it != "null" },
                antonym = item.optString("antonym").takeIf { it.isNotBlank() && it != "null" },
                collocation = item.optString("collocation").takeIf { it.isNotBlank() && it != "null" },
                exampleSentence = item.optString("exampleSentence").takeIf { it.isNotBlank() && it != "null" },
                examSourceRef = item.optString("examSourceRef").takeIf { it.isNotBlank() && it != "null" },
                dateAdded = item.optLong("dateAdded", System.currentTimeMillis()), masteryStatus = item.optString("masteryStatus", "New"),
                correctStreak = item.optInt("correctStreak", 0).coerceAtLeast(0),
                nextReviewDate = item.optLong("nextReviewDate").takeIf { it > 0 },
                incorrectStreak = item.optInt("incorrectStreak", 0).coerceAtLeast(0), tags = item.optString("tags", "")
            )
        }
        val historyOutput = mutableListOf<BackupHistoryEntry>()
        val historyArray = root.optJSONArray("history") ?: JSONArray()
        if (historyArray.length() > 500_000) throw IllegalArgumentException("Backup contains too many review events.")
        for (i in 0 until historyArray.length()) {
            val event = historyArray.optJSONObject(i) ?: continue
            val eventWord = event.optString("word").trim()
            val reviewedAt = event.optLong("reviewedAt", 0L)
            if (eventWord.isNotEmpty() && reviewedAt > 0L) historyOutput += BackupHistoryEntry(eventWord, event.optBoolean("remembered", false), reviewedAt)
        }
        val settings = root.optJSONObject("settings") ?: JSONObject()
        val goal = settings.optInt("dailyGoal", 20).coerceIn(5, 100)
        val theme = settings.optString("theme", "System").takeIf { it in listOf("System", "Light", "Dark") } ?: "System"
        return BackupSnapshot(output, historyOutput, goal, theme)
    }
    fun decode(json: String): List<VocabWord> = decodeSnapshot(json).words
}
