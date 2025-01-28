package hu.ait.connect.ui.screen.newPerson

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import hu.ait.connect.data.category.Category
import hu.ait.connect.ui.screen.AudioPlaybackUI
import hu.ait.connect.ui.screen.AudioRecordViewModel
import hu.ait.connect.ui.screen.ConfigurationViewModel
import hu.ait.connect.ui.screen.RecordingUI
import hu.ait.connect.ui.screen.camera.ComposeFileProvider
import hu.ait.connect.ui.screen.category.CategoryViewModel
import hu.ait.connect.ui.screen.components.CategoriesDropdown
import hu.ait.connect.ui.screen.person.PersonViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewPersonScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    personViewModel: PersonViewModel = hiltViewModel(),
    configurationViewModel: ConfigurationViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    audioRecordViewModel: AudioRecordViewModel = viewModel(factory = AudioRecordViewModel.factory),
) {

    var categories = categoryViewModel.getAllCategories().collectAsState(initial = emptyList())
    val configuration = configurationViewModel.getConfig().collectAsState(initial = null)
    var tagList = configuration.value?.taglist ?: emptyList()

    data class TagData(
        val label: String,
        var value: String = "",
        var isEditing: Boolean = false,
        var isSaved: Boolean = false
    )

    var tags = remember { mutableStateListOf<List<TagData>>() }

    LaunchedEffect(tagList) {
        tags.clear() // Clears the existing list
        tags.addAll(listOf(tagList.map { TagData(label = it) })) // Adds new items to the same list
    }

    Log.d("TAG", "NewPersonScreen tags: $tags")

    fun onSave(
        personViewModel: PersonViewModel,
        personName: String,
        additionalDetails: String,
        audioRecorded: Boolean,
        audioRecordViewModel: AudioRecordViewModel,
        imageUri: Uri?,
        tagValueList: List<List<TagData>>,
        selectedCategory: Category?
    ) {
        var tags: Map<String, Any>? = null

        tagValueList.forEach { tag ->
            tags = tag.filter { it.value.isNotEmpty() }.map { it.label to it.value }.toMap()
        }

        personViewModel.addPerson(
            name = personName,
            description = additionalDetails,
            categoryId = selectedCategory?.id,
            tags = tags ?: emptyMap(),
            audio = if (audioRecorded) {
                audioRecordViewModel.getAudioByteArray() // Assign audio if recorded
            } else {
                null // Provide null if audio is not recorded
            },
            imageUri = imageUri?.toString() // Save the image URI
        )
        audioRecordViewModel.stopRecording() // Stop recording when saving
        audioRecordViewModel.stopPlaying()   // Stop playback when saving
    }

    var personName by remember { mutableStateOf("") }
    var isNameValid by remember { mutableStateOf(false) }
    var additionalDetails by remember { mutableStateOf("") }
    var audioRecorded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scrollStateChips = rememberScrollState()
    val scrollStateDialog = rememberScrollState()
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

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

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Add New Person") }
                )
            }
        },
        content = { innerpadding ->
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(innerpadding),
                shape = RoundedCornerShape(size = 6.dp)
            ) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(15.dp),
                ) {
                    Column(
                        modifier = modifier
                            .padding(15.dp,0.dp)
                    )
                    {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Name") },
                            value = "$personName",
                            onValueChange = { personName = it },
                            singleLine = true,
                            isError = isNameValid && personName.isBlank(),
                            supportingText = {
                                if (isNameValid && personName.isBlank()) {
                                    Text(text = "Name is required!", color = Color.Red)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.horizontalScroll(scrollState)
                        ) {
                            tags.forEach { tagListItem ->
                                tagListItem.forEachIndexed { index, tag ->
                                    AssistChip(
                                        onClick = {
                                            val updatedList = tagListItem.toMutableList()
                                            updatedList[index] = tag.copy(isEditing = true)
                                            val indexInOuterList = tags.indexOf(tagListItem)
                                            tags[indexInOuterList] = updatedList
                                        },
                                        label = {
                                            Text(tag.label)
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Filled.Add,
                                                contentDescription = "Add $tag.label",
                                                Modifier.size(AssistChipDefaults.IconSize)
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                        }

                        tags.forEach { tagListItem ->
                            tagListItem.forEachIndexed { index, tag ->
                                if (tag.isEditing) {
                                    OutlinedTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text(tag.label) },
                                        value = tag.value,
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = {
                                            val updatedList = tagListItem.toMutableList()
                                            updatedList[index] =
                                                tag.copy(
                                                    isEditing = false,
                                                    isSaved = tag.value != ""
                                                )
                                            val indexInOuterList = tags.indexOf(tagListItem)
                                            tags[indexInOuterList] = updatedList
                                        }),
                                        onValueChange = { newValue ->
                                            val updatedList = tagListItem.toMutableList()
                                            updatedList[index] = tag.copy(value = newValue)
                                            val indexInOuterList = tags.indexOf(tagListItem)
                                            tags[indexInOuterList] = updatedList
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                val updatedList = tagListItem.toMutableList()
                                                updatedList[index] =
                                                    tag.copy(
                                                        isEditing = false,
                                                        isSaved = tag.value != ""
                                                    )
                                                val indexInOuterList = tags.indexOf(tagListItem)
                                                tags[indexInOuterList] = updatedList
                                            }) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Save"
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.horizontalScroll(scrollStateChips)
                        ) {
                            tags.flatten().filter { it.isSaved }.forEach { tag ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(tag.value) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Additional Details") },
                            value = "$additionalDetails",
                            onValueChange = { additionalDetails = it },
                            trailingIcon = {
                                Row {
                                    RecordIcon(
                                        permissionsState,
                                        audioRecordViewModel,
                                        audioRecorded
                                    )
                                    TakePicture(permissionsState, context, imageUri, cameraLauncher)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CategoriesDropdown(
                            categories,
                            preselected = "Uncategorized",
                            onSelectionChanged = { selected ->
                                Log.d("SELECTEDCAT", "NewPersonDialog: $selected")
                                selectedCategory = selected
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            categoryViewModel = categoryViewModel
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        if (audioRecorded) {
                            AudioPlaybackUI(audioRecordViewModel = audioRecordViewModel)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (hasImage && imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                modifier = Modifier.size(200.dp, 200.dp),
                                contentDescription = "Selected image",
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        Column() {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                TextButton(
                                    onClick = {
                                        audioRecordViewModel.stopRecording()
                                        audioRecordViewModel.stopPlaying()
                                        navController.popBackStack()
                                    },
                                ) {
                                    Text(
                                        "Cancel",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        if (personName.isBlank()) {
                                            isNameValid = true
                                        } else {
                                            onSave(
                                                personViewModel,
                                                personName,
                                                additionalDetails,
                                                audioRecorded,
                                                audioRecordViewModel,
                                                imageUri,
                                                tags,
                                                selectedCategory
                                            )
                                            navController.popBackStack()
                                        }
                                    },
                                ) {
                                    Text(
                                        "Save",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }


                    }
                }
            }

        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun TakePicture(
    permissionsState: MultiplePermissionsState,
    context: Context,
    imageUri: Uri?,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
) {
    var imageUri1 = imageUri
    if (permissionsState.allPermissionsGranted) {
        IconButton(
            onClick = {
                val uri = ComposeFileProvider.getImageUri(context)
                imageUri1 = uri
                cameraLauncher.launch(uri)
            }
        ) {
            Icon(
                Icons.Filled.CameraAlt,
                contentDescription = "Add image"
            )
        }

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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RecordIcon(
    permissionsState: MultiplePermissionsState,
    audioRecordViewModel: AudioRecordViewModel,
    audioRecorded: Boolean
) {
    var audioRecorded1 = audioRecorded
    if (permissionsState.allPermissionsGranted) {
        RecordingUI(audioRecordViewModel = audioRecordViewModel,
            onAudioRecorded = { audioRecorded1 = true })
    } else {
        Button(onClick = {
            permissionsState.launchMultiplePermissionRequest()
        }) {
            Text(text = "Request permissions")
        }
    }
}
