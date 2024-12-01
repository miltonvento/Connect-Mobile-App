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
import hu.ait.connect.data.Person
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.font.FontStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PersonViewModel = hiltViewModel(),
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
            FloatingActionButton(onClick = {
                showAddDialog = true
            })
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        },
    ) { innerpadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerpadding)
            ) {
                if (peopleList.value.isEmpty()) {
                    Text(
                        "Empty list", modifier = Modifier
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

//                                onItemEdit = { item ->
//                                    itemToEdit = item
//                                    showAddDialog = true
//                                }

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
                    label = { Text("Name")},
                    value = "$personName",
                    onValueChange = { personName = it },
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.horizontalScroll(scrollState)
                ){
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
                        label = { Text("Meeting Location")},
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
                        label = { Text("Nationality")},
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
                        label = { Text("Occupation")},
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
                ){
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
                    label = { Text("Additional Details")},
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

                Row( modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconToggleButton(
                        checked = recordButtonChecked,
                        onCheckedChange = {
                            recordButtonChecked= it
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
                        text = "Create voice note",
                        fontStyle = FontStyle.Italic
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Add image"
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(text = "Upload Image",
                        fontStyle = FontStyle.Italic)

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            viewModel.addPerson(
                                Person(
                                    name = personName
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