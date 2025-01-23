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

@Composable
fun ListViewComponent(
    person: Person,
    onDeletePerson: (Person) -> Unit,
    onNavigateToPersonDetails: (String) -> Unit,
    categoryColor: Int,
) {
    var personId = person.id
    var personName = person.name
    var personDescription = person.description
    var personAudio = person.audio
    var personTags = person.tags
    val personImageUri = person.imageUri

    ListItem(
        modifier = Modifier.padding(vertical = 8.dp)
            .clickable {
                onNavigateToPersonDetails(personId.toString())
            },
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
            personImageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Person Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(categoryColor), CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Image(
                    painter = painterResource(R.drawable.profile_avatar),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(categoryColor), CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                modifier = Modifier.clickable {
                                    onDeletePerson(person)
                },
                tint = Color.Black
            )
        },
        colors = ListItemDefaults.colors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    )
}