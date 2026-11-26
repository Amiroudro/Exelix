package com.depu.vocabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.depu.vocabapp.data.ExelixDatabase
import com.depu.vocabapp.data.WordEntity
import com.depu.vocabapp.data.WordRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, ExelixDatabase::class.java, "exelix.db").build()
        val repository = WordRepository(db.wordDao())
        setContent { ExelixApp(repository) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExelixApp(repository: WordRepository) {
    val scope = rememberCoroutineScope()
    val words by repository.words.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf("Library") }
    var showAdd by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var newWord by remember { mutableStateOf("") }
    var newMeaning by remember { mutableStateOf("") }
    var newExample by remember { mutableStateOf("") }
    val dueCount = words.count { it.nextReviewAt <= System.currentTimeMillis() || it.nextReviewAt == 0L }
    val filtered = words.filter { it.word.contains(search, true) || it.meaning.contains(search, true) }

    MaterialTheme(colorScheme = lightColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF176B48), secondary = androidx.compose.ui.graphics.Color(0xFFD62828))) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Exelix", style = MaterialTheme.typography.titleLarge) }, subtitle = { Text("Vocabulary, built for steady progress") }) },
            bottomBar = {
                NavigationBar {
                    listOf("Home", "Library", "Review", "Backup").forEach { tab ->
                        NavigationBarItem(selected = selectedTab == tab, onClick = { selectedTab = tab }, label = { Text(tab) }, icon = { Text(when(tab) { "Home" -> "⌂"; "Library" -> "▤"; "Review" -> "✓"; else -> "⇧" }) })
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (selectedTab) {
                    "Home" -> {
                        Text("Your study overview", style = MaterialTheme.typography.headlineSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SummaryCard("Vocabulary", words.size.toString(), Modifier.weight(1f))
                            SummaryCard("Due", dueCount.toString(), Modifier.weight(1f))
                        }
                        SummaryCard("Learning", words.count { it.status == "Learning" }.toString(), Modifier.fillMaxWidth())
                        Text("Your vocabulary is stored locally on this device.", style = MaterialTheme.typography.bodyMedium)
                    }
                    "Library" -> {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vocabulary library", style = MaterialTheme.typography.headlineSmall)
                            Button(onClick = { showAdd = true }) { Text("Add") }
                        }
                        OutlinedTextField(search, { search = it }, label = { Text("Search words") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filtered, key = { it.id }) { item ->
                                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.word, style = MaterialTheme.typography.titleMedium)
                                        Text(item.meaning, style = MaterialTheme.typography.bodyLarge)
                                        if (item.example.isNotBlank()) Text(item.example, style = MaterialTheme.typography.bodySmall)
                                        Text(item.status, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                        TextButton(onClick = { scope.launch { repository.delete(item.id) } }) { Text("Remove") }
                                    }
                                }
                            }
                        }
                    }
                    "Review" -> {
                        Text("Review queue", style = MaterialTheme.typography.headlineSmall)
                        val due = words.filter { it.nextReviewAt <= System.currentTimeMillis() || it.nextReviewAt == 0L }
                        if (due.isEmpty()) Text("Nothing due right now. Nice work.")
                        else {
                            var reveal by remember { mutableStateOf(false) }
                            val current = due.firstOrNull()
                            current?.let {
                                Card(Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(it.word, style = MaterialTheme.typography.headlineMedium)
                                        if (reveal) Text(it.meaning, style = MaterialTheme.typography.titleLarge)
                                        Button(onClick = { reveal = !reveal }) { Text(if (reveal) "Hide meaning" else "Reveal meaning") }
                                        if (reveal) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(onClick = { scope.launch { repository.update(it.copy(streak = 0, status = "Learning", nextReviewAt = System.currentTimeMillis() + 10 * 60_000)); reveal = false } }) { Text("Again") }
                                                Button(onClick = { scope.launch { val streak = it.streak + 1; val days = listOf(1, 3, 7, 14, 30)[(streak - 1).coerceIn(0, 4)]; repository.update(it.copy(streak = streak, status = if (streak >= 5) "Mastered" else if (streak >= 2) "Learning" else "New", nextReviewAt = System.currentTimeMillis() + days * 24 * 60 * 60_000L)); index = (index + 1).coerceAtMost(due.lastIndex); reveal = false } }) { Text("Remembered") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Text("Backup & transfer", style = MaterialTheme.typography.headlineSmall)
                        Text("Backup and restore tools will be built next. Your words currently stay in the local database.")
                    }
                }
            }
        }
        if (showAdd) AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Add vocabulary") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(newWord, { newWord = it }, label = { Text("Word") }, singleLine = true)
                    OutlinedTextField(newMeaning, { newMeaning = it }, label = { Text("Meaning") })
                    OutlinedTextField(newExample, { newExample = it }, label = { Text("Example") })
                }
            },
            confirmButton = {
                Button(enabled = newWord.isNotBlank() && newMeaning.isNotBlank(), onClick = {
                    scope.launch { repository.add(WordEntity(word = newWord.trim(), meaning = newMeaning.trim(), example = newExample.trim())); newWord = ""; newMeaning = ""; newExample = ""; showAdd = false }
                }) { Text("Save") }
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
