package recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

import translator.SynthTranslator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final SynthTranslator synth_translator;
    private final AudioFormat audio_format = get_audio_format();
    private TargetDataLine target_data_line;
    private final int seconds = 4;

    public VoiceRecorder(SynthTranslator synth_translator) {
        this.synth_translator = synth_translator;
    }

    public String capture_audio() {
        try {
            DataLine.Info data_line_info = new DataLine.Info(TargetDataLine.class, this.audio_format);
            this.target_data_line = (TargetDataLine) AudioSystem.getLine(data_line_info);

            Capturer capturer = new Capturer();

            this.target_data_line.open(this.audio_format);
            this.target_data_line.start();

            capturer.start();

            this.target_data_line.drain();
            this.target_data_line.close();

        } catch (LineUnavailableException e) {
            System.out.println("Error: " + e);
        }

        return "Capturing finished!";
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

    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * audio_format.getChannels() * audio_format.getFrameSize()];
            ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = target_data_line.read(temp_buffer, 0, temp_buffer.length)) != -1) {
                output_stream.write(temp_buffer, 0, cnt);

                if (output_stream.size() >= audio_format.getFrameRate() * 2 * seconds) {
                    System.out.println(output_stream.size());
                    break;
                }
            }

            try {
                output_stream.close();
            } catch (IOException ignored) {
                System.out.println("Error: " + ignored);
            }
        }
    }
}
