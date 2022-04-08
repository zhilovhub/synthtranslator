package com.example.synthtranslator.recorder;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final Capturer capturer = new Capturer();
    private final Player playerThread = new Player();
    private volatile AudioRecord recorder;
    private volatile AudioTrack player;
    private InputStream input_stream;
    private ByteArrayOutputStream byte_output_stream;

    public VoiceRecorder(AudioRecord recorder, AudioTrack player) {
        this.recorder = recorder;
        this.player = player;
    }

    private final class Capturer extends Thread {
        public void run() {
            recorder.startRecording();

            byte[] temp_buffer = new byte[1024 * 2];
            byte_output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = recorder.read(temp_buffer, 0, temp_buffer.length)) != -1) {
                byte_output_stream.write(temp_buffer, 0, cnt);
            }
        }
    }

    public void captureAudio() {
        recorder.startRecording();
        capturer.start();
    }

    private final class Player extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * 2];
            int cnt;

            try {
                while (true) {
                    if (input_stream != null) {
                        cnt = input_stream.read(temp_buffer, 0, temp_buffer.length);
                        if (cnt != -1)
                            player.write(temp_buffer, 0, cnt);
                    }

                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void updateAudioStream(InputStream is) {
        input_stream = is;
    }

    public void playAudio() {
        player.play();
        playerThread.start();
    }

    public ByteArrayOutputStream getVoiceStream() {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();

        try {
            temp.write(this.byte_output_stream.toByteArray());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        this.byte_output_stream.reset();
        return temp;
    }

    public int getAvailableBytesOfSynthesizing() {
        int available_bytes = 0;

        try {
            if (this.input_stream != null)
                available_bytes = this.input_stream.available();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return available_bytes;
    }

    public int getAvailableBytesOfCapturing() {
        int availableBytes = 0;

        if (byte_output_stream != null) {
            availableBytes = byte_output_stream.size();
        }
        return availableBytes;
    }

//    public void closeEverything() {
//        try {
//            audio_stream.close();
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//        }
//        try {
//            input_stream.close();
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//        }
//        try {
//            target_data_line.stop();
//            target_data_line.drain();
//            target_data_line.close();
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//        }
//        try {
//            source_data_line.drain();
//            source_data_line.stop();
//            source_data_line.close();
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//        }
//        try {
//            byte_output_stream.close();
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//        }
//    }
}
