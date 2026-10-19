package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineMoonshineModelConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.desktop.settings.SUPPORTED_LOCAL_ASR_MODELS
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Local native acceptance harness; uses copied fixtures, never microphones or user settings. */
class SherpaModelReloadNativeTest {
    private val request: AsrRequest by lazy {
        val resource = requireNotNull(javaClass.getResource("/jfk.wav"))
        val (pcm, info) = AudioManager.loadFromWav(Path.of(resource.toURI()))
        AsrRequest(pcm.copyOf(5 * info.sampleRate * info.sampleWidth * info.channels), info)
    }

    @Test
    fun `native Moonshine reloads Base Tiny Base without restarting`() {
        verifyRoundTrip(
            "sherpa-onnx-moonshine-base-en-quantized-2026-02-27",
            "sherpa-onnx-moonshine-tiny-en-quantized-2026-02-27",
        )
    }

    @Test
    fun `native Whisper reloads multilingual English multilingual without restarting`() {
        verifyRoundTrip("sherpa-onnx-whisper-tiny", "sherpa-onnx-whisper-tiny.en")
    }

    private fun verifyRoundTrip(first: String, second: String) {
        val plugin = ObservedNativeAsr(first)
        try {
            plugin.initialize()
            plugin.enable()
            val sequence = listOf(first, second, first)
            sequence.forEachIndexed { index, modelId ->
                plugin.updateOptions(SherpaWhisperAsrOptions(modelId = modelId, language = Language.ENGLISH))
                val response = plugin.transcribe(request).getOrThrow()
                assertTrue(response.text.isNotBlank(), "Expected short fixture transcription for $modelId")
                println("NATIVE_RESULT requested=$modelId configured=${plugin.configuredIds.last()} chars=${response.text.length}")
                assertEquals(sequence.take(index + 1), plugin.configuredIds, "Actual native configuration must reload")
            }
            plugin.transcribe(request).getOrThrow()
            assertEquals(sequence, plugin.configuredIds, "Unchanged configuration must reuse the engine")
        } finally {
            plugin.disable()
            plugin.shutdown()
        }
    }

    private class ObservedNativeAsr(modelId: String) : SherpaOfflineAsr<SherpaWhisperAsrOptions>(
        SherpaWhisperAsrOptions(modelId = modelId, language = Language.ENGLISH)
    ) {
        override val id = "NATIVE_RELOAD_ACCEPTANCE"
        val configuredIds = mutableListOf<String>()
        override fun supportedModels(): Map<String, VoiceModel> = SUPPORTED_LOCAL_ASR_MODELS
        override fun applyModelSpecificConfig(
            builder: OfflineModelConfig.Builder,
            model: VoiceModel,
            modelPath: Path,
            language: Language,
        ): OfflineModelConfig.Builder {
            configuredIds.add(model.id)
            val encoder = modelPath.resolve(model.components[0].name).toString()
            val decoder = modelPath.resolve(model.components[1].name).toString()
            return when (model.provider) {
                AsrProvider.SHERPA_MOONSHINE -> builder.setMoonshine(
                    OfflineMoonshineModelConfig.builder().setEncoder(encoder).setMergedDecoder(decoder).build()
                )
                AsrProvider.SHERPA_WHISPER -> builder.setWhisper(
                    OfflineWhisperModelConfig.builder().setEncoder(encoder).setDecoder(decoder)
                        .setLanguage(language.iso2).setTask("transcribe").build()
                )
                else -> error("Unsupported test family")
            }
        }
    }
}
