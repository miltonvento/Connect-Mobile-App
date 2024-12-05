package hu.ait.connect.ui.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Categories") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                )
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
//            val items = listOf("Category 1", "Category 2", "Category 3", "Category 4")
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2), // Two columns
//                modifier = Modifier
//                    .padding(innerpadding)
//                    .fillMaxSize(),
//                contentPadding = PaddingValues(16.dp), // Padding around the grid
//                horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between columns
//                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between rows
//            ) {
//                items(items.size) { index ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .aspectRatio(1f), // Ensures the cards are square
//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                    ) {
//                        Text(
//                            text = items[index],
//                            style = MaterialTheme.typography.bodyLarge,
//                            modifier = Modifier
//                                .padding(16.dp)
//                                .fillMaxSize()
//                        )
//                    }
//                }
//
//            }
            if (showAddDialog) {
                AddCategoryDialog(
                    categoryViewModel,
                    onCancel = {
                        showAddDialog = false
                    })
            }

            Column(
                modifier = Modifier
                    .padding(innerpadding)
            ) {
                var categories = categoryViewModel.getAllCategories().collectAsState(initial = null)
                Text("Categories")
                Text("Categories: $categories")
            }
        }
    )
}

@Composable
fun AddCategoryDialog(
    viewModel: CategoryViewModel,
    onCancel: () -> Unit
) {
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
                    "Add New Category",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Category Name") },
                    value = "",
                    onValueChange = { },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {}
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
