package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Local acceptance only: observe actual production plugins/native configurations without changing their APIs. */
class SherpaProductionPluginNativeTest {
    private val base = "sherpa-onnx-moonshine-base-en-quantized-2026-02-27"
    private val tiny = "sherpa-onnx-moonshine-tiny-en-quantized-2026-02-27"
    private val request: AsrRequest by lazy {
        val resource = requireNotNull(javaClass.getResource("/jfk.wav"))
        val (pcm, info) = AudioManager.loadFromWav(Path.of(resource.toURI()))
        AsrRequest(pcm.copyOf(5 * info.sampleRate * info.sampleWidth * info.channels), info)
    }

    @Test
    fun `actual production plugins reload through same and cross provider registry selection`() {
        val moonshine = SherpaMoonshineAsr(SherpaMoonshineAsrOptions(base, Language.ENGLISH))
        val whisper = SherpaWhisperAsr(SherpaWhisperAsrOptions(language = Language.ENGLISH))
        val registry = AppPluginRegistry()
        registry.register(AppPluginCategory.ASR, moonshine)
        registry.register(AppPluginCategory.ASR, whisper)
        try {
            val ids = listOf(base, tiny, base, "sherpa-onnx-whisper-tiny", "sherpa-onnx-whisper-tiny.en",
                "sherpa-onnx-whisper-tiny", base)
            ids.forEach { id ->
                val plugin: SherpaOfflineAsr<*> = if (id.startsWith("sherpa-onnx-moonshine")) {
                    moonshine.updateOptions(SherpaMoonshineAsrOptions(id, Language.ENGLISH))
                    moonshine
                } else {
                    whisper.updateOptions(SherpaWhisperAsrOptions(id, Language.ENGLISH))
                    whisper
                }
                registry.setActiveById(AppPluginCategory.ASR, plugin.id)
                val response = plugin.transcribe(request).getOrThrow()
                assertTrue(response.text.isNotBlank())
                val actual = actualNativeModel(plugin)
                assertEquals(id, actual)
                println("PRODUCTION_NATIVE requested=$id loaded=$actual chars=${response.text.length}")
            }
        } finally {
            registry.shutdownAll()
        }
    }

    @Test
    fun `actual Moonshine failed replacement leaves no stale engine and recovers`() {
        val plugin = SherpaMoonshineAsr(SherpaMoonshineAsrOptions(base, Language.ENGLISH))
        try {
            plugin.enable()
            assertTrue(plugin.transcribe(request).getOrThrow().text.isNotBlank())
            plugin.updateOptions(SherpaMoonshineAsrOptions("missing-model", Language.ENGLISH))
            val failed = plugin.transcribe(request)
            assertTrue(failed.isFailure)
            assertNull(nativeAdapter(plugin))
            plugin.updateOptions(SherpaMoonshineAsrOptions(tiny, Language.ENGLISH))
            assertTrue(plugin.transcribe(request).getOrThrow().text.isNotBlank())
            assertEquals(tiny, actualNativeModel(plugin))
            println("PRODUCTION_NATIVE_RECOVERY invalid selection failed safely; Tiny loaded and transcribed")
        } finally {
            plugin.disable()
            plugin.shutdown()
        }
    }

    private fun nativeAdapter(plugin: SherpaOfflineAsr<*>): Any? {
        val slot = SherpaOfflineAsr::class.java.getDeclaredField("recognizer")
        slot.isAccessible = true
        return slot.get(plugin)
    }

    private fun actualNativeModel(plugin: SherpaOfflineAsr<*>): String {
        val adapter = assertNotNull(nativeAdapter(plugin))
        val handle = adapter.javaClass.getDeclaredField("recognizer")
        handle.isAccessible = true
        val native = handle.get(adapter) as OfflineRecognizer
        return Path.of(native.config.modelConfig.tokens).parent.fileName.toString()
    }
}
