package hu.ait.connect.ui.screen.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.connect.data.category.Category
import hu.ait.connect.ui.screen.category.CategoryViewModel
import hu.ait.connect.ui.screen.category.SliderWithLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagArea(
    tags: Map<String, Any>?,
    taglist: List<String>? = null,
    borderColor: Int? = null
) {

    if (tags == null || tags.isEmpty()) {
        return
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .height(50.dp)
            .verticalScroll(scrollState)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
        ) {
            tags.forEach { (tag, value) ->
                if (value != "") {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                value.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        border = BorderStroke(
                            1.dp,
                            borderColor?.let { Color(it) } ?: Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesDropdown(
    categoryList: State<List<Category>>,
    selectedCategory: Category?,
    onSelectionChanged: (myData: Category) -> Unit,
    modifier: Modifier = Modifier,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
) {

    var expanded by remember { mutableStateOf(false) }
//    var categoryName by remember { mutableStateOf("") }

    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = selectedCategory?.name ?: "Select Category",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(
                Icons.Outlined.ArrowDropDown, null, modifier =
                Modifier.padding(8.dp)
            )

            CategorySelectionMenu(
                expanded = expanded,
                categoryList = categoryList,
                onSelectionChanged = onSelectionChanged,
                categoryViewModel = categoryViewModel,
                setExpanded = { expanded = it }
            )

        }
    }
}

@Composable
fun CategorySelectionMenu(
    expanded: Boolean,
    categoryList: State<List<Category>>,
    onSelectionChanged: (myData: Category) -> Unit,
    categoryViewModel: CategoryViewModel,
    withAddCategory: Boolean = true,
    setExpanded: (Boolean) -> Unit = {}
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<Color>(Color.LightGray) }

    var isAddCategoryExpanded by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            setExpanded(false)
            isAddCategoryExpanded = false
        },
        modifier = Modifier.fillMaxWidth(0.70f)
    ) {

        if (!isAddCategoryExpanded) {
            categoryList.value.forEach { listEntry ->
                DropdownMenuItem(
                    onClick = {
                        onSelectionChanged(listEntry)
                        setExpanded(false)
                    },
                    text = {
                        Text(
                            text = listEntry.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Start)
                                .padding(0.dp)
                        )
                    },
                )
            }

            if (withAddCategory) {
                DropdownMenuItem(
                    onClick = {
                        isAddCategoryExpanded = true
                    },
                    text = {
                        Text(
                            text = "Add category",
                            style = TextStyle(
                                fontSize = 14.sp
                            )
                        )
                    }
                )
            }
        } else
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(8.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Category Name") },
                    value = categoryName,
                    onValueChange = {
                        categoryName = it
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End

                ) {
                    TextButton(
                        onClick = {
                            categoryViewModel.addCategory(
                                categoryName = categoryName,
                                categoryColor = selectedColor
                            ) {
                                newCategory ->
                                onSelectionChanged(newCategory)
                                setExpanded(false)
                                isAddCategoryExpanded = false
                            }
                        },
                        enabled = categoryName.isNotEmpty()
                    ) {
                        Text("Save")
                    }
                    TextButton(
                        onClick = {
                            isAddCategoryExpanded = false
                        },
                    ) {
                        Text("Cancel")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                AdvancedColorPicker(
                    onColorChanged = { color ->
                        selectedColor = color
                    },
                )
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

    Column(
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ){
            Text(
                text = "Adjust color using slider below:",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
            )
            Box(
            ){
                Spacer(
                    modifier = Modifier
                        .size(24.dp)
                        .background(selectedColor, shape = CircleShape)
                )
            }
        }

        SliderWithLabel(
            value = hue,
            valueRange = 0f..360f,
            onValueChange = { hue = it }
        )
    }
}