package com.example.synthtranslator.recorder;

import android.media.AudioRecord;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final Capturer capturer = new Capturer();
//    private final AudioFormat audio_format_synthesizing = get_audio_format_synthesizing();
    private volatile AudioRecord recorder;
    private InputStream input_stream;
    private ByteArrayOutputStream byte_output_stream;
    private boolean running = true;

    public VoiceRecorder(AudioRecord recorder) {
        this.recorder = recorder;
    }

//    private AudioFormat get_audio_format_synthesizing() {
//        return new AudioFormat(
//                48000f,
//                16,
//                1,
//                true,
//                false
//        );
//    }
//
    private final class Capturer extends Thread {
        public void run() {
            recorder.startRecording();

            byte[] temp_buffer = new byte[1024 * 2];
            byte_output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = recorder.read(temp_buffer, 0, temp_buffer.length)) != -1 && running) {
                byte_output_stream.write(temp_buffer, 0, cnt);
            }
        }
    }

    public void captureAudio() {
        recorder.startRecording();
        capturer.start();
    }

//    private final class Player extends Thread {
//        public void run() {
//            byte[] temp_buffer = new byte[1024 * audio_format_synthesizing.getChannels() * audio_format_synthesizing.getFrameSize()];
//            int cnt;
//
//            try {
//                while (true) {
//                    if (audio_stream != null) {
//                        cnt = audio_stream.read(temp_buffer, 0, temp_buffer.length);
//                        if (cnt != -1)
//                            source_data_line.write(temp_buffer, 0, cnt);
//                    }
//
//                }
//            } catch (IOException e) {
//                System.out.println("Error: " + e);
//            }
//        }
//    }

//    public void updateAudioStream(InputStream is) {
//        try {
//            input_stream = new ByteArrayInputStream(is.readAllBytes());
//            audio_stream = new AudioInputStream(input_stream, audio_format_synthesizing, input_stream.available() / this.audio_format_synthesizing.getFrameSize());
//        } catch (IOException e) {
//            System.out.println("Error: " + e);
//        }
//    }

//    public void play_audio() {
//        try {
//            DataLine.Info data_line_info = new DataLine.Info(SourceDataLine.class, audio_format_synthesizing);
//            source_data_line = (SourceDataLine) AudioSystem.getLine(data_line_info);
//
//            source_data_line.open(audio_format_synthesizing);
//            source_data_line.start();
//
//            Player player = new Player();
//            player.start();
//        } catch (LineUnavailableException e) {
//            System.out.println("Error: " + e);
//        }
//    }

//    public ByteArrayOutputStream getVoiceStream() {
//        ByteArrayOutputStream temp = new ByteArrayOutputStream();
//
//        try {
//            temp.write(this.byte_output_stream.toByteArray());
//        } catch (IOException e) {
//            System.out.println("Error: " + e);
//        }
//
//        this.byte_output_stream.reset();
//        return temp;
//    }
//
//    public void stopCapturing() {
//        this.running = false;
//    }
//
//    public void keepCapturing() {
//        this.running = true;
//    }
//
//    public int getAvailableBytesOfSynthesizing() {
//        int available_bytes = 0;
//
//        try {
//            available_bytes = this.input_stream.available();
//        } catch (IOException e) {
//            System.out.println("Error: " + e);
//        }
//
//        return available_bytes;
//    }

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
