package com.example.synthtranslator;

import java.util.Random;

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

    private String[] testRandomEnglishPhrases = new String[] {"This is a working player system Congrats!",
    "Hello I am testing my program. I should work for a long time",
    "Could you tell me how to get to the subway?",
    "If only I know",
    "sorry",
    "Hello",
    "What is the day today",
    "I like to play some games"};
    private String testPhrase = testRandomEnglishPhrases[new Random().nextInt(testRandomEnglishPhrases.length)];

    public void setAudioInstruments(AudioRecord audioRecord, AudioTrack audioTrack) {
        voiceRecorder.setAudioInstruments(audioRecord, audioTrack);
    }

    public void startLoop() {
        this.voiceRecorder.captureAudioThreadStart();
        this.voiceRecorder.playAudioThreadStart();

        while (!finished) {
            System.out.println(voiceRecorder.getAvailableBytesOfCapturing());
            while (!finished && isRunning) {
                System.out.println(voiceRecorder.getAvailableBytesOfCapturing() + " We are in level 1 inside");
                if (voiceRecorder.getAvailableBytesOfCapturing() >= 16000 * 2 * 4) {
                    //                recognized_text = st.recognize(vr.getVoiceStream());
                    //                translated_text = st.translate(recognized_text);
                    voiceRecorder.getVoiceStream();
                    testPhrase = testRandomEnglishPhrases[new Random().nextInt(testRandomEnglishPhrases.length)];
                    translated_text = testPhrase;
                    synthesized_stream = synthTranslator.synthesize(translated_text);

                    voiceRecorder.updateAudioStream(synthesized_stream);

                    System.out.println(recognized_text);
                    System.out.println(translated_text);

                    while (!finished && isRunning) {
                        if (voiceRecorder.getAvailableBytesOfCapturing() >= 16000 * 2 * 3 && voiceRecorder.getAvailableBytesOfSynthesizing() <= 16000 * 2 + 16000 && isRunning) {
                            //                        recognized_text = synthTranslator.recognize(vr.getVoiceStream());
                            //                        translated_text = synthTranslator.translate(recognized_text);
                            voiceRecorder.getVoiceStream();
                            testPhrase = testRandomEnglishPhrases[new Random().nextInt(testRandomEnglishPhrases.length)];
                            translated_text = testPhrase;
                            synthesized_stream = synthTranslator.synthesize(translated_text);

                            System.out.println(recognized_text);
                            System.out.println(translated_text);

                            voiceRecorder.updateAudioStream(synthesized_stream);
                        }
                    }
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