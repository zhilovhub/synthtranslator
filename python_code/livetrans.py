import urllib.request
import requests
import pyaudio
import wave
import time
import json

from pydub import AudioSegment
from pydub.playback import play

from data import *
from settings import CHUNKS, SAMPLE_FORMAT, CHANNELS, FS, SECONDS

from threading import Thread

audio_length = None
start_time = None
flag = True


class SynthTranslator:
    """Перевод, распознавание, синтез"""

    def __init__(self):
        self.folder_id = FOLDER_ID
        self.api_key = API_KEY

    def recognize(self, audio):
        """Преобразование голоса в текст"""
        with open(audio, "rb") as f:
            data = f.read()

        params = "&".join([
            "topic=general",
            "folderId=%s" % self.folder_id,
            "lang=ru-RU",
            "format=lpcm",
            "sampleRateHertz=16000"
        ])

        url = urllib.request.Request("https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?%s" % params, data=data)
        url.add_header("Authorization", "Api-Key %s" % self.api_key)

        response_data = urllib.request.urlopen(url).read().decode('UTF-8')
        decoded_data = json.loads(response_data)

        if decoded_data.get("error_code") is None:
            result = decoded_data.get("result")
        else:
            result = 'Не распознал'

        return result

    def translate(self, text):
        """Перевод текста"""
        headers = {
            "Authorization": f"Api-Key {self.api_key}",
        }

        res = requests.post(
            "https://translate.api.cloud.yandex.net/translate/v2/translate",
            json={
                "sourceLanguageCode": 'ru',
                "targetLanguageCode": 'en',
                "format": 'PLAIN_TEXT',
                "texts": text
            },
            headers=headers)

        return json.loads(res.text)['translations'][0]['text']

    def synthesize(self, text):
        """Синтез текста"""
        url = 'https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize'
        headers = {
            "Authorization": f"Api-Key {self.api_key}",
        }

        data = {
            'text': text,
            'lang': 'en-US',
            'folderId': self.folder_id,
            'voice': 'nick',
            'speed': 1.2,
            'format': 'lpcm',
            'sampleRateHertz': 48000,
        }

        with requests.post(url, headers=headers, data=data, stream=True) as resp:
            if resp.status_code != 200:
                raise RuntimeError("Invalid response received: code: %d, message: %s" % (resp.status_code, resp.text))

            for chunk in resp.iter_content(chunk_size=None):
                yield chunk


class Recorder:
    """Запись голоса"""

    def __init__(self, synth_translator):
        self.count = 0
        self.synth_translator = synth_translator

    def record(self):
        """Запись голоса"""
        p = pyaudio.PyAudio()

        stream = p.open(format=SAMPLE_FORMAT,
                        channels=CHANNELS,
                        rate=FS,
                        frames_per_buffer=CHUNKS,
                        input=True)

        while True:
            # print('Recording...')
            frames = []

            now = time.time()

            try:
                while True:
                    data = stream.read(1024)
                    frames.append(data)
                    # print(time.time() - now)
                    if time.time() - now >= SECONDS and (audio_length is None or start_time is None) or \
                            audio_length and start_time and audio_length - time.time() <= 1.5 and flag:
                        self.save_voice(frames=frames, p=p, from_file='audios/output.wav', to_file='audios/output.pcm')
                        frames.clear()
                        now = time.time()
            except KeyboardInterrupt:
                # print('Finishing...')
                pass

            stream.stop_stream()
            stream.close()

            p.terminate()

    def save_voice(self, frames, p, from_file, to_file):
        """Сохранение записанного голоса и экспорт его в нужный формат"""
        global flag

        print('Saving...')
        flag = False

        wf = wave.open(filename, 'wb')
        wf.setnchannels(CHANNELS)
        wf.setsampwidth(p.get_sample_size(SAMPLE_FORMAT))
        wf.setframerate(16000)
        wf.writeframes(b''.join(frames))
        wf.close()

        sound = AudioSegment.from_wav(from_file)
        sound.export(to_file, format='s16le', bitrate='16k')

        # Переводы, воспроизведение голоса и т.д происходят в отдельном потоке
        main_loop_thread = Thread(target=main_loop, args=(self.synth_translator,))
        main_loop_thread.start()


filename = 'audios/output.wav'


def main_loop(synth_translator: SynthTranslator) -> None:
    """Основные действия с записанным голосом и воспроизведение перевода"""
    global start_time, audio_length, flag

    result = synth_translator.recognize('audios/output.pcm')
    print('Test:', result)

    translated_result = synth_translator.translate(result)

    print(translated_result)

    with open('audios/translated.pcm', 'wb') as f:
        for audio_content in synth_translator.synthesize(text=translated_result):
            f.write(audio_content)

    sound = AudioSegment.from_file(file='audios/translated.pcm', sample_width=2, frame_rate=48000, channels=1)
    audio_length = time.time() + sound.duration_seconds
    flag = True
    start_time = time.time()

    play(sound)


def main():
    synth_translator = SynthTranslator()
    recorder = Recorder(synth_translator)

    recorder.record()


if __name__ == '__main__':
    main()
    input('конец')
