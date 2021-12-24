import pyaudio
import wave

from pydub import AudioSegment

chunks = 1024
sample_format = pyaudio.paInt16
channels = 1
fs = 16000
seconds = 5
filename = 'output.wav'


def save_voice(frames, p):
    print('Saving...')

    wf = wave.open(filename, 'wb')
    wf.setnchannels(channels)
    wf.setsampwidth(p.get_sample_size(sample_format))
    wf.setframerate(16000)
    wf.writeframes(b''.join(frames))
    wf.close()

    sound = AudioSegment.from_wav('output.wav')
    sound.export('output.pcm', format='s16le', bitrate='16k')


def record():
    p = pyaudio.PyAudio()

    stream = p.open(format=sample_format,
                    channels=channels,
                    rate=fs,
                    frames_per_buffer=chunks,
                    input=True)

    print('Recording...')
    frames = []

    try:
        while True:
            data = stream.read(1024)
            frames.append(data)
    except KeyboardInterrupt:
        save_voice(frames, p)
        print('Finishing...')

    stream.stop_stream()
    stream.close()

    p.terminate()


def main():
    record()


if __name__ == '__main__':
    main()
    input('конец')