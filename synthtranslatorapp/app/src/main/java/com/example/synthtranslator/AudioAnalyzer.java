package com.example.synthtranslator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioAnalyzer {
    private ByteArrayInputStream byteArrayRawInputStream;
    private ByteArrayInputStream byteArrayProcessedInputStream;

    /**
     * Feeds to AudioAnalyzer new data of InputStream
     * @param is InputStream of synthesized audio
     */
    public void feedRawInputStream(InputStream is) {
        byteArrayRawInputStream = copyFromInputStream(is);
    }

    /**
     * Clears RawInputStream
     */
    private void clearRawInputStream() {
        byteArrayRawInputStream.reset();
    }

    /**
     * @param is inputstream to convert into ByteArrayInputStream
     * @return ByteArrayInputStream of InputStream
     */
    public ByteArrayInputStream copyFromInputStream(InputStream is) {
        ByteArrayOutputStream tempByteOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[16384];
        int cnt;

        try {
            while ((cnt = is.read(buffer, 0, buffer.length)) != -1) {
                tempByteOutputStream.write(buffer, 0, cnt);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return new ByteArrayInputStream(tempByteOutputStream.toByteArray());
    }
}
