package com.example.synthtranslator;

import java.util.Random;

import android.media.AudioRecord;
import android.media.AudioTrack;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;

import com.example.synthtranslator.translator.SynthTranslator;
import com.example.synthtranslator.recorder.VoiceRecorder;

class SynthTranslatorLoop {
    private final SynthTranslator synthTranslator = new SynthTranslator();
    private final VoiceRecorder voiceRecorder = new VoiceRecorder();

    private String recognized_text;
    private String translated_text;
    private InputStream synthesized_stream;

    private MutableLiveData<String> recognizedTextLiveData;
    private MutableLiveData<String> translatedTextLiveData;

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

    public SynthTranslatorLoop(MutableLiveData<String> recognizedTextLiveData, MutableLiveData<String> translatedTextLiveData) {
        this.recognizedTextLiveData = recognizedTextLiveData;
        this.translatedTextLiveData = translatedTextLiveData;
    }

    public void setAudioInstruments(AudioRecord audioRecord, AudioTrack audioTrack) {
        voiceRecorder.setAudioInstruments(audioRecord, audioTrack);
    }

    public void startLoop() {
        this.voiceRecorder.captureAudioThreadStart();
        this.voiceRecorder.playAudioThreadStart();

        while (!finished) {
//            System.out.println(voiceRecorder.getAvailableBytesOfCapturing());
            while (!finished && isRunning) {
//                System.out.println(voiceRecorder.getAvailableBytesOfCapturing() + " We are in level 1 inside");
                if (voiceRecorder.getAvailableBytesOfCapturing() >= 16000 * 2 * 4) {
                    recognized_text = synthTranslator.recognize(voiceRecorder.getVoiceStream());

                    if (recognized_text.equals("")) {
                        voiceRecorder.getVoiceStream();
                        System.out.println("Empty, we should wait and don't crush");
                        continue;
                    }

                    translated_text = synthTranslator.translate(recognized_text);

                    recognizedTextLiveData.postValue(recognized_text);
                    translatedTextLiveData.postValue(translated_text);
//                    testPhrase = testRandomEnglishPhrases[new Random().nextInt(testRandomEnglishPhrases.length)];
//                    translated_text = testPhrase;
                    synthesized_stream = synthTranslator.synthesize(translated_text);

                    voiceRecorder.updateAudioStream(synthesized_stream);

                    System.out.println(recognized_text);
                    System.out.println(translated_text);

                    while (!finished && isRunning) {
//                        System.out.println(voiceRecorder.getAvailableBytesOfCapturing() + " We are in level 2 inside");
                        if (voiceRecorder.getAvailableBytesOfCapturing() >= 16000 * 2 * 3 && voiceRecorder.getAvailableBytesOfSynthesizing() <= 16000 * 2 * 2.5 && isRunning) {
                            recognized_text = synthTranslator.recognize(voiceRecorder.getVoiceStream());

                            if (recognized_text.equals("")) {
                                voiceRecorder.getVoiceStream();
                                System.out.println("Empty, we should wait and don't crush");
                                continue;
                            }

                            translated_text = synthTranslator.translate(recognized_text);

                            recognizedTextLiveData.postValue(recognized_text);
                            translatedTextLiveData.postValue(translated_text);
//                            testPhrase = testRandomEnglishPhrases[new Random().nextInt(testRandomEnglishPhrases.length)];
//                            translated_text = testPhrase;
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

    public int getAmplitude() {
        return voiceRecorder.getAmplitude();
    }

    public void stopLoop() {
        isRunning = false;
        finished = true;
        voiceRecorder.closeResources();
        System.out.println("WE'VE CLOSED EVERYTHING!!");
    }
}