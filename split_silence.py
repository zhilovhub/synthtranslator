from pydub import AudioSegment
from pydub.silence import split_on_silence


sound_file = AudioSegment.from_wav('output.wav')
audio_chunks = split_on_silence(sound_file,
                                min_silence_len=250,
                                silence_thresh=-32,
                                keep_silence=2000
                                )


for i, chunk in enumerate(audio_chunks):
    out_file = f'chunk{i}.wav'
    print(f'Exporting {out_file}...')
    chunk.export(out_file, format='wav')
