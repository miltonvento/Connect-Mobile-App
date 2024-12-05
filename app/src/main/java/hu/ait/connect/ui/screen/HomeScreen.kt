package hu.ait.connect.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.connect.data.person.Person
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import coil.compose.AsyncImage
import hu.ait.connect.ui.screen.camera.ComposeFileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PersonViewModel = hiltViewModel(),
    configurationViewModel: ConfigurationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onNavigateToPersonDetails: (String) -> Unit
) {
    val peopleList = viewModel.getAllPeople().collectAsState(emptyList())
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    val tabs = listOf("All", "Category 2", "Category 3", "Category 4", "Category 5", "Category 6")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Connect") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                )
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
                            text = { Text(tab) }
                        )
                    }
                }
            }


        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
                modifier = Modifier.padding(bottom = 72.dp)
            )
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        },
        content = { innerpadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerpadding)
            ) {

                if (peopleList.value.isEmpty()) {
                    Text(
                        "Click + to add a person", modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                } else {
                    LazyColumn {
                        items(peopleList.value) { person ->
                            PersonCard(
                                person,
                                onDeletePerson = { person ->
                                    viewModel.deletePerson(person)
                                },
                                onNavigateToPersonDetails = onNavigateToPersonDetails
                            )
                        }
                    }
                }
            }

            if (showAddDialog) {
                NewPersonDialog(
                    viewModel,
                    onCancel = {
                        showAddDialog = false
                    },
                    onSave = {
                        showAddDialog = false
                    }

                )
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewPersonDialog(
    viewModel: PersonViewModel,
    audioRecordViewModel: AudioRecordViewModel = viewModel(factory = AudioRecordViewModel.factory),
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var additionalDetails by remember { mutableStateOf("") }
    var audioRecorded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scrollStateChips = rememberScrollState()

    var showLocationTextInput by remember { mutableStateOf(false) }
    var showNationalityTextInput by remember { mutableStateOf(false) }
    var showOccupationTextInput by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var showLocationText by remember { mutableStateOf(false) }
    var showNationalityText by remember { mutableStateOf(false) }
    var showOccupationText by remember { mutableStateOf(false) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
    )

    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    Dialog(onDismissRequest = {
        onCancel()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(size = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    "Add New Person",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    value = "$personName",
                    onValueChange = { personName = it },
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.horizontalScroll(scrollState)
                ) {
                    AssistChip(
                        onClick = { showLocationTextInput = true },
                        label = { Text("Meeting Location") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Meeting Location",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AssistChip(
                        onClick = { showNationalityTextInput = true },
                        label = { Text("Nationality") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Nationality",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AssistChip(
                        onClick = { showOccupationTextInput = true },
                        label = { Text("Occupation") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Occupation",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                }

                if (showLocationTextInput) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Meeting Location") },
                        value = "$location",
                        onValueChange = { location = it },
                        trailingIcon = {
                            IconButton(onClick = {
                                showLocationText = true
                                showLocationTextInput = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        }
                    )
                }

                if (showNationalityTextInput) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nationality") },
                        value = "$nationality",
                        onValueChange = { nationality = it },
                        trailingIcon = {
                            IconButton(onClick = {
                                showNationalityText = true
                                showNationalityTextInput = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        }
                    )
                }

                if (showOccupationTextInput) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Occupation") },
                        value = "$occupation",
                        onValueChange = { occupation = it },
                        trailingIcon = {
                            IconButton(onClick = {
                                showOccupationText = true
                                showOccupationTextInput = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.horizontalScroll(scrollStateChips)
                ) {
                    if (showLocationText) {
                        AssistChip(
                            onClick = { },
                            label = { Text(location) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    if (showNationalityText) {
                        AssistChip(
                            onClick = { },
                            label = { Text(nationality) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    if (showOccupationText) {
                        AssistChip(
                            onClick = { },
                            label = { Text(occupation) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Additional Details") },
                    value = "$additionalDetails",
                    onValueChange = { additionalDetails = it },
//                    isError = personName.isBlank(),
//                    supportingText = {
//                        if (personName.isBlank()) {
//                            Text(text = "name required!", color = Color.Red)
//                        }
//                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (permissionsState.allPermissionsGranted) {
                    RecordingUI(audioRecordViewModel = audioRecordViewModel,
                        onAudioRecorded = {audioRecorded = true})
                } else {
                    Button(onClick = {
                        permissionsState.launchMultiplePermissionRequest()
                    }) {
                        Text(text = "Request permissions")
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (audioRecorded) {
                    AudioPlaybackUI(audioRecordViewModel = audioRecordViewModel)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (permissionsState.allPermissionsGranted) {
                        IconButton(
                            onClick = {
                                val uri = ComposeFileProvider.getImageUri(context)
                                imageUri = uri
                                cameraLauncher.launch(uri)
                            }
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = "Add image"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Upload Image",
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        val textToShow = if (permissionsState.shouldShowRationale) {
                            // If the user has denied the permission but the rationale can be shown,
                            // then gently explain why the app requires this permission
                            "RATIONALEXPLANATION The camera is important for this app. Please grant the permission."
                        } else {
                            // If it's the first time the user lands on this feature, or the user
                            // doesn't want to be asked again for this permission, explain that the
                            // permission is required
                            "Camera permission required for this feature to be available. " +
                                    "Please grant the permission"
                        }
                        Text(textToShow)

                        Button(onClick = {
                            permissionsState.launchMultiplePermissionRequest()
                        }) {
                            Text("Request permission")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (hasImage && imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        modifier = Modifier.size(200.dp, 200.dp),
                        contentDescription = "Selected image",
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            viewModel.addPerson(
                                Person(
                                    name = personName,
                                    description = additionalDetails,
//                                    tags = mapOf(
//                                        "Nationality" to "",
//                                        "Gender" to "Male",
//                                        "Meeting Location" to ""
//                                    )
                                    audio = if (audioRecorded) {
                                        audioRecordViewModel.getAudioByteArray() // Assign audio if recorded
                                    } else {
                                        null // Provide null if audio is not recorded
                                    },
                                    imageUri = imageUri?.toString() // Save the image URI
                                )
                            )
                            audioRecordViewModel.stopRecording() // Stop recording when saving
                            audioRecordViewModel.stopPlaying()   // Stop playback when saving
                            onSave()
                        },
//                        enabled = personName.isNotEmpty()
                    ) {
                        Text("Save")
                    }

                    TextButton(
                        onClick = {
                            audioRecordViewModel.stopRecording() // Stop recording when canceling
                            audioRecordViewModel.stopPlaying()   // Stop playback when canceling
                            onCancel()
                        },
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun PersonCard(
    person: Person,
    onDeletePerson: (Person) -> Unit,
    onNavigateToPersonDetails: (String) -> Unit,
    audioRecordViewModel: AudioRecordViewModel = viewModel(factory = AudioRecordViewModel.factory),
) {
    var personId = person.id
    var personName = person.name
    var personAudio = person.audio
    val personImageUri = person.imageUri

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                onNavigateToPersonDetails(personId.toString())
            },
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display the image if available
                personImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Person Image",
                        modifier = Modifier
                            .size(40.dp) // Adjust the size to your preference
                            .clip(CircleShape) // Optional: make the image circular
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$personName",
                    )
                    if (personAudio != null){
                        audioRecordViewModel.saveAudioFileFromByteArray(personAudio, "$personId, audio.3gp")
                        if (audioRecordViewModel.isFileExists("$personId, audio.3gp")) {
                            AudioPlaybackUI(audioRecordViewModel = audioRecordViewModel, audioFilePath = "$personId, audio.3gp")
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable {
                            onDeletePerson(person)
                        },
                        tint = Color.Red
                    )

                    IconButton(
                        onClick = {
                            onNavigateToPersonDetails(personId.toString())
                        }
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Info"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioVisualizer(amplitude: Int, modifier: Modifier = Modifier) {
    val bars = 10
    val maxAmplitude = 32767 // MediaRecorder's max amplitude value
    val normalizedAmplitude = (amplitude.toFloat() / maxAmplitude * bars).toInt()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
}

@Composable
fun RecordingUI(audioRecordViewModel: AudioRecordViewModel, onAudioRecorded: () -> Unit) {
    val amplitude by audioRecordViewModel.audioAmplitude.observeAsState(0)
    var isRecording by remember { mutableStateOf(false) }

    Row( modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
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

        Spacer(modifier = Modifier.height(16.dp))
        Column (){
            Text(
                text = if (!isRecording) "Press to Record" else "",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isRecording) {
                // Show visualizer while recording
                AudioVisualizer(amplitude = amplitude, modifier = Modifier.fillMaxWidth().height(50.dp))
            }
        }

    }
}

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
                    audioRecordViewModel.startPlaying(audioFilePath)
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
                modifier = Modifier.fillMaxWidth().height(20.dp)
            )
        }
    }
}

//@Composable
//fun CameraScreen() {
//    var hasImage by remember {
//        mutableStateOf(false)
//    }
//    var imageUri by remember {
//        mutableStateOf<Uri?>(null)
//    }
//    val context = LocalContext.current
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture(),
//        onResult = { success ->
//            hasImage = success
//        }
//    )
//}
