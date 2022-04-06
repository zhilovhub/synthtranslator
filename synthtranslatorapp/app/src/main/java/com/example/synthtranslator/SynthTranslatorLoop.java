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
        this.vr.playAudio();

        while (true) {
            if (vr.getAvailableBytesOfCapturing() >= 16000 * 2 * 4) {
                recognized_text = st.recognize(vr.get_voice_stream());
                translated_text = st.translate(recognized_text);
                synthesized_stream = st.synthesize(translated_text);

                vr.update_audio_stream(synthesized_stream);

                System.out.println(recognized_text);
                System.out.println(translated_text);

                while (true) {
                    if (vr.get_available_bytes_of_synthesizing() <= 16000 * 2 * 2) {
                        recognized_text = st.recognize(vr.get_voice_stream());
                        translated_text = st.translate(recognized_text);
                        synthesized_stream = st.synthesize(translated_text);

                        System.out.println(recognized_text);
                        System.out.println(translated_text);

                        vr.update_audio_stream(synthesized_stream);
                    }
                }
            }
        }
    }
}