package hu.ait.connect.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagArea(
    tags: Map<String, Any>?,
    taglist: List<String>? = null
) {
    if (tags == null || tags.isEmpty()) {
        Text("No memory cues added. Click below to add memory cues", fontStyle = FontStyle.Italic)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
        ) {
            taglist?.forEach { tag ->
                if (tag != "") {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                tag.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                ),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "$tag",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
        return
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .height(100.dp)
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
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}