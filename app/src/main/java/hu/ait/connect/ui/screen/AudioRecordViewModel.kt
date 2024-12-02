package hu.ait.connect.ui.screen

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File
import java.io.IOException

class AudioRecordViewModel(private var context: Context) : ViewModel(){
    private var myPlayer: MediaPlayer? = null
    private var myRecorder: MediaRecorder? = null
    private val _audioAmplitude = MutableLiveData(0)
    val audioAmplitude: LiveData<Int> = _audioAmplitude
    private val handler = Handler(Looper.getMainLooper())
    private val _playbackProgress = MutableLiveData(0f)
    val playbackProgress: LiveData<Float> = _playbackProgress
    private val playbackHandler = Handler(Looper.getMainLooper())
    private val _playbackComplete = MutableLiveData(false)
    val playbackComplete: LiveData<Boolean> = _playbackComplete

    fun startRecording() {
        try {
            myRecorder = MediaRecorder()
            myRecorder?.setAudioSource(
                MediaRecorder.AudioSource.MIC
            )
            myRecorder?.setOutputFormat(
                MediaRecorder.OutputFormat.THREE_GPP
            )

            myRecorder?.setOutputFile(context.openFileOutput("audiorecordtest.3gp", Context.MODE_PRIVATE).fd)

            myRecorder?.setAudioEncoder(
                MediaRecorder.AudioEncoder.AMR_NB
            )
            myRecorder?.prepare()
            myRecorder?.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("TAG_AUDIO_RECORD", "prepare() failed")
        }
        // Continuously fetch audio amplitude
        handler.post(updateAmplitudeRunnable)
    }

    fun stopRecording() {
        try{
            myRecorder?.stop()
            myRecorder?.release()
            _audioAmplitude.value = 0 // Reset amplitude
            handler.removeCallbacks(updateAmplitudeRunnable)
        } catch (e: IllegalStateException) {
            Log.e("TAG_AUDIO_RECORD", "stoppings recording failed")
        }

    }

    private val updateAmplitudeRunnable = object : Runnable {
        override fun run() {
            _audioAmplitude.value = myRecorder?.maxAmplitude ?: 0
            handler.postDelayed(this, 100) // Update every 100ms
        }
    }

    fun startPlaying() {
        myPlayer = MediaPlayer()
        try {
            myPlayer?.setDataSource(context.openFileInput("audiorecordtest.3gp").fd)
            myPlayer?.setOnPreparedListener {
                myPlayer?.start()
                startUpdatingPlaybackProgress()
            }
            myPlayer?.setOnCompletionListener {
                stopPlaying()
                _playbackComplete.value = true
            }
            myPlayer?.prepare()
        } catch (e: IOException) {
            Log.e("TAG_AUDIO_RECORD", "prepare() failed")
        }
    }

    fun stopPlaying() {
        try {
            myPlayer?.stop()
            myPlayer?.release()
            _playbackProgress.value = 0f
            _playbackComplete.value = false
            playbackHandler.removeCallbacksAndMessages(null)
        } catch (e: IllegalStateException) {
            Log.e("TAG_AUDIO_RECORD", "stopping playing audio failed")
        }
    }

    private fun startUpdatingPlaybackProgress() {
        playbackHandler.post(object : Runnable {
            override fun run() {
                myPlayer?.let {
                    val progress = it.currentPosition.toFloat() / it.duration
                    _playbackProgress.value = progress
                    if (it.isPlaying) {
                        playbackHandler.postDelayed(this, 100) // Update every 100ms
                    }
                }
            }
        })
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as Application)
                AudioRecordViewModel(application)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateAmplitudeRunnable)
        myRecorder?.release()
    }

    fun getAudioByteArray(): ByteArray {
        val file = File(context.filesDir, "audiorecordtest.3gp")
        return file.readBytes() // Read the file as a byte array
    }
}