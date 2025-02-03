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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
    preselected: String,
    onSelectionChanged: (myData: Category) -> Unit,
    modifier: Modifier = Modifier,
    categoryViewModel: CategoryViewModel
) {

    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) }
    var addCategory by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<Color>(Color.LightGray) }

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
                text = selected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(
                Icons.Outlined.ArrowDropDown, null, modifier =
                Modifier.padding(8.dp)
            )

            CategorySelectionMenu(
                expanded,
                addCategory,
                categoryList,
                selected,
                onSelectionChanged,
                categoryName,
                categoryViewModel,
                selectedColor
            )

        }
    }
}

@Composable
fun CategorySelectionMenu(
    expanded: Boolean,
    addCategory: Boolean,
    categoryList: State<List<Category>>,
    selected: String,
    onSelectionChanged: (myData: Category) -> Unit,
    categoryName: String,
    categoryViewModel: CategoryViewModel,
    selectedColor: Color,
    withAddCategory: Boolean = true,
    onDismiss: () -> Unit = {}
) {
    var expanded1 = expanded
    var addCategory1 = addCategory
    var selected1 = selected
    var categoryName1 = categoryName
    var selectedColor1 = selectedColor

    DropdownMenu(
        expanded = expanded1,
        onDismissRequest = { onDismiss() },
        modifier = Modifier.fillMaxWidth(0.70f)
    ) {
        if (!addCategory1) {
            categoryList.value.forEach { listEntry ->
                DropdownMenuItem(
                    onClick = {
                        selected1 = listEntry.name
                        expanded1 = false
                        onSelectionChanged(listEntry)
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
                        addCategory1 = true
                        expanded1 = false
                    },
                    text = {
                        TextButton(
                            onClick = {
                                addCategory1 = true
                            },
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                text = "Add category",
                                style = TextStyle(
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                )
            }

        } else
            Column(
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Category Name") },
                    value = categoryName1,
                    onValueChange = {
                        categoryName1 = it
                        selected1 = categoryName1
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            categoryViewModel.addCategory(
                                categoryName = categoryName1,
                                categoryColor = selectedColor1
                            ) { newCategory ->
                                onSelectionChanged(newCategory)
                                addCategory1 = false
                            }
                        },
                        enabled = categoryName1.isNotEmpty()
                    ) {
                        Text("Save")
                    }
                    TextButton(
                        onClick = {
                            addCategory1 = false
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
                        .background(selectedColor1),
                )
                Spacer(modifier = Modifier.height(16.dp))
                AdvancedColorPicker(
                    onColorChanged = { color ->
                        selectedColor1 = color
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

//@Composable
//fun PersonCard(
//    categoryColor: Int,
//    person: Person,
//    onDeletePerson: (Person) -> Unit,
//    onNavigateToPersonDetails: (String) -> Unit,
//    audioRecordViewModel: AudioRecordViewModel = viewModel(factory = AudioRecordViewModel.factory),
//) {
//    var personId = person.id
//    var personName = person.name
//    var personDescription = person.description
//    var personAudio = person.audio
//    var personTags = person.tags
//    val personImageUri = person.imageUri
//
//    var expanded by remember { mutableStateOf(false) }
//
//    Card(
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant,
//        ),
//        shape = RoundedCornerShape(20.dp),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 5.dp
//        ),
//        modifier = Modifier
//            .padding(5.dp)
//            .fillMaxWidth()
//            .clickable {
//                onNavigateToPersonDetails(personId.toString())
//            },
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(20.dp)
//                .animateContentSize()
//        ) {
//
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                personImageUri?.let { uri ->
//                    AsyncImage(
//                        model = uri,
//                        contentDescription = "Person Image",
//                        modifier = Modifier
//                            .size(60.dp)
//                            .clip(CircleShape)
//                            .border(2.dp, Color(categoryColor), CircleShape),
//                        contentScale = ContentScale.Crop
//                    )
//                } ?: run {
//                    Image(
//                        painter = painterResource(R.drawable.profile_avatar),
//                        contentDescription = "Profile Picture",
//                        modifier = Modifier
//                            .size(65.dp)
//                            .clip(CircleShape)
//                            .border(2.dp, Color(categoryColor), CircleShape),
//                        contentScale = ContentScale.Crop,
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//
//                    Text(
//                        text = "$personName",
//                        fontSize = 22.sp,
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    if (
//                        personTags?.isNotEmpty() == true
//                    ) {
//                        TagArea(
//                            tags = personTags,
//                            borderColor = categoryColor
//                        )
//                    } else {
//                        Text(
//                            personDescription,
//                            maxLines = 2
//                        )
//                    }
//                    if (expanded) {
//                        if (personAudio != null) {
//                            audioRecordViewModel.saveAudioFileFromByteArray(
//                                personAudio,
//                                "$personId, audio.3gp"
//                            )
//                            if (audioRecordViewModel.isFileExists("$personId, audio.3gp")) {
//                                AudioPlaybackUI(
//                                    audioRecordViewModel = audioRecordViewModel,
//                                    audioFilePath = "$personId, audio.3gp"
//                                )
//                            }
//                        }
//                    }
//                }
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(onClick = { expanded = !expanded }) {
//                        Icon(
//                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
//                            else Icons.Filled.KeyboardArrowDown,
//                            contentDescription = if (expanded) {
//                                "Less"
//                            } else {
//                                "More"
//                            },
////                            tint = Color(categoryColor)
//                        )
//                    }
//
//                    Icon(
//                        imageVector = Icons.Filled.Delete,
//                        contentDescription = "Delete",
//                        modifier = Modifier.clickable {
//                            onDeletePerson(person)
//                        },
////                        tint = Color(categoryColor)
//                    )
//                }
//
//            }
//        }
//    }
//}