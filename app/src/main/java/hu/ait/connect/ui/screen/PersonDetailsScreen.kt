package hu.ait.connect.ui.screen

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import hu.ait.connect.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailsScreen(
    navController: NavHostController,
    personId: String,
    personViewModel: PersonViewModel = hiltViewModel(),
) {
    val person = personViewModel.getPersonById(personId.toInt()).collectAsState(initial = null)
    var personName by rememberSaveable { mutableStateOf("") }
    var cornerRadius = 20

    if (person.value != null) {
        personName = person.value!!.name
    } else {
        Text(text = "Loading person details...")
    }

    val description =
        "The first time I met Sarah, she had an easy smile that made me feel instantly at ease. She wore a deep blue jacket that seemed to complement the calmness in her eyes, which were a striking shade of green, almost like the color of a forest after rain. Her voice was soft but confident, and she spoke with an openness that made me feel like I could talk to her for hours. I remember how she laughed at something silly I said, her laugh warm and genuine, like we had known each other for years. She had a quiet strength about her, yet there was something playful in the way she moved, as if she was always up for a bit of adventure".trimIndent()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Person Details",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Content placed in the top-right corner
                    IconButton(onClick = { /* Handle action here */ }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            )

        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.profile_avatar),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp) // Size of the circular image
                            .clip(RoundedCornerShape(cornerRadius.dp)), // Makes the image circular
                        contentScale = ContentScale.Crop // Crop to fit inside the circle
                    )
                    Spacer(modifier = Modifier.width(20.dp)) // Space between image and text
                    Text(
                        text = "$personName",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 42.sp),
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(thickness = 1.dp)
                Spacer(Modifier.height(5.dp))
                Text(
                    "Memory Cues",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        color = Color.Gray
                    ),
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Notes",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        color = Color.Gray
                    ),
                )
                Spacer(Modifier.height(5.dp))
                Card(
//                    modifier = Modifier.padding(bottom = 4.dp, top = 4.dp, start = 4.dp, end = 4.dp),
                ) {
                    Text(
                        description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp, start = 4.dp, end = 4.dp)
                    )
                }

            }
        }
    )
}
