import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hu.ait.connect.R
import hu.ait.connect.ui.screen.camera.ComposeFileProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import hu.ait.connect.data.person.Person
import hu.ait.connect.ui.screen.person.PersonViewModel

@Composable
private fun CameraCapture(
    onImageCaptured: (Boolean, Uri?) -> Unit,
    context: Context,
    updateIsTakingPicture: (Boolean) -> Unit,
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onImageCaptured(true, imageUri!!)
            }
            updateIsTakingPicture(false)
        }
    )

    onImageCaptured(false, imageUri)

    LaunchedEffect(Unit) {
        val uri = ComposeFileProvider.getImageUri(context)
        imageUri = uri
        cameraLauncher.launch(uri)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureButton(
    onImageCaptured: (Boolean, Uri?) -> Unit,
) {

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
    )

    val context = LocalContext.current
    var isTakingPicture by remember {
        mutableStateOf(false)
    }

    IconButton(
        onClick = {
            if (permissionsState.allPermissionsGranted) {
                isTakingPicture = true
            } else {
                permissionsState.launchMultiplePermissionRequest()
            }
        }
    ) {
        Icon(
            Icons.Filled.CameraAlt,
            contentDescription = "Add image"
        )
    }

    if (isTakingPicture) {
        CameraCapture(
            onImageCaptured = onImageCaptured,
            context = context,
            updateIsTakingPicture = { isTakingPicture = it }
        )
    }
}

@Composable
fun ProfileImage(
    imageUri: String?,
    contentDescription: String? = "image",
    borderColor: Int = Color.Transparent.toArgb(),
    imageSize: Int = 60,
    modifier: Modifier? = null
) {
    Box(
        modifier =
        if (modifier == null) Modifier
            .size(imageSize.dp)
            .clip(CircleShape)
            .border(2.dp, Color(borderColor), CircleShape)
        else modifier
    ) {
        imageUri?.let { uri ->
            AsyncImage(
                model = imageUri,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = contentDescription,
            )
        } ?: run {
            ProfilePicturePlaceholder()
        }
    }
}

@Composable
fun ProfilePicturePlaceholder(
) {
    Image(
        painter = painterResource(R.drawable.profile_avatar),
        contentDescription = "Profile Picture Placeholder",
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ClickableProfilePicture(
    person: Person,
    personImageUri: String?,
    categoryColor: Int = Color.Transparent.toArgb(),
    size: Int = 60,
    cornerRadius: Int = 50
) {
    var showimageDialog by remember { mutableStateOf(false) }

    ProfileImage(
        imageUri = personImageUri,
        contentDescription = "Person Image",
        modifier =
            Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(cornerRadius  ))
                .border(2.dp, Color(categoryColor), CircleShape)
                .clickable { showimageDialog = true }
    )

    if (showimageDialog) {
        FullProfileImageDialog(
            person = person,
            showDialog = showimageDialog,
            updateShowDialog = { showimageDialog = it },
            imageUri = personImageUri,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FullProfileImageDialog(
    personViewModel: PersonViewModel = hiltViewModel(),
    person: Person,
    showDialog: Boolean,
    updateShowDialog: (Boolean) -> Unit,
    imageUri: String? = null,
) {

    var displayUri by remember { mutableStateOf(imageUri) }

    var isSuccessfulCapture by remember { mutableStateOf(false) }
    var isTakingPicture by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val onImageCaptured = { success: Boolean, uri: Uri? ->
        isSuccessfulCapture = success
        if (success) {
            displayUri = uri!!.toString()
            personViewModel.editPerson(person.copy(imageUri = displayUri))
        }
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
    )

    if (showDialog) {
        Dialog(onDismissRequest = { updateShowDialog(false) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileImage(
                        imageUri = displayUri,
                        contentDescription = "Enlarged Profile Picture",
                        imageSize = 200
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                updateShowDialog(false)
                            },
                        ) {
                            Text(
                                "Close",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        VerticalDivider(thickness = 1.dp, modifier = Modifier.fillMaxHeight())

                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (permissionsState.allPermissionsGranted) {
                                    isTakingPicture = true
                                } else {
                                    permissionsState.launchMultiplePermissionRequest()
                                }
                            },
                        ) {
                            Text(
                                "Replace",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        if (isTakingPicture) {
                            CameraCapture(
                                onImageCaptured,
                                context = context,
                                updateIsTakingPicture = { isTakingPicture = it }
                            )
                        }


                    }
                }
            }
        }
    }

}