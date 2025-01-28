package hu.ait.connect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextField
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import hu.ait.connect.ui.screen.category.CategoryViewModel
import hu.ait.connect.ui.screen.components.ListViewComponent
import hu.ait.connect.ui.screen.person.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PersonViewModel = hiltViewModel(),
    configurationViewModel: ConfigurationViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onNavigateToPersonDetails: (String) -> Unit,
    navController: NavHostController
) {
    val peopleList = viewModel.getAllPeople().collectAsState(emptyList())
//    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    val configuration = configurationViewModel.getConfig().collectAsState(initial = null)
//    var tagList = configuration.value?.taglist

    var categories = categoryViewModel.getAllCategories().collectAsState(initial = emptyList())
    var categoryNames = categories.value.map { it.name }
    val tabs = listOf("All") + categoryNames
    var selectedTabIndex by remember { mutableStateOf(0) }

    var isSearching by rememberSaveable { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Connect")
                        if (isSearching) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = { Text("Search...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        RoundedCornerShape(4.dp)
                                    ),
                                shape = RoundedCornerShape(24.dp),
                            )
                        } else {
                            Text("Connect")
                        }
                            },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    actions = {
                        IconButton(onClick = {
                            isSearching = !isSearching
                            if (!isSearching) viewModel.updateSearchQuery("")
                        }) {
                            Icon(
                                imageVector = if (isSearching) Icons.Filled.Close else Icons.Filled.Search,
                                contentDescription = if (isSearching) "Close Search" else "Search"
                            )
                        }
                    }
                )
                if (!isSearching) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 16.dp,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(tab.trim()) }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("newperson")
                },
            )
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        },
        content = { innerpadding ->

            val peopleToDisplay = remember(peopleList.value, searchQuery, selectedTabIndex) {
                if (isSearching) {
                    peopleList.value.filter {
                        val matchesCategory = if (selectedTabIndex == 0) true else it.categoryId == categories.value[selectedTabIndex - 1].id
                        val matchesSearch = searchQuery.isBlank() ||
                                it.name.contains(searchQuery, true) ||
                                it.description.contains(searchQuery, true) ||
                                it.tags?.values?.any { value ->
                                    value.toString().contains(searchQuery, true)
                                } ?: false
                        matchesCategory && matchesSearch
                    }
                } else {
                    peopleList.value.filter {
                        if (selectedTabIndex == 0) true else it.categoryId == categories.value[selectedTabIndex - 1].id
                    }
                }
            }

            LazyColumn(
                contentPadding = innerpadding,
                modifier = modifier.padding(8.dp)) {
                if (peopleToDisplay.isEmpty()) {
                    item {
                        Text(
                            if (isSearching) "No results found" else "Click + to add a person",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                } else {
                    itemsIndexed(peopleToDisplay) {index, person ->
                        ListViewComponent(
                            person = person,
                            onDeletePerson = { person ->
                                    viewModel.deletePerson(person)
                                },
                            categoryColor = Color.Transparent.toArgb(),
                            onNavigateToPersonDetails = onNavigateToPersonDetails
                        )

                        if (index < peopleToDisplay.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }

                    }
                }
            }

        }
    )
}

@Composable
fun AudioVisualizer(amplitude: Int, modifier: Modifier = Modifier) {
    val bars = 10
    val maxAmplitude = 32767 // MediaRecorder's max amplitude value
    val normalizedAmplitude = (amplitude.toFloat() / maxAmplitude * bars).toInt()

        for (i in 1..bars) {
            val heightFactor = if (i <= normalizedAmplitude) i.toFloat() / bars else 0.2f
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height((30.dp * heightFactor).coerceAtLeast(5.dp))
                    .background(
                        color = if (i <= normalizedAmplitude) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }

}

@Composable
fun RecordingUI(audioRecordViewModel: AudioRecordViewModel, onAudioRecorded: () -> Unit) {
    val amplitude by audioRecordViewModel.audioAmplitude.observeAsState(0)
    var isRecording by remember { mutableStateOf(false) }

        IconToggleButton(
            checked = isRecording,
            onCheckedChange = { checked ->
                isRecording = checked
                if (checked) {
                    audioRecordViewModel.startRecording()
                } else {
                    audioRecordViewModel.stopRecording()
                    onAudioRecorded()
                }
            }
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }

            if (isRecording) {
                // Show visualizer while recording
                AudioVisualizer(
                    amplitude = amplitude,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
}

//@Composable
//fun RecordingUI(audioRecordViewModel: AudioRecordViewModel, onAudioRecorded: () -> Unit) {
//    val amplitude by audioRecordViewModel.audioAmplitude.observeAsState(0)
//    var isRecording by remember { mutableStateOf(false) }
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconToggleButton(
//            checked = isRecording,
//            onCheckedChange = { checked ->
//                isRecording = checked
//                if (checked) {
//                    audioRecordViewModel.startRecording()
//                } else {
//                    audioRecordViewModel.stopRecording()
//                    onAudioRecorded()
//                }
//            }
//        ) {
//            Icon(
//                imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
//                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
//                modifier = Modifier.size(ButtonDefaults.IconSize)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Column() {
//            Text(
//                text = if (!isRecording) "Press to Record" else "",
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (isRecording) {
//                // Show visualizer while recording
//                AudioVisualizer(
//                    amplitude = amplitude,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp)
//                )
//            }
//        }
//    }
//}


//@Composable
//fun AudioVisualizer(amplitude: Int, modifier: Modifier = Modifier) {
//    val bars = 10
//    val maxAmplitude = 32767 // MediaRecorder's max amplitude value
//    val normalizedAmplitude = (amplitude.toFloat() / maxAmplitude * bars).toInt()
//
//    Row(
//        modifier = modifier,
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        for (i in 1..bars) {
//            val heightFactor = if (i <= normalizedAmplitude) i.toFloat() / bars else 0.2f
//            Box(
//                modifier = Modifier
//                    .width(8.dp)
//                    .height((30.dp * heightFactor).coerceAtLeast(5.dp))
//                    .background(
//                        color = if (i <= normalizedAmplitude) MaterialTheme.colorScheme.primary
//                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
//                        shape = RoundedCornerShape(50)
//                    )
//            )
//        }
//    }
//}

@Composable
fun AudioPlaybackVisualizer(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun AudioPlaybackUI(audioRecordViewModel: AudioRecordViewModel, audioFilePath: String? = null) {
    val playbackProgress by audioRecordViewModel.playbackProgress.observeAsState(0f)
    val playbackComplete by audioRecordViewModel.playbackComplete.observeAsState(false)
    var isPlaying by remember { mutableStateOf(false) }

    if (playbackComplete && isPlaying) {
        // Reset UI when playback completes
        isPlaying = false
        audioRecordViewModel.stopPlaying() // Ensure cleanup
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconToggleButton(
            checked = isPlaying,
            onCheckedChange = { checked ->
                isPlaying = checked
                if (checked) {
                    if (!audioFilePath.isNullOrEmpty() && audioRecordViewModel.isFileExists(audioFilePath)) {
                        audioRecordViewModel.startPlaying(audioFilePath)
                    } else {
                        audioRecordViewModel.startPlaying("audiorecordtest.3gp")
                    }
                } else {
                    audioRecordViewModel.stopPlaying()
                }
            }
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Stop Playing" else "Start Playing",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isPlaying) {
            AudioPlaybackVisualizer(
                progress = playbackProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
        }
    }
}
