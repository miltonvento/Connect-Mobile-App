package hu.ait.connect.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.connect.R
import hu.ait.connect.data.Person
import java.util.Date
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
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
    viewModel: HomeScreenViewModel,
    onCancel: () -> Unit
) {
    var personName by remember {
        mutableStateOf(
            ""
        )
    }

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
                    "New Person",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { "Name" },
                    value = "$personName",
                    onValueChange = { personName = it },
//                    isError = personName.isBlank(),
//                    supportingText = {
//                        if (personName.isBlank()) {
//                            Text(text = "name required!", color = Color.Red)
//                        }
//                    }
                )
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
                onNavigateToPersonDetails(personName)
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
                            onNavigateToPersonDetails(personName)
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