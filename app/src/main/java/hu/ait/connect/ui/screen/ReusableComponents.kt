package hu.ait.connect.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ait.connect.data.category.Category

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagArea(
    tags: Map<String, Any>?,
    taglist: List<String>? = null,
    borderColor: Int? = null
) {

    if (tags == null || tags.isEmpty()){
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
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        border = BorderStroke(1.dp, borderColor?.let { Color(it) } ?: Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }

    //    if (tags == null || tags.isEmpty()) {
//        Text("No memory cues added. Click below to add memory cues", fontStyle = FontStyle.Italic)
//        FlowRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(2.dp),
//        ) {
//            taglist?.forEach { tag ->
//                if (tag != "") {
//                    AssistChip(
//                        onClick = {},
//                        label = {
//                            Text(
//                                tag.toString(),
//                                style = MaterialTheme.typography.bodyMedium.copy(
//                                    fontSize = 16.sp,
//                                ),
//                            )
//                        },
//                        leadingIcon = {
//                            Icon(
//                                Icons.Filled.Add,
//                                contentDescription = "$tag",
//                                Modifier.size(AssistChipDefaults.IconSize)
//                            )
//                        }
//                    )
//                    Spacer(modifier = Modifier.width(12.dp))
//                }
//            }
//        }
//        return
//    }
}

@Composable
fun CategoriesDropdown(
    list: List<Category>,
    preselected: String,
    onSelectionChanged: (myData: Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) }

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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.70f)
            ) {
                list.forEach { listEntry ->
                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry.name
                            expanded = false
                            onSelectionChanged(listEntry)
                        },
                        text = {
                            Text(
                                text = listEntry.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                            )
                        },
                    )
                }
            }
        }
    }
}