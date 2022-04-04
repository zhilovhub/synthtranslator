package com.example.synthtranslator;

import java.io.InputStream;

import com.example.synthtranslator.translator.SynthTranslator;
import com.example.synthtranslator.recorder.VoiceRecorder;

class SynthTranslatorLoop {
    private final SynthTranslator st = new SynthTranslator();
    private final VoiceRecorder vr = new VoiceRecorder();

    private String recognized_text;
    private String translated_text;
    private InputStream synthesized_stream;


}