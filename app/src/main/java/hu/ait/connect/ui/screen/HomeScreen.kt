package hu.ait.connect.ui.screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import hu.ait.connect.data.Person
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.font.FontStyle
import hu.ait.connect.data.Configuration

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connect") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
            )
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
                NewPersonDialog(viewModel,
                    onCancel = {
                        showAddDialog = false
                    }
                )
            }
        }
    )
}

@Composable
fun NewPersonDialog(
    viewModel: PersonViewModel,
    onCancel: () -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var additionalDetails by remember { mutableStateOf("") }
    var recordButtonChecked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
//                    isError = personName.isBlank(),
//                    supportingText = {
//                        if (personName.isBlank()) {
//                            Text(text = "name required!", color = Color.Red)
//                        }
//                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AssistChip(
                        onClick = { Log.d("Assist chip", "add age") },
                        label = { Text("Age") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Age",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    AssistChip(
                        onClick = { Log.d("Assist chip", "add location") },
                        label = { Text("Location") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Location",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    AssistChip(
                        onClick = { Log.d("Assist chip", "add occupation") },
                        label = { Text("Occupation") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Occupation",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    AssistChip(
                        onClick = { Log.d("Assist chip", "Add Place of Work/Study") },
                        label = { Text("Place of work/study") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Place of Work/Study",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    AssistChip(
                        onClick = { Log.d("Assist chip", "Add Stature") },
                        label = { Text("Stature") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Stature",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    AssistChip(
                        onClick = { Log.d("Assist chip", "Add Nationality") },
                        label = { Text("Nationality") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Nationality",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )

//                    OutlinedButton(
//                        onClick = {  },
//                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
//                        border = BorderStroke(1.dp, Color.Blue)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Add,
//                            contentDescription = "Add Nationality",
//                            modifier = Modifier.size(ButtonDefaults.IconSize)
//                        )
//                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                        Text("Nationality", color = Color.Green)
//                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row() {
                    IconToggleButton(
                        checked = recordButtonChecked,
                        onCheckedChange = {
                            recordButtonChecked = it
                            // Handle speech recognition
                        }
                    ) {
                        Icon(
                            imageVector = if (recordButtonChecked) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = if (recordButtonChecked) "Stop" else "Record",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Record speech for text transformation or type below",
                        fontStyle = FontStyle.Italic
                    )

                }

                Spacer(modifier = Modifier.width(16.dp))

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

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Add image"
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Upload Image (Optional)",
                        fontStyle = FontStyle.Italic
                    )

                }
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
                                )
                            )
                            onCancel()
                        },
//                        enabled = personName.isNotEmpty()
                    ) {
                        Text("Save")
                    }

                    TextButton(
                        onClick = {
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
    onNavigateToPersonDetails: (String) -> Unit
) {
    var personId = person.id
    var personName = person.name

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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$personName",
                    )
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