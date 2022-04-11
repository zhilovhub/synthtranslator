package com.example.synthtranslator;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.InputStream;

import com.example.synthtranslator.translator.SynthTranslator;
import com.example.synthtranslator.recorder.VoiceRecorder;

class SynthTranslatorLoop {
    private final SynthTranslator synthTranslator = new SynthTranslator();
    private final VoiceRecorder voiceRecorder = new VoiceRecorder();

    private String recognized_text;
    private String translated_text;
    private InputStream synthesized_stream;

    private boolean isRunning = true;
    private boolean finished = false;

    public void setAudioInstruments(AudioRecord audioRecord, AudioTrack audioTrack) {
        voiceRecorder.setAudioInstruments(audioRecord, audioTrack);
    }

    public void startLoop() {
        this.voiceRecorder.captureAudioThreadStart();
        this.voiceRecorder.playAudioThreadStart();
        boolean flag = true;

        while (!finished) {
            System.out.println(voiceRecorder.getAvailableBytesOfCapturing());
            if (voiceRecorder.getAvailableBytesOfCapturing() >= 16000 * 2 * 4 && isRunning && flag == true) {
//                recognized_text = st.recognize(vr.getVoiceStream());
//                translated_text = st.translate(recognized_text);
                voiceRecorder.getVoiceStream();
                translated_text = "This is a working player system Congrats!";
                synthesized_stream = synthTranslator.synthesize(translated_text);

                voiceRecorder.updateAudioStream(synthesized_stream);

                System.out.println(recognized_text);
                System.out.println(translated_text);

                while (!finished) {
                    System.out.println(voiceRecorder.getAvailableBytesOfCapturing());
                    if (voiceRecorder.getAvailableBytesOfSynthesizing() <= 16000 * 2 * 2 && isRunning) {
//                        recognized_text = synthTranslator.recognize(vr.getVoiceStream());
//                        translated_text = synthTranslator.translate(recognized_text);
                        voiceRecorder.getVoiceStream();
                        translated_text = "Hello I am testing my program. I should work for a long time";
                        synthesized_stream = synthTranslator.synthesize(translated_text);

                        System.out.println(recognized_text);
                        System.out.println(translated_text);

                        voiceRecorder.updateAudioStream(synthesized_stream);
                    }
                    if (!synthTranslator.checkLooping()) {
                        break;
                    }
                }
                flag = false;
                if (!synthTranslator.checkLooping()) {
                    break;
                }
            }
        }
    }

    public void pauseLoop() {
        isRunning = false;
        voiceRecorder.pauseAudioInstruments();
    }

    public void continueLoop() {
        isRunning = true;
        voiceRecorder.continueAudioInstruments();
    }

    public void stopLoop() {
        isRunning = false;
        finished = true;
        voiceRecorder.closeResources();
        System.out.println("WE'VE CLOSED EVERYTHING!!");
    }
}