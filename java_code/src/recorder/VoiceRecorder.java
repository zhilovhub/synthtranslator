package recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;

import translator.SynthTranslator;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final SynthTranslator synth_translator;
    private final AudioFormat audio_format = get_audio_format();
    private AudioInputStream audio_stream;
    private InputStream input_stream;
    private TargetDataLine target_data_line;
    private SourceDataLine source_data_line;
    private ByteArrayOutputStream output_stream;
    private final int seconds = 2;

    public VoiceRecorder(SynthTranslator synth_translator) {
        this.synth_translator = synth_translator;
    }

    private void play_audio() {
        try {
            byte audio_data[] = output_stream.toByteArray();

            input_stream = new ByteArrayInputStream(audio_data);
            audio_stream = new AudioInputStream(input_stream, audio_format, audio_data.length / this.audio_format.getFrameSize());

            DataLine.Info data_line_info = new DataLine.Info(SourceDataLine.class, audio_format);
            source_data_line = (SourceDataLine) AudioSystem.getLine(data_line_info);

            source_data_line.open(audio_format);
            source_data_line.start();

            Player player = new Player();
            player.start();
        } catch (LineUnavailableException e) {
            System.out.println("Error: " + e);
        }
    }

    public void capture_audio() {
        try {
            DataLine.Info data_line_info = new DataLine.Info(TargetDataLine.class, this.audio_format);
            this.target_data_line = (TargetDataLine) AudioSystem.getLine(data_line_info);

            this.target_data_line.open(this.audio_format);
            this.target_data_line.start();

            Capturer capturer = new Capturer();

            capturer.start();

        } catch (LineUnavailableException e) {
            System.out.println("Error: " + e);
        }
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

    private final class Player extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * audio_format.getChannels() * audio_format.getFrameSize()];
            int cnt;

            try {
                while ((cnt = audio_stream.read(temp_buffer, 0, temp_buffer.length)) != -1) {
                    source_data_line.write(temp_buffer, 0, cnt);
                }

                source_data_line.drain();
                source_data_line.stop();
                source_data_line.close();

                input_stream.close();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * audio_format.getChannels() * audio_format.getFrameSize()];
            output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = target_data_line.read(temp_buffer, 0, temp_buffer.length)) != -1) {
                output_stream.write(temp_buffer, 0, cnt);

                if (output_stream.size() >= audio_format.getFrameRate() * 2 * seconds) {
                    System.out.println(output_stream.size());
                    break;
                }
            }

            try {
                target_data_line.stop();
                target_data_line.drain();
                target_data_line.close();

                output_stream.close();
                play_audio();
            } catch (IOException ignored) {
                System.out.println("Error: " + ignored);
            }
        }
    }
}
