package com.voicetasker.app.data.recorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device speech-to-text transcription using Android's SpeechRecognizer.
 * Supports Italian (it-IT) language.
 */
@Singleton
class SpeechTranscriberImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    sealed class TranscriptionState {
        data object Idle : TranscriptionState()
        data object Listening : TranscriptionState()
        data class Result(val text: String) : TranscriptionState()
        data class PartialResult(val text: String) : TranscriptionState()
        data class Error(val message: String) : TranscriptionState()
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private val _state = MutableStateFlow<TranscriptionState>(TranscriptionState.Idle)
    val state: StateFlow<TranscriptionState> = _state.asStateFlow()

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription.asStateFlow()

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.value = TranscriptionState.Error("Riconoscimento vocale non disponibile")
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "it-IT")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.value = TranscriptionState.Listening
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Errore audio"
                    SpeechRecognizer.ERROR_CLIENT -> "Errore client"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permessi insufficienti"
                    SpeechRecognizer.ERROR_NETWORK -> "Errore di rete"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Timeout di rete"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Nessun risultato"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Riconoscitore occupato"
                    SpeechRecognizer.ERROR_SERVER -> "Errore server"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout parlato"
                    else -> "Errore sconosciuto ($error)"
                }
                _state.value = TranscriptionState.Error(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                _transcription.value = text
                _state.value = TranscriptionState.Result(text)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    _state.value = TranscriptionState.PartialResult(text)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        _state.value = TranscriptionState.Idle
    }

    fun reset() {
        _transcription.value = ""
        _state.value = TranscriptionState.Idle
    }
}
