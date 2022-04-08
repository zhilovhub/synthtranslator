package com.example.synthtranslator;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.IOException;
import java.io.InputStream;

import com.example.synthtranslator.translator.SynthTranslator;
import com.example.synthtranslator.recorder.VoiceRecorder;

class SynthTranslatorLoop {
    private final SynthTranslator st = new SynthTranslator();
    private final VoiceRecorder vr;

    private String recognized_text;
    private String translated_text;
    private InputStream synthesized_stream;

    SynthTranslatorLoop(AudioRecord recorder, AudioTrack player) {
        this.vr = new VoiceRecorder(recorder, player);
    }

    void startLoop() throws IOException {
        this.vr.captureAudio();
        this.vr.playAudio();

        while (true) {
//            System.out.println(vr.getAvailableBytesOfCapturing());
            if (vr.getAvailableBytesOfCapturing() >= 16000 * 2 * 4) {
//                recognized_text = st.recognize(vr.getVoiceStream());
//                translated_text = st.translate(recognized_text);
                vr.getVoiceStream();
                translated_text = "Hello I am testing my program. I should work for a long time";
                synthesized_stream = st.synthesize(translated_text);

                vr.updateAudioStream(synthesized_stream);
                System.out.println(synthesized_stream.available() + " available");

                System.out.println(recognized_text);
                System.out.println(translated_text);

//                while (true) {
////                    System.out.println(vr.getAvailableBytesOfSynthesizing());
//                    if (vr.getAvailableBytesOfSynthesizing() <= 16000 * 2 * 2) {
////                        recognized_text = st.recognize(vr.getVoiceStream());
////                        translated_text = st.translate(recognized_text);
//                        vr.getVoiceStream();
//                        translated_text = "Hello I am testing my program. I should work for a long time";
//                        synthesized_stream = st.synthesize(translated_text);
//
//                        System.out.println(recognized_text);
//                        System.out.println(translated_text);
//
//                        vr.updateAudioStream(synthesized_stream);
//                    }
//                    if (!st.checkLooping()) {
//                        break;
//                    }
//                }
                if (!st.checkLooping()) {
                    break;
                }
            }
        }
    }
}