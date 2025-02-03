package hu.ait.connect.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hu.ait.connect.R
import hu.ait.connect.data.person.Person
import ClickableProfilePicture
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListViewComponent(
    person: Person,
    categoryColor: Int,
    isSelected: Boolean = false,
    onLongPress: () -> Unit,
    onClick: () -> Unit
) {
    var personName = person.name
    var personDescription = person.description
    var personTags = person.tags
    val personImageUri = person.imageUri

    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        label = "SelectionBackground"
    )
        ListItem(  modifier = Modifier
            .padding(vertical = 8.dp)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongPress
            ),
            colors = ListItemDefaults.colors(
                containerColor = backgroundColor
            ),
            headlineContent = {
                Text(
                    text = personName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                if (
                    personTags?.isNotEmpty() == true
                ) {
                    TagArea(
                        tags = personTags,
                        borderColor = categoryColor
                    )
                } else {
                    Text(
                        personDescription,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            leadingContent = {
                ClickableProfilePicture(
                    personImageUri = personImageUri,
                    categoryColor = categoryColor,
                    person = person,
                )
            },
//        colors = ListItemDefaults.colors(
//        ),
        )
    }