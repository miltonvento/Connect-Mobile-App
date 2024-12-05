package hu.ait.connect.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Slider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onNavigateToCategoryDetails: (String) -> Unit,
    navController: NavHostController,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var categories = categoryViewModel.getAllCategories().collectAsState(initial = emptyList())

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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Two columns
                modifier = Modifier
                    .padding(innerpadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp), // Padding around the grid
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between columns
                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between rows
            ) {
                items(categories.value.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f), // Ensures the cards are square
                        colors = CardDefaults.cardColors(containerColor = Color(categories.value[index].color)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        onClick = {
                            onNavigateToCategoryDetails(categories.value[index].id.toString())
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = categories.value[index].name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize()
                            )
                        }


                    }
                }

            }
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
//                Text("Categories")
//                Text("Categories: ${categoryNames}")
            }
        }
    )
}

@Composable
fun AddCategoryDialog(
    viewModel: CategoryViewModel,
    onCancel: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<Color>(Color.LightGray) }

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
                    value = categoryName,
                    onValueChange = { categoryName = it },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            viewModel.addCategory(categoryName = categoryName, categoryColor = selectedColor) {
                                onCancel()
                            }
                        }
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
                    Text(
                        text = ("Selected Color"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(selectedColor),
//                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                Spacer(modifier = Modifier.height(16.dp))
                    AdvancedColorPicker(
                        onColorChanged = { color ->
                            selectedColor = color
                        },
                    )
            }
        }
    }
}

@Composable
fun AdvancedColorPicker(
    onColorChanged: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(0f) } // Hue 0-360
    var saturation by remember { mutableStateOf(0.5f) } // Saturation 0-1
    var lightness by remember { mutableStateOf(0.5f) } // Lightness 0-1

    val selectedColor = Color.hsl(hue, saturation, lightness)
    onColorChanged(selectedColor)

    Column() {
        Text(
            text = "Adjust color using slider below:",
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SliderWithLabel(
            value = hue,
            valueRange = 0f..360f,
            onValueChange = { hue = it }
        )
    }
}

@Composable
fun SliderWithLabel(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

