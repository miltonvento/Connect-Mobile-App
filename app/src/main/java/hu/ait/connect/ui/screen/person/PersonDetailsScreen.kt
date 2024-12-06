package hu.ait.connect.ui.screen.person

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import hu.ait.connect.R
import hu.ait.connect.data.person.Person
import hu.ait.connect.ui.screen.AudioPlaybackUI
import hu.ait.connect.ui.screen.AudioRecordViewModel
import hu.ait.connect.ui.screen.ConfigurationViewModel
import hu.ait.connect.ui.screen.TagArea
import hu.ait.connect.ui.screen.category.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailsScreen(
    navController: NavHostController,
    personId: String,
    personViewModel: PersonViewModel = hiltViewModel(),
    configurationViewModel: ConfigurationViewModel = hiltViewModel(),
    audioRecordViewModel: AudioRecordViewModel = viewModel(factory = AudioRecordViewModel.factory)
) {
    val configuration = configurationViewModel.getConfig().collectAsState(initial = null)
    val person = personViewModel.getPersonById(personId.toInt()).collectAsState(initial = null)
    var personName by rememberSaveable { mutableStateOf("") }
    var personImageUri by rememberSaveable { mutableStateOf<String?>(null) }
    var personDescription by rememberSaveable { mutableStateOf("") }
    var personAudio by rememberSaveable { mutableStateOf(ByteArray(0)) }
    var cornerRadius = 20

    if (person.value == null) {
        Text(text = "Loading person details...")

    } else {
        personName = person.value!!.name
        personDescription = person.value!!.description
        personAudio = person.value!!.audio!!
        personImageUri = person.value!!.imageUri

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        // Content placed in the top-right corner
                        IconButton(onClick = {
                            personViewModel.deletePerson(person.value!!)
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                )

            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        personImageUri?.let { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Person Image",
                                modifier = Modifier
                                    .size(100.dp) // Size of the circular image
                                    .clip(RoundedCornerShape(cornerRadius.dp)), // Makes the image circular
                                contentScale = ContentScale.Crop // Crop to fit inside the circle
                            )
                        } ?: run {
                            Image(
                                painter = painterResource(R.drawable.profile_avatar),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp) // Size of the circular image
                                    .clip(RoundedCornerShape(cornerRadius.dp)), // Makes the image circular
                                contentScale = ContentScale.Crop // Crop to fit inside the circle
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp)) // Space between image and text
                        Text(
                            text = "$personName",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 42.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Memory Cues",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            color = Color.Gray
                        ),
                    )
                    Spacer(Modifier.height(5.dp))

                    person.value!!.tags ?.let {
                        TagArea(
                            person.value!!.tags,
                            configuration.value?.taglist,
                        )
                    } ?: run {
                        Text("No memory cues added", fontStyle = FontStyle.Italic)
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Notes",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            color = Color.Gray
                        ),
                    )
                    Spacer(Modifier.height(5.dp))

                    if (personAudio != null){
                        audioRecordViewModel.saveAudioFileFromByteArray(personAudio, "$personId, audio.3gp")
                        if (audioRecordViewModel.isFileExists("$personId, audio.3gp")) {
                            AudioPlaybackUI(audioRecordViewModel = audioRecordViewModel, audioFilePath = "$personId, audio.3gp")
                        }
                    }

                    PersonInfor(personViewModel, person)
//                    Text(person.value.toString())

                }
            }
        )
    }

}

@Composable
fun PersonInfor(
    personViewModel: PersonViewModel,
    person: State<Person?>
) {
    EditableText(personViewModel, person.value!!)
}


@Composable
fun EditableText(
    personViewModel: PersonViewModel = hiltViewModel(),
    personToEdit: Person
) {
    // State to track the current text and edit mode
    var text by remember { mutableStateOf(personToEdit.description) }
    var isEditing by remember { mutableStateOf(false) }

    fun onSave(newText: String) {
        val EditedPerson = personToEdit?.copy(
            description = newText
        )
        if (EditedPerson != null) {
            personViewModel.editPerson(EditedPerson)
        }
    }

    Card(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .height(250.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { isEditing = true } // Toggle to edit mode on click
        ) {
            if (isEditing) {
                // Editable text field
                BasicTextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    singleLine = false,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),

                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        isEditing = false
                        onSave(text) // Save text on pressing Done
                    }
                    ))
            } else {

                Text(
                    text = if (text == "") "Tap to add notes..." else text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = if (text == "") FontStyle.Italic else FontStyle.Normal
                    ),
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}
