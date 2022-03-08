package recorder;

import javax.sound.sampled.AudioFormat;

import translator.SynthTranslator;

public class VoiceRecorder {
    private final SynthTranslator synth_translator;

    public VoiceRecorder(SynthTranslator synth_translator) {
        this.synth_translator = synth_translator;
    }

    private AudioFormat get_audio_format() {
        return new AudioFormat(
                16000f,
                16,
                1,
                true,
                false
        );
    }
}