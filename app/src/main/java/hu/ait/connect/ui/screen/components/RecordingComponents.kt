import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import hu.ait.connect.ui.screen.AudioRecordViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingUI(
    audioRecordViewModel: AudioRecordViewModel,
    onAudioRecorded: () -> Unit,
    isRecording: (Boolean) -> Unit,
    permissionsState: MultiplePermissionsState,
) {
    val amplitude by audioRecordViewModel.audioAmplitude.observeAsState(0)
    var isSelected by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    var onDialogDismiss = { showDialog = false }

    IconToggleButton(
        checked = isSelected,
        onCheckedChange = { isChecked ->
            isSelected = isChecked
            if (isChecked && !permissionsState.allPermissionsGranted) {
                permissionsState.launchMultiplePermissionRequest()
            }

            if (isChecked && permissionsState.allPermissionsGranted) {
                audioRecordViewModel.startRecording()
                isRecording(true)
            } else {
                audioRecordViewModel.stopRecording()
                isRecording(false)
                onAudioRecorded()
            }
        }
    ) {
        Icon(
            imageVector = if (isSelected && permissionsState.allPermissionsGranted) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = if (isSelected) "Stop Recording" else "Start Recording",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }

    if (isSelected && !permissionsState.allPermissionsGranted) {
        showDialog = true
    }

//    if (showDialog) {
//        permissionsDialog(permissionsState = permissionsState, onDismiss = onDialogDismiss)
//    }

//    if (isSelected) {
//        AudioVisualizer(
//            amplitude = amplitude,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//        )
//    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun permissionsDialog(
    permissionsState: MultiplePermissionsState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "To record a voice message, allow Connect to use the microphone.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                permissionsState.launchMultiplePermissionRequest()
            }) {
                Text(text = "Request permissions")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordIcon(
    permissionsState: MultiplePermissionsState,
    audioRecordViewModel: AudioRecordViewModel,
    audioRecorded: Boolean
) {
    var audioRecorded1 = audioRecorded

//    if (permissionsState.allPermissionsGranted) {
//        RecordingUI(audioRecordViewModel = audioRecordViewModel,
//            onAudioRecorded = { audioRecorded1 = true })
//    } else {
//        Button(onClick = {
//            permissionsState.launchMultiplePermissionRequest()
//        }) {
//            Text(text = "Request permissions")
//        }
//    }
}

//@Composable
//fun AudioPlaybackVisualizer(progress: Float, modifier: Modifier = Modifier) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(20.dp)
//            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth(progress)
//                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
//        )
//    }
//}

@Composable
fun AudioVisualizer(amplitude: Int, modifier: Modifier = Modifier) {
    val bars = 20
    val maxAmplitude = 32767
    val normalizedAmplitude = (amplitude.toFloat() / maxAmplitude * bars).toInt()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..bars) {

            val heightFactor by animateFloatAsState(
                targetValue = if (i <= normalizedAmplitude) i.toFloat() / bars else 0.2f,
                animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height((30.dp * heightFactor).coerceAtLeast(10.dp))
                    .background(
                        color = if (i <= normalizedAmplitude) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun AudioPlaybackUI(audioRecordViewModel: AudioRecordViewModel, audioFilePath: String? = null) {
    val playbackProgress by audioRecordViewModel.playbackProgress.observeAsState(0f)
    val playbackComplete by audioRecordViewModel.playbackComplete.observeAsState(false)
    var isPlaying by remember { mutableStateOf(false) }

    if (playbackComplete && isPlaying) {
        isPlaying = false
        audioRecordViewModel.stopPlaying()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconToggleButton(
            checked = isPlaying,
            onCheckedChange = { checked ->
                isPlaying = checked
                if (checked) {
                    if (!audioFilePath.isNullOrEmpty() && audioRecordViewModel.isFileExists(
                            audioFilePath
                        )
                    ) {
                        audioRecordViewModel.startPlaying(audioFilePath)
                    } else {
                        audioRecordViewModel.startPlaying("audiorecordtest.3gp")
                    }
                } else {
                    audioRecordViewModel.stopPlaying()
                }
            }
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Stop Playing" else "Start Playing",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AudioPlaybackVisualizer(
            progress = playbackProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

@Composable
fun AudioPlaybackVisualizer(progress: Float, modifier: Modifier = Modifier) {
    val bars = 20
    val barWidth = 6.dp

    // Generate random heights for the bars to simulate a waveform
    val randomHeights = remember { List(bars) { (3..20).random() } }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        randomHeights.forEachIndexed { index, height ->
            val barFillProgress = animateFloatAsState(
                targetValue = if (index < (progress * bars).toInt()) 1f else 0f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height((height * 2).dp) // Scale the height to make it more visible
                    .background(
                        color = if (barFillProgress.value == 1f) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(50)
                    )
                    .fillMaxHeight()
            )
        }
    }
}

//@Composable
//fun AudioPlaybackUI(audioRecordViewModel: AudioRecordViewModel, audioFilePath: String? = null) {
//    val playbackProgress by audioRecordViewModel.playbackProgress.observeAsState(0f)
//    val playbackComplete by audioRecordViewModel.playbackComplete.observeAsState(false)
//    var isPlaying by remember { mutableStateOf(false) }
//
//    if (playbackComplete && isPlaying) {
//        isPlaying = false
//        audioRecordViewModel.stopPlaying()
//    }
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center,
//    ) {
//        IconToggleButton(
//            checked = isPlaying,
//            onCheckedChange = { checked ->
//                isPlaying = checked
//                if (checked) {
//                    if (!audioFilePath.isNullOrEmpty() && audioRecordViewModel.isFileExists(
//                            audioFilePath
//                        )
//                    ) {
//                        audioRecordViewModel.startPlaying(audioFilePath)
//                    } else {
//                        audioRecordViewModel.startPlaying("audiorecordtest.3gp")
//                    }
//                } else {
//                    audioRecordViewModel.stopPlaying()
//                }
//            }
//        ) {
//            Icon(
//                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
//                contentDescription = if (isPlaying) "Stop Playing" else "Start Playing",
//                modifier = Modifier.size(ButtonDefaults.IconSize)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        if (isPlaying) {
//            AudioPlaybackVisualizer(
//                progress = playbackProgress,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(20.dp)
//            )
//        }
//    }
//}