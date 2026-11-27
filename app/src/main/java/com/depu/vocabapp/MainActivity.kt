package com.depu.vocabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.depu.vocabapp.data.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

private fun readUriTextLimited(context: android.content.Context, uri: android.net.Uri, maxBytes: Int): String {
    val input = context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("Could not read selected file")
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    var total = 0
    input.use { stream ->
        while (true) {
            val count = stream.read(buffer)
            if (count < 0) break
            total += count
            if (total > maxBytes) throw IllegalArgumentException("Selected file is too large (limit ${maxBytes / 1_000_000} MB).")
            output.write(buffer, 0, count)
        }
    }
    return output.toString(Charsets.UTF_8.name())
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = Room.databaseBuilder(applicationContext, ExelixDatabase::class.java, "exelix_rebuild.db").build()
        setContent { ExelixApp(database.vocabDao()) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExelixApp(dao: VocabDao) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var backupFeedback by remember { mutableStateOf("") }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            try {
                val snapshot = dao.observeAll().first()
                val data = BackupFormat.encode(snapshot)
                val stream = context.contentResolver.openOutputStream(uri) ?: throw IllegalStateException("Could not create backup file")
                stream.use { it.write(data.toByteArray(Charsets.UTF_8)) }
                backupFeedback = "Backup saved (${snapshot.size} words)."
            } catch (error: Exception) {
                backupFeedback = "Backup failed: ${error.message ?: "unknown error"}"
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            try {
                val raw = readUriTextLimited(context, uri, 10_000_000)
                val restored = BackupFormat.decode(raw)
                val result = dao.mergeImport("JSON backup", restored)
                backupFeedback = "Restore finished: ${result.added} added, ${result.duplicates} duplicates skipped."
            } catch (error: Exception) {
                backupFeedback = "Restore failed: ${error.message ?: "invalid backup"}"
            }
        }
    }
    var pendingImport by remember { mutableStateOf<List<VocabWord>>(emptyList()) }
    var showImportPreview by remember { mutableStateOf(false) }
    val csvImportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            try {
                val raw = readUriTextLimited(context, uri, 2_000_000)
                pendingImport = DelimitedVocabularyImporter.parse(raw)
                if (pendingImport.isEmpty()) backupFeedback = "No vocabulary rows found. Check the file headers and content."
                else showImportPreview = true
            } catch (error: Exception) {
                backupFeedback = "Import failed: ${error.message ?: "invalid file"}"
            }
        }
    }
    val words by dao.observeAll().collectAsState(initial = emptyList())
    val wordCount by dao.observeCount().collectAsState(initial = 0)
    val masteredCount by dao.observeMasteredCount().collectAsState(initial = 0)
    val history by dao.observeHistory().collectAsState(initial = emptyList())
    val allReviewCount by dao.observeAllReviewCount().collectAsState(initial = 0)
    val allRememberedCount by dao.observeAllRememberedCount().collectAsState(initial = 0)
    val allReviewTimestamps by dao.observeReviewTimestamps().collectAsState(initial = emptyList())
    val importBatches by dao.observeBatches().collectAsState(initial = emptyList())
    val todayStart = remember { java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0) }.timeInMillis }
    val reviewedToday by dao.observeReviewCountSince(todayStart).collectAsState(initial = 0)
    val rememberedToday by dao.observeRememberedCountSince(todayStart).collectAsState(initial = 0)
    val weekStart = remember { java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
        add(java.util.Calendar.DAY_OF_YEAR, -6)
    }.timeInMillis }
    val reviewedThisWeek by dao.observeReviewCountSince(weekStart).collectAsState(initial = 0)
    val rememberedThisWeek by dao.observeRememberedCountSince(weekStart).collectAsState(initial = 0)
    val analyticsHistory by dao.observeHistorySince(weekStart).collectAsState(initial = emptyList())
    val weeklyRecallPercent = if (reviewedThisWeek > 0) rememberedThisWeek * 100 / reviewedThisWeek else 0
    val preferences = remember { context.getSharedPreferences("exelix_preferences", android.content.Context.MODE_PRIVATE) }
    var dailyGoal by remember { mutableIntStateOf(preferences.getInt("daily_goal", 20).coerceIn(5, 100)) }
    var themePreference by remember { mutableStateOf(preferences.getString("theme", "System") ?: "System") }
    val darkTheme = themePreference == "Dark" || (themePreference == "System" && androidx.compose.foundation.isSystemInDarkTheme())
    var tab by remember { mutableStateOf("Home") }
    var showAdd by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<VocabWord?>(null) }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var search by remember { mutableStateOf("") }
    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var pos by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var synonyms by remember { mutableStateOf("") }
    var antonyms by remember { mutableStateOf("") }
    var collocations by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var sessionReviewed by remember { mutableIntStateOf(0) }
    val due = ReviewQueue.dueFirst(words, System.currentTimeMillis())
    val filtered = words.filter { listOf(it.word, it.banglaMeaning, it.englishDefinition, it.partOfSpeech, it.tags, it.synonym.orEmpty(), it.antonym.orEmpty(), it.collocation.orEmpty(), it.examSourceRef.orEmpty()).any { value -> value.contains(search, ignoreCase = true) } }

    MaterialTheme(colorScheme = if (darkTheme) darkColorScheme(primary = Color(0xFF66C99B), secondary = Color(0xFFFF8585)) else lightColorScheme(primary = Color(0xFF176B48), secondary = Color(0xFFD62828))) {
        Scaffold(
            topBar = { TopAppBar(title = { Column { Text("Exelix"); Text("Vocabulary, built for steady progress", style = MaterialTheme.typography.bodySmall) } }) },
            bottomBar = {
                NavigationBar {
                    listOf("Home", "Library", "Review", "History", "More").forEach { name ->
                        val icon = when (name) {
                            "Home" -> Icons.Default.Home
                            "Library" -> Icons.Default.Book
                            "Review" -> Icons.Default.Check
                            "History" -> Icons.Default.History
                            else -> Icons.Default.MoreHoriz
                        }
                        NavigationBarItem(
                            selected = tab == name || (name == "More" && tab in listOf("Analytics", "Backup", "Settings")),
                            onClick = { tab = name },
                            label = { Text(name) },
                            icon = { Icon(icon, contentDescription = name) }
                        )
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 10.dp).then(if (tab in listOf("Home", "Analytics", "More", "Backup", "Settings")) Modifier.verticalScroll(rememberScrollState()) else Modifier), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (tab) {
                    "Home" -> {
                        Text("Study overview", style = MaterialTheme.typography.headlineSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("Vocabulary", wordCount.toString(), Modifier.weight(1f))
                            SummaryCard("Due now", due.size.toString(), Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("Mastered", masteredCount.toString(), Modifier.weight(1f))
                            SummaryCard("Learning", words.count { it.masteryStatus == "Learning" }.toString(), Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("Today's reviews", reviewedToday.toString(), Modifier.weight(1f))
                            SummaryCard("Remembered", rememberedToday.toString(), Modifier.weight(1f))
                        }
                        Text("This week", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("Reviews", reviewedThisWeek.toString(), Modifier.weight(1f))
                            SummaryCard("Recall", if (reviewedThisWeek == 0) "—" else "$weeklyRecallPercent%", Modifier.weight(1f))
                        }
                        Text(text = if (reviewedThisWeek == 0) { "Start a review session to build your learning insights." } else { "${rememberedThisWeek} remembered from ${reviewedThisWeek} review attempts over the last 7 days." })
                        Text("Daily goal: $reviewedToday / $dailyGoal reviews")
                        LinearProgressIndicator(progress = { (reviewedToday.toFloat() / dailyGoal).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                        Text(if (reviewedToday >= dailyGoal) "Daily goal reached!" else "Open Analytics for daily activity, streaks, and detailed learning stats.")
                        Text("Offline-first. Your vocabulary is stored in the app's local database.")
                    }
                    "Analytics" -> {
                        val dayKeys = (0..6).map { offset ->
                            java.util.Calendar.getInstance().apply {
                                set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
                                add(java.util.Calendar.DAY_OF_YEAR, -offset)
                            }.timeInMillis
                        }.reversed()
                        val dailyCounts = dayKeys.map { start ->
                            val end = start + 24L * 60 * 60 * 1000
                            analyticsHistory.count { it.reviewedAt >= start && it.reviewedAt < end }
                        }
                        val dailyRemembered = dayKeys.map { start ->
                            val end = start + 24L * 60 * 60 * 1000
                            analyticsHistory.count { it.reviewedAt >= start && it.reviewedAt < end && it.remembered }
                        }
                        val activeDays = dailyCounts.count { it > 0 }
                        val maxDaily = (dailyCounts.maxOrNull() ?: 0).coerceAtLeast(1)
                        val streak = StudyStreak.calculate(allReviewTimestamps)
                        val totalRemembered = allRememberedCount
                        val totalRecall = if (allReviewCount == 0) 0 else totalRemembered * 100 / allReviewCount
                        Text("Learning analytics", style = MaterialTheme.typography.headlineSmall)
                        Text("Insights from your saved review history", style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("All-time reviews", allReviewCount.toString(), Modifier.weight(1f))
                            SummaryCard("Overall recall", if (allReviewCount == 0) "—" else "$totalRecall%", Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("Active days (7d)", activeDays.toString(), Modifier.weight(1f))
                            SummaryCard("Current streak", "$streak day(s)", Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("7-day reviews", reviewedThisWeek.toString(), Modifier.weight(1f))
                            SummaryCard("7-day recall", if (reviewedThisWeek == 0) "—" else "$weeklyRecallPercent%", Modifier.weight(1f))
                        }
                        Text("Daily activity — last 7 days", style = MaterialTheme.typography.titleMedium)
                        dayKeys.forEachIndexed { index, start ->
                            val dateLabel = java.text.SimpleDateFormat("EEE, d", java.util.Locale.getDefault()).format(java.util.Date(start))
                            val count = dailyCounts[index]
                            val recallForDay = if (count == 0) "—" else "${dailyRemembered[index] * 100 / count}% recall"
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(dateLabel, modifier = Modifier.width(72.dp), style = MaterialTheme.typography.bodySmall)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    LinearProgressIndicator(progress = { count.toFloat() / maxDaily }, modifier = Modifier.fillMaxWidth())
                                    Text("$count reviews • $recallForDay", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        Text("Recall rate is the share of review attempts marked Remembered. It is a learning signal, not a grade.", style = MaterialTheme.typography.bodySmall)
                        if (allReviewCount == 0) Text("No review history yet. Review vocabulary to start building analytics.")
                    }
                    "More" -> {
                        Text("More tools", style = MaterialTheme.typography.headlineSmall)
                        Text("Study insights, backup, and app preferences.")
                        listOf(
                            Triple("Analytics", "Learning trends, recall, activity, and streaks.", "Analytics"),
                            Triple("Backup & transfer", "Export or restore your vocabulary backup.", "Backup"),
                            Triple("Settings", "Daily goal, appearance, and preferences.", "Settings")
                        ).forEach { (title, description, destination) ->
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(title, style = MaterialTheme.typography.titleMedium)
                                    Text(description, style = MaterialTheme.typography.bodyMedium)
                                    Button(onClick = { tab = destination }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Open $title")
                                    }
                                }
                            }
                        }
                    }
                    "Settings" -> {
                        Text("Settings & preferences", style = MaterialTheme.typography.headlineSmall)
                        Text("Personalize your study experience. Preferences are saved on this device.", style = MaterialTheme.typography.bodyMedium)
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Daily study goal", style = MaterialTheme.typography.titleMedium)
                                Text("$dailyGoal review attempts per day", style = MaterialTheme.typography.bodyMedium)
                                Slider(value = dailyGoal.toFloat(), onValueChange = { dailyGoal = (it / 5f).toInt() * 5 }, valueRange = 5f..100f, steps = 18, onValueChangeFinished = { preferences.edit().putInt("daily_goal", dailyGoal).apply() })
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("5", style = MaterialTheme.typography.labelSmall)
                                    Text("100", style = MaterialTheme.typography.labelSmall)
                                }
                                Text("Your goal tracks review attempts, not vocabulary added.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                                listOf("System", "Light", "Dark").forEach { option ->
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        RadioButton(selected = themePreference == option, onClick = {
                                            themePreference = option
                                            preferences.edit().putString("theme", option).apply()
                                        })
                                        Text(option, modifier = Modifier.padding(start = 6.dp))
                                    }
                                }
                            }
                        }
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Review schedule", style = MaterialTheme.typography.titleMedium)
                                Text("Exelix schedules remembered words using progressively longer intervals and returns words marked Again to the learning queue.")
                                Text("Current scheduler intervals are built into the app. Custom interval controls and reminders are not available yet.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Storage & privacy", style = MaterialTheme.typography.titleMedium)
                                Text("Vocabulary, review history, and preferences are stored locally on this device. The app works offline.")
                                Text("Export a backup from Backup before moving devices or reinstalling.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("Exelix • Native rebuild", style = MaterialTheme.typography.labelSmall)
                    }
                    "Library" -> {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vocabulary library", style = MaterialTheme.typography.headlineSmall)
                            Button(onClick = { editingId = null; word = ""; meaning = ""; definition = ""; pos = ""; example = ""; synonyms = ""; antonyms = ""; collocations = ""; source = ""; tags = ""; feedback = ""; showAdd = true }) { Text("Add word") }
                        }
                        OutlinedTextField(search, { search = it }, label = { Text("Search vocabulary") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Text("${filtered.size} words", style = MaterialTheme.typography.labelMedium)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filtered, key = { it.id }) { item ->
                                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.word, style = MaterialTheme.typography.titleLarge)
                                        if (item.partOfSpeech.isNotBlank()) Text(item.partOfSpeech, style = MaterialTheme.typography.labelMedium)
                                        if (item.banglaMeaning.isNotBlank()) Text(item.banglaMeaning, style = MaterialTheme.typography.bodyLarge)
                                        if (item.englishDefinition.isNotBlank()) Text(item.englishDefinition, style = MaterialTheme.typography.bodyMedium)
                                        if (!item.exampleSentence.isNullOrBlank()) Text("Example: ${item.exampleSentence}", style = MaterialTheme.typography.bodySmall)
                                        if (!item.synonym.isNullOrBlank()) Text("Synonyms: ${item.synonym}", style = MaterialTheme.typography.bodySmall)
                                        if (!item.antonym.isNullOrBlank()) Text("Antonyms: ${item.antonym}", style = MaterialTheme.typography.bodySmall)
                                        if (item.tags.isNotBlank()) Text("Tags: ${item.tags}", style = MaterialTheme.typography.labelSmall)
                                        Text(item.masteryStatus, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            TextButton(onClick = {
                                                editingId = item.id
                                                word = item.word
                                                meaning = item.banglaMeaning
                                                definition = item.englishDefinition
                                                pos = item.partOfSpeech
                                                example = item.exampleSentence.orEmpty()
                                                synonyms = item.synonym.orEmpty()
                                                antonyms = item.antonym.orEmpty()
                                                collocations = item.collocation.orEmpty()
                                                source = item.examSourceRef.orEmpty()
                                                tags = item.tags
                                                feedback = ""
                                                showAdd = true
                                            }) { Text("Edit") }
                                            TextButton(onClick = { pendingDelete = item }) { Text("Delete") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Review" -> {
                        Text("Review queue", style = MaterialTheme.typography.headlineSmall)
                        Text("${due.size} due now", style = MaterialTheme.typography.labelLarge)
                        Text("Completed this session: $sessionReviewed", style = MaterialTheme.typography.bodyMedium)
                        val sessionTotal = (sessionReviewed + due.size).coerceAtLeast(1)
                        LinearProgressIndicator(
                            progress = { (sessionReviewed.toFloat() / sessionTotal).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Session progress: $sessionReviewed / $sessionTotal", style = MaterialTheme.typography.labelSmall)
                        val current = due.firstOrNull()
                        if (current == null) Text(if (sessionReviewed > 0) "Session complete. Great work!" else "Nothing due right now. Nice work.")
                        else {
                            var revealed by remember(current.id) { mutableStateOf(false) }
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(current.word, style = MaterialTheme.typography.headlineMedium)
                                    if (current.partOfSpeech.isNotBlank()) Text(current.partOfSpeech, style = MaterialTheme.typography.labelMedium)
                                    if (revealed) {
                                        Text(current.banglaMeaning, style = MaterialTheme.typography.titleLarge)
                                        if (current.englishDefinition.isNotBlank()) Text(current.englishDefinition)
                                        if (!current.exampleSentence.isNullOrBlank()) Text(current.exampleSentence, style = MaterialTheme.typography.bodySmall)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(onClick = {
                                                scope.launch {
                                                    dao.insertHistory(StudyHistoryEvent(vocabularyId = current.id, word = current.word, remembered = false))
                                                    val result = ReviewScheduler.again()
                                                    dao.update(current.copy(correctStreak = 0, incorrectStreak = current.incorrectStreak + 1, masteryStatus = result.status, nextReviewDate = result.nextReviewAt))
                                                    sessionReviewed++
                                                }
                                            }) { Text("Again") }
                                            Button(onClick = {
                                                scope.launch {
                                                    dao.insertHistory(StudyHistoryEvent(vocabularyId = current.id, word = current.word, remembered = true))
                                                    val result = ReviewScheduler.remembered(current.correctStreak)
                                                    dao.update(current.copy(correctStreak = result.streak, incorrectStreak = 0, masteryStatus = result.status, nextReviewDate = result.nextReviewAt))
                                                    sessionReviewed++
                                                }
                                            }) { Text("Remembered") }
                                        }
                                    } else Button(onClick = { revealed = true }) { Text("Reveal answer") }
                                }
                            }
                        }
                    }
                    "History" -> {
                        Text("Study history", style = MaterialTheme.typography.headlineSmall)
                        Text("Showing the most recent 200 review events.")
                        Text("Today: $reviewedToday reviews • $rememberedToday remembered", style = MaterialTheme.typography.bodyMedium)
                        if (history.isEmpty()) Text("Your review activity will appear here after your first review.")
                        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(history, key = { it.eventId }) { event ->
                                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(event.word, style = MaterialTheme.typography.titleMedium)
                                        Text(if (event.remembered) "Remembered" else "Again", color = if (event.remembered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                        Text(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(java.util.Date(event.reviewedAt)), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Text("Backup & transfer", style = MaterialTheme.typography.headlineSmall)
                        Text("JSON backup includes vocabulary only (not review history or preferences). Restore merges into this device; existing words are kept and duplicates are skipped.")
                        Button(onClick = { exportLauncher.launch("Exelix-backup.json") }, modifier = Modifier.fillMaxWidth()) { Text("Export JSON backup") }
                        OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/json")) }, modifier = Modifier.fillMaxWidth()) { Text("Restore / merge JSON backup") }
                        OutlinedButton(onClick = { csvImportLauncher.launch(arrayOf("text/*", "application/vnd.ms-excel")) }, modifier = Modifier.fillMaxWidth()) { Text("Import CSV / TSV with preview") }
                        if (backupFeedback.isNotBlank()) Text(backupFeedback, color = MaterialTheme.colorScheme.primary)
                        Text("Recent imports", style = MaterialTheme.typography.titleMedium)
                        if (importBatches.isEmpty()) Text("Import history will appear here after your first CSV, TSV, or JSON restore.")
                        else importBatches.take(5).forEach { batch ->
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(batch.fileName, style = MaterialTheme.typography.titleSmall)
                                    Text("${batch.wordCount} words added", style = MaterialTheme.typography.bodyMedium)
                                    Text(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(java.util.Date(batch.importedAt)), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showImportPreview) AlertDialog(
        onDismissRequest = { showImportPreview = false },
        title = { Text("Import preview (${pendingImport.size} rows)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preview of the first rows. Existing vocabulary will be skipped.")
                val existingWordKeys = words.map { it.normalizedWord }.toSet()
                val alreadyInLibrary = pendingImport.count { it.normalizedWord in existingWordKeys }
                val repeatedInFile = pendingImport.groupBy { it.normalizedWord }.values.sumOf { group -> (group.size - 1).coerceAtLeast(0) }
                Text("Already in library: $alreadyInLibrary • Repeated in file: $repeatedInFile", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                pendingImport.take(5).forEach { item ->
                    Column {
                        Text(item.word, style = MaterialTheme.typography.titleMedium)
                        if (item.banglaMeaning.isNotBlank()) Text(item.banglaMeaning)
                        if (item.englishDefinition.isNotBlank()) Text(item.englishDefinition, style = MaterialTheme.typography.bodySmall)
                    }
                    Divider()
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    val result = dao.mergeImport("CSV/TSV import", pendingImport)
                    backupFeedback = "Import complete: ${result.added} added, ${result.duplicates} duplicates skipped."
                    pendingImport = emptyList()
                    showImportPreview = false
                }
            }) { Text("Import ${pendingImport.size} words") }
        },
        dismissButton = { TextButton(onClick = { showImportPreview = false }) { Text("Cancel") } }
    )
    if (pendingDelete != null) AlertDialog(
        onDismissRequest = { pendingDelete = null },
        title = { Text("Delete vocabulary?") },
        text = { Text("Delete \"${pendingDelete?.word}\" from your library? This cannot be undone.") },
        confirmButton = {
            Button(onClick = {
                val target = pendingDelete
                pendingDelete = null
                if (target != null) scope.launch { dao.delete(target) }
            }) { Text("Delete") }
        },
        dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } }
    )
    if (showAdd) AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text(if (editingId == null) "Add vocabulary" else "Edit vocabulary") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState())) {
                    OutlinedTextField(word, { word = it }, label = { Text("Word *") }, singleLine = true)
                    OutlinedTextField(meaning, { meaning = it }, label = { Text("Bangla meaning") })
                    OutlinedTextField(definition, { definition = it }, label = { Text("English definition") })
                    OutlinedTextField(pos, { pos = it }, label = { Text("Part of speech") })
                    OutlinedTextField(example, { example = it }, label = { Text("Example sentence") })
                    OutlinedTextField(synonyms, { synonyms = it }, label = { Text("Synonyms") })
                    OutlinedTextField(antonyms, { antonyms = it }, label = { Text("Antonyms") })
                    OutlinedTextField(collocations, { collocations = it }, label = { Text("Collocations") })
                    OutlinedTextField(source, { source = it }, label = { Text("Source / exam") })
                    OutlinedTextField(tags, { tags = it }, label = { Text("Tags") })
                    if (feedback.isNotBlank()) Text(feedback, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                Button(enabled = word.isNotBlank(), onClick = {
                    scope.launch {
                        val normalized = word.trim().lowercase()
                        val common = VocabWord(word = word.trim(), normalizedWord = normalized, banglaMeaning = meaning.trim(), englishDefinition = definition.trim(), partOfSpeech = pos.trim(), exampleSentence = example.trim().ifBlank { null }, synonym = synonyms.trim().ifBlank { null }, antonym = antonyms.trim().ifBlank { null }, collocation = collocations.trim().ifBlank { null }, examSourceRef = source.trim().ifBlank { null }, tags = tags.trim())
                        val currentId = editingId
                        if (currentId != null) {
                            val duplicate = words.any { it.id != currentId && it.normalizedWord == normalized }
                            val existing = words.firstOrNull { it.id == currentId }
                            if (duplicate) feedback = "Another entry already uses this word."
                            else if (existing != null) {
                                dao.update(existing.copy(word = common.word, normalizedWord = common.normalizedWord, banglaMeaning = common.banglaMeaning, englishDefinition = common.englishDefinition, partOfSpeech = common.partOfSpeech, exampleSentence = common.exampleSentence, synonym = common.synonym, antonym = common.antonym, collocation = common.collocation, examSourceRef = common.examSourceRef, tags = common.tags))
                                showAdd = false
                                editingId = null
                                feedback = ""
                            }
                        } else {
                            val id = dao.insertIgnore(common)
                            feedback = if (id == -1L) "This word already exists." else "Added."
                            if (id != -1L) { word = ""; meaning = ""; definition = ""; pos = ""; example = ""; synonyms = ""; antonyms = ""; collocations = ""; source = ""; tags = ""; showAdd = false }
                        }
                    }
                }) { Text(if (editingId == null) "Save" else "Save changes") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
