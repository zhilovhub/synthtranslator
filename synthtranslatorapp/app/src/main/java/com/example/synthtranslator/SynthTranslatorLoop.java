package com.example.synthtranslator;

import android.os.AsyncTask;
import java.io.InputStream;

import com.example.synthtranslator.translator.SynthTranslator;

class SynthTranslatorLoop extends AsyncTask {
    private final SynthTranslator st = new SynthTranslator();

    private String recognized_text;
    private String translated_text;
    private InputStream synthesized_stream;

    @Override
    protected Object doInBackground(Object[] objects) {
        translated_text = st.translate(objects[0].toString());
        return null;
    }

}