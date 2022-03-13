package recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final Capturer capturer = new Capturer();
    private final AudioFormat audio_format_capturing = get_audio_format_capturing();
    private final AudioFormat audio_format_synthesizing = get_audio_format_synthesizing();
    private volatile AudioInputStream audio_stream;
    private InputStream input_stream;
    private TargetDataLine target_data_line;
    private SourceDataLine source_data_line;
    private ByteArrayOutputStream byte_output_stream;

    private boolean running = true;

    private AudioFormat get_audio_format_capturing() {
        return new AudioFormat(
                16000f,
                16,
                1,
                true,
                false
        );
    }

    private AudioFormat get_audio_format_synthesizing() {
        return new AudioFormat(
                48000f,
                16,
                1,
                true,
                false
        );
    }

    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * audio_format_capturing.getChannels() * audio_format_capturing.getFrameSize()];
            byte_output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = target_data_line.read(temp_buffer, 0, temp_buffer.length)) != -1 && running) {
                byte_output_stream.write(temp_buffer, 0, cnt);
            }
        }
    }

    public void capture_audio() {
        try {
            DataLine.Info data_line_info = new DataLine.Info(TargetDataLine.class, this.audio_format_capturing);
            this.target_data_line = (TargetDataLine) AudioSystem.getLine(data_line_info);

            this.target_data_line.open(this.audio_format_capturing);
            this.target_data_line.start();

            capturer.start();
        } catch (LineUnavailableException e) {
            System.out.println("Error: " + e);
        }
    }

    private final class Player extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * audio_format_synthesizing.getChannels() * audio_format_synthesizing.getFrameSize()];
            int cnt;

            try {
                while (true) {
                    if (audio_stream != null) {
                        cnt = audio_stream.read(temp_buffer, 0, temp_buffer.length);
                        if (cnt != -1)
                            source_data_line.write(temp_buffer, 0, cnt);
                    }

                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void update_audio_stream(InputStream is) {
        try {
            input_stream = new ByteArrayInputStream(is.readAllBytes());
            audio_stream = new AudioInputStream(input_stream, audio_format_synthesizing, input_stream.available() / this.audio_format_synthesizing.getFrameSize());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void play_audio() {
        try {
            DataLine.Info data_line_info = new DataLine.Info(SourceDataLine.class, audio_format_synthesizing);
            source_data_line = (SourceDataLine) AudioSystem.getLine(data_line_info);

            source_data_line.open(audio_format_synthesizing);
            source_data_line.start();

            Player player = new Player();
            player.start();
        } catch (LineUnavailableException e) {
            System.out.println("Error: " + e);
        }
    }

    public ByteArrayOutputStream get_voice_stream() {
        return byte_output_stream;
    }

    public void reset_voice_stream() {
        byte_output_stream.reset();
    }

    public void stop_capturing() {
        this.running = false;
    }

    public void keep_capturing() {
        this.running = true;
    }

    public int get_available_bytes_of_synthesizing() {
        int available_bytes = 0;

        try {
            available_bytes = this.input_stream.available();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return available_bytes;
    }

    public int get_available_bytes_of_capturing() {
        int available_bytes = 0;
        if (this.byte_output_stream != null)
            available_bytes = this.byte_output_stream.size();

        return available_bytes;
    }

    public void close_everything() {
        try {
            audio_stream.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            input_stream.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            target_data_line.stop();
            target_data_line.drain();
            target_data_line.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            source_data_line.drain();
            source_data_line.stop();
            source_data_line.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            byte_output_stream.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
