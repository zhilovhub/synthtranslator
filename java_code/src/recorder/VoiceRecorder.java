package recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

import java.io.ByteArrayOutputStream;

/**
 * Класс, отвечающий за запись звука с источника (микрофона)
 */
public class VoiceRecorder {
    private final AudioAnalyzer audioAnalyzer;

    private final Capturer capturer = new Capturer();
    private TargetDataLine targetDataLine;
    private final AudioFormat audioFormatCapturing = getAudioFormatCapturing();

    private boolean running = true;

    private int maxAmplitude = 0;

    private AudioFormat getAudioFormatCapturing() {
        return new AudioFormat(
                16000f,
                16,
                1,
                true,
                false
        );
    }

    /**
     * Constructor
     */
    public VoiceRecorder() {
        audioAnalyzer = new AudioAnalyzer(30, (int) audioFormatCapturing.getSampleRate(),
                audioFormatCapturing.getSampleSizeInBits() / 2, audioFormatCapturing.getChannels());
    }

    /**
     * Thread for recording audio
     */
    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[960];

            while (targetDataLine.read(temp_buffer, 0, temp_buffer.length) != -1 && running) {
                System.out.println(maxFromBuffer(temp_buffer));
                audioAnalyzer.feedRecordedRawSignal(temp_buffer, audioFormatCapturing.isBigEndian());
            }
        }
    }

    public void captureAudio() {
        try {
            DataLine.Info data_line_info = new DataLine.Info(TargetDataLine.class, this.audioFormatCapturing);
            this.targetDataLine = (TargetDataLine) AudioSystem.getLine(data_line_info);

            this.targetDataLine.open(this.audioFormatCapturing);
            this.targetDataLine.start();

            capturer.start();
        } catch (LineUnavailableException e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Calculates max amplitude value from byte buffer
     * @param buffer ByteBuffer
     * @return max amplitude value
     */
    private int maxFromBuffer(byte[] buffer) {
        short maxValue = 0;

        for (int i = 0; i < buffer.length / 2; i++) {
            short curSample = getShort(buffer[i * 2], buffer[i * 2 + 1]);
            if (curSample > maxValue) {
                maxValue = curSample;
            }
        }

        return maxValue;
    }

    /**
     * Transform two byte values into one short value (little-endian)
     * @param b1 small byte
     * @param b2 big byte
     * @return short value of two bytes
     */
    private short getShort(byte b1, byte b2) {
        return (short) (b1 | (b2 << 8));
    }


    /**
     * Calculates how mush seconds have already recorded
     * @return seconds count
     */
    public float getAvailableSecondsOfCapturing() {
        return audioAnalyzer.getAvailableSecondsOfCapturing();
    }

    /**
     * Returns recorded speech
     * @return ByteArrayOutputStream of audio
     */
    public ByteArrayOutputStream getVoiceStream() {
        return audioAnalyzer.getVoiceStream();
    }

    /**
     * Sets recordFlag to True
     */
    public void stopCapturing() {
        this.running = false;
    }

    /**
     * Close every resource. Recorder unable after calling this method
     */
    public void closeEverything() {
        try {
            targetDataLine.stop();
            targetDataLine.drain();
            targetDataLine.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * @return max amplitude value by the current moment
     */
    public int getAmplitude() {
        return maxAmplitude;
    }
}
