package com.example.synthtranslator;

import android.media.AudioRecord;

import java.io.InputStream;

import com.example.synthtranslator.translator.SynthTranslator;
import com.example.synthtranslator.recorder.VoiceRecorder;

class SynthTranslatorLoop {
    private final SynthTranslator st = new SynthTranslator();
    private final VoiceRecorder vr;

    private String recognized_text;
    private String translated_text;
    private InputStream synthesized_stream;

    SynthTranslatorLoop(AudioRecord recorder) {
        this.vr = new VoiceRecorder(recorder);
    }

    void startLoop() {
        this.vr.captureAudio();

        while (true) {
            System.out.println(this.vr.getAvailableBytesOfCapturing());
        }
    }
}