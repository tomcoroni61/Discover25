package trust.jesus.discover.little.recognio

import android.os.Bundle


//https://github.com/StephenVinouze/KontinuousSpeechRecognizer/tree/master

interface RecognitionCallback {
    fun onPrepared(status: RecognitionStatus)
    fun onBeginningOfSpeech()
    //fun onKeywordDetected()
    fun onReadyForSpeech(params: Bundle)
    //fun onBufferReceived(buffer: ByteArray)
    fun onRmsChanged(rmsdB: Float)
    fun onPartialResults(results: List<String>)
    fun onResults(results: List<String>, scores: FloatArray?)
    fun onError(errorCode: Int)
    //fun onEvent(eventType: Int, params: Bundle)
    //fun onEndOfSpeech()
}