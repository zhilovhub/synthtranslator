package com.example.synthtranslator.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final Capturer capturer = new Capturer();
    private final AudioFormat audio_format_synthesizing = get_audio_format_synthesizing();
    private final Activity context;
    private volatile AudioRecord recorder = getRecorder();
    private InputStream input_stream;
    private ByteArrayOutputStream byte_output_stream;

    private boolean running = true;

    VoiceRecorder(Activity context) {
        this.context = context;
    }

    private int getMinBufferSize() {
        return AudioRecord.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
    }

    private AudioRecord getRecorder() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECORD_AUDIO}, 1234);
            return null;
        }
        else {
            return new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    getMinBufferSize()
            );
        }
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
        ByteArrayOutputStream temp = new ByteArrayOutputStream();

        try {
            temp.write(this.byte_output_stream.toByteArray());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        this.byte_output_stream.reset();
        return temp;
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
