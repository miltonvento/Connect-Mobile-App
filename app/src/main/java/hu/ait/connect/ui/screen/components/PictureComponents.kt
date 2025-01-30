import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.google.accompanist.permissions.MultiplePermissionsState
import hu.ait.connect.R
import hu.ait.connect.ui.screen.camera.ComposeFileProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.toArgb


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TakePicture(
    permissionsState: MultiplePermissionsState,
    context: Context,
    updateImageUri: (Uri?) -> Unit,
    updateHasImage: (Boolean) -> Unit,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
) {

    IconButton(
        onClick = {
            if (permissionsState.allPermissionsGranted) {
                updateImageUri(null)
                updateHasImage(false)
                val uri = ComposeFileProvider.getImageUri(context)
                updateImageUri(uri)
                cameraLauncher.launch(uri)
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
    personImageUri: String?,
    categoryColor: Int,
) {
    var showimageDialog by remember { mutableStateOf(false) }

    ProfileImage(
        imageUri = personImageUri,
        contentDescription = "Person Image",
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .border(2.dp, Color(categoryColor), CircleShape)
            .clickable { showimageDialog = true }
    )

    if (showimageDialog) {
        FullProfileImageDialog(
            showDialog = showimageDialog,
            updateShowDialog = { showimageDialog = it },
            imageUri = personImageUri
        )
    }
}

@Composable
fun FullProfileImageDialog(
    showDialog: Boolean,
    updateShowDialog: (Boolean) -> Unit,
    imageUri: String? = null
) {
    if (showDialog) {
        Dialog(onDismissRequest = { updateShowDialog(false) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileImage(
                        imageUri = imageUri,
                        contentDescription = "Enlarged Profile Picture",
                        imageSize = 200
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
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
                            },
                        ) {
                            Text(
                                "Replace",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }
    }

}

//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//private fun TakePicture(
//    permissionsState: MultiplePermissionsState,
//    context: Context,
//    imageUri: Uri?,
//    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
//) {
//    var imageUri1 = imageUri
//
//    if (permissionsState.allPermissionsGranted) {
//        IconButton(
//            onClick = {
//
//
//
//                val uri = ComposeFileProvider.getImageUri(context)
//                imageUri1 = uri
//                cameraLauncher.launch(uri)
//            }
//        ) {
//            Icon(
//                Icons.Filled.CameraAlt,
//                contentDescription = "Add image"
//            )
//        }
//
//    } else {
//        val textToShow = if (permissionsState.shouldShowRationale) {
//            // If the user has denied the permission but the rationale can be shown,
//            // then gently explain why the app requires this permission
//            "RATIONALEXPLANATION The camera is important for this app. Please grant the permission."
//        } else {
//            // If it's the first time the user lands on this feature, or the user
//            // doesn't want to be asked again for this permission, explain that the
//            // permission is required
//            "Camera permission required for this feature to be available. " +
//                    "Please grant the permission"
//        }
//        Text(textToShow)
//
//        Button(onClick = {
//            permissionsState.launchMultiplePermissionRequest()
//        }) {
//            Text("Request permission")
//        }
//    }
//}