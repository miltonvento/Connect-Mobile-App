package hu.ait.connect.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import hu.ait.connect.data.category.Category
import hu.ait.connect.data.person.Person
import hu.ait.connect.ui.screen.category.CategoryViewModel
import hu.ait.connect.ui.screen.components.CategorySelectionMenu
import hu.ait.connect.ui.screen.components.ListViewComponent
import hu.ait.connect.ui.screen.person.PersonViewModel
import kotlinx.coroutines.launch

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

    var selectedPersons = remember { mutableStateListOf<Person>() }
    var selectionMode by remember { mutableStateOf(false) }

    val configuration = configurationViewModel.getConfig().collectAsState(initial = null)
//    var tagList = configuration.value?.taglist

    var categories = categoryViewModel.getAllCategories().collectAsState(initial = emptyList())
    var categoryNames = categories.value.map { it.name }
    val tabs = listOf("All") + categoryNames
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showCategorySelectionMenu by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearching by rememberSaveable { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (selectionMode) {
                            Text("Selected: ${selectedPersons.size}")
                        } else if (isSearching) {
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

                        if (selectionMode) {
                            IconButton(onClick = {
                                showCategorySelectionMenu = !showCategorySelectionMenu
                            })
                            {
                                Icon(
                                    imageVector = Icons.Filled.GroupAdd,
                                    contentDescription = "Add to category"
                                )
                            }

                            IconButton(onClick = {
                                viewModel.deletePeopleByIds(
                                    selectedPersons.map { it.id }.toList()
                                )
                                selectedPersons.clear()
                                selectionMode = false
                            })

                            {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete"
                                )
                            }

                            if (showCategorySelectionMenu) {
                                CategorySelectionMenu(
                                    expanded = showCategorySelectionMenu,
                                    addCategory = false,
                                    categoryList = categories,
                                    selected = "Select Category",
                                    onSelectionChanged = { selectedCategory ->
                                        selectedPersons.forEach { person ->
                                            viewModel.editPerson(
                                                person.copy(categoryId = selectedCategory.id)
                                            )
                                        }
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Added to ${selectedCategory.name}")
                                        }
                                        selectedPersons.clear()
                                        showCategorySelectionMenu = false
                                        selectionMode = false
                                    },
                                    categoryName = "",
                                    categoryViewModel = categoryViewModel,
                                    selectedColor = Color.Transparent,
                                    withAddCategory = false,
                                    onDismiss = { showCategorySelectionMenu = false }
                                )
                            }

                        } else {
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
                        val matchesCategory =
                            if (selectedTabIndex == 0) true else it.categoryId == categories.value[selectedTabIndex - 1].id
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
                modifier = modifier.padding(8.dp)
            ) {
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
                    itemsIndexed(peopleToDisplay) { index, person ->
                        ListViewComponent(
                            person = person,
                            categoryColor = Color.Transparent.toArgb(),
                            onLongPress = {
                                if (!selectionMode) selectionMode = true
                                if (!selectedPersons.contains(person)) {
                                    selectedPersons.add(person)
                                }
                            },
                            onClick = {
                                if (selectionMode) {
                                    if (selectedPersons.contains(person)) {
                                        selectedPersons.remove(person)
                                        if (selectedPersons.isEmpty()) selectionMode = false
                                    } else {
                                        selectedPersons.add(person)
                                    }
                                } else {
                                    onNavigateToPersonDetails(person.id.toString())
                                }
                            },
                            isSelected = selectedPersons.contains(person)
                        )
                    }
                }
            }

        }
    )
}