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


class SynthTranslator:
    def __init__(self):
        self.folder_id = FOLDER_ID
        self.api_key = API_KEY


class Recorder:
    def __init__(self):
        self.count = 0

    def record(self):
        # сделать запись нормальной
        p = pyaudio.PyAudio()

        stream = p.open(format=SAMPLE_FORMAT,
                        channels=CHANNELS,
                        rate=FS,
                        frames_per_buffer=CHUNKS,
                        input=True)

        while True:
            print('Recording...')
            frames = []

            now = time.time()

            try:
                while True:
                    data = stream.read(1024)
                    frames.append(data)
                    print(time.time() - now)
                    if time.time() - now >= SECONDS:
                        self.save_voice(frames=frames, p=p, from_file='output.wav', to_file='output.pcm')
                        frames.clear()
                        now = time.time()
            except KeyboardInterrupt:
                print('Finishing...')

            stream.stop_stream()
            stream.close()

            p.terminate()

    def save_voice(self, frames, p, from_file, to_file):
        print('Saving...')

        wf = wave.open(filename, 'wb')
        wf.setnchannels(CHANNELS)
        wf.setsampwidth(p.get_sample_size(SAMPLE_FORMAT))
        wf.setframerate(16000)
        wf.writeframes(b''.join(frames))
        wf.close()

        sound = AudioSegment.from_wav(from_file)
        sound.export(to_file, format='s16le', bitrate='16k')

        main()


filename = 'output.wav'


def synthesize(text):
    url = 'https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize'
    headers = {
        "Authorization": f"Api-Key {API_KEY}",
    }

    data = {
        'text': text,
        'lang': 'en-US',
        'folderId': FOLDER_ID,
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


def translate(text):
    headers = {
        "Authorization": f"Api-Key {API_KEY}",
    }

    # Отправим запрос
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


def recognize(audio):
    with open(audio, "rb") as f:
        data = f.read()

    params = "&".join([
        "topic=general",
        "folderId=%s" % FOLDER_ID,
        "lang=ru-RU",
        "format=lpcm",
        "sampleRateHertz=16000"
    ])

    url = urllib.request.Request("https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?%s" % params, data=data)
    url.add_header("Authorization", "Api-Key %s" % API_KEY)

    response_data = urllib.request.urlopen(url).read().decode('UTF-8')
    decoded_data = json.loads(response_data)

    if decoded_data.get("error_code") is None:
        result = decoded_data.get("result")
    else:
        result = 'Не распознал'

    return result


def main():
    result = recognize('output.pcm')

    translated_result = translate(result)

    print(result)
    print(translated_result)

    with open('translated.pcm', 'wb') as f:
        for audio_content in synthesize(text=translated_result):
            f.write(audio_content)

    sound = AudioSegment.from_file(file='translated.pcm', sample_width=2, frame_rate=48000, channels=1)
    play(sound)


if __name__ == '__main__':
    synth_translator = SynthTranslator()
    recorder = Recorder()

    recorder.record()
    input('конец')
