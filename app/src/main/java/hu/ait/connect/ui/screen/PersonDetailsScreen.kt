package hu.ait.connect.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import hu.ait.connect.R
import hu.ait.connect.data.Person
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailsScreen(
    navController: NavHostController,
    personId: String,
    personViewModel: PersonViewModel = hiltViewModel(),
) {
    val person = personViewModel.getPersonById(personId.toInt()).collectAsState(initial = null)
    var personName by rememberSaveable { mutableStateOf("") }
    var personDescription by rememberSaveable { mutableStateOf("") }
    var cornerRadius = 20

    if (person.value == null) {
        Text(text = "Loading person details...")

    } else {
        personName = person.value!!.name
        personDescription = person.value!!.description

//    val description =
//        "The first time I met Sarah, she had an easy smile that made me feel instantly at ease. She wore a deep blue jacket that seemed to complement the calmness in her eyes, which were a striking shade of green, almost like the color of a forest after rain. Her voice was soft but confident, and she spoke with an openness that made me feel like I could talk to her for hours. I remember how she laughed at something silly I said, her laugh warm and genuine, like we had known each other for years. She had a quiet strength about her, yet there was something playful in the way she moved, as if she was always up for a bit of adventure".trimIndent()


        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Person Details",
                            style = MaterialTheme.typography.titleLarge
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
                        IconButton(onClick = { /* Handle action here */ }) {
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
                        Image(
                            painter = painterResource(R.drawable.profile_avatar),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp) // Size of the circular image
                                .clip(RoundedCornerShape(cornerRadius.dp)), // Makes the image circular
                            contentScale = ContentScale.Crop // Crop to fit inside the circle
                        )
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

                    TagArea(person.value!!.tags)

                    Text(
                        "Notes",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            color = Color.Gray
                        ),
                    )
                    Spacer(Modifier.height(5.dp))

                    PersonInfor(personViewModel, person)

//                Text("Name: $personName, Description: $personDescription Tags: ${person.value!!.tags}")

                }
            }
        )
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagArea(
    tags: Map<String, Any>?
) {
    if (tags == null || tags.isEmpty()) {
        Text("No memory cues added. Click below to add memory cues")
        return
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .height(100.dp).verticalScroll(scrollState)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
        ) {
            tags.forEach { (tag, value) ->
                if (value != "") {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                value.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                ),
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
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
        modifier = Modifier.background(MaterialTheme.colorScheme.surface).height(250.dp)
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
                    value = text ?: "",
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
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}
