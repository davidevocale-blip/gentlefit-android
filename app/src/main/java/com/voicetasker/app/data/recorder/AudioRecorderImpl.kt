package com.voicetasker.app.data.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles audio recording using MediaRecorder.
 * Saves recordings as M4A (AAC) files in the app's internal storage.
 */
@Singleton
class AudioRecorderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    sealed class RecorderState {
        data object Idle : RecorderState()
        data object Recording : RecorderState()
        data object Paused : RecorderState()
        data class Error(val message: String) : RecorderState()
    }

    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var recordingStartTime: Long = 0

    private val _state = MutableStateFlow<RecorderState>(RecorderState.Idle)
    val state: StateFlow<RecorderState> = _state.asStateFlow()

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude.asStateFlow()

    private fun getRecordingsDir(): File {
        val dir = File(context.filesDir, "recordings")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun startRecording(): String? {
        try {
            val fileName = "voice_${System.currentTimeMillis()}.m4a"
            val file = File(getRecordingsDir(), fileName)
            currentFilePath = file.absolutePath

            mediaRecorder = createMediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(currentFilePath)
                prepare()
                start()
            }

            recordingStartTime = System.currentTimeMillis()
            _state.value = RecorderState.Recording
            return currentFilePath
        } catch (e: Exception) {
            _state.value = RecorderState.Error(e.message ?: "Recording error")
            return null
        }
    }

    fun stopRecording(): Pair<String?, Long> {
        val duration = System.currentTimeMillis() - recordingStartTime
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
            // MediaRecorder may throw if stopped too quickly
        }
        mediaRecorder = null
        _state.value = RecorderState.Idle
        val path = currentFilePath
        currentFilePath = null
        return Pair(path, duration)
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.pause()
            _state.value = RecorderState.Paused
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
            _state.value = RecorderState.Recording
        }
    }

    fun getMaxAmplitude(): Int {
        return try {
            val amp = mediaRecorder?.maxAmplitude ?: 0
            _amplitude.value = amp
            amp
        } catch (_: Exception) {
            0
        }
    }

    fun deleteRecording(filePath: String) {
        try {
            File(filePath).delete()
        } catch (_: Exception) { }
    }

    @Suppress("DEPRECATION")
    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
    }
}
