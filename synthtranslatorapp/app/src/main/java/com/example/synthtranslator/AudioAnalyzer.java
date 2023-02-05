package com.example.synthtranslator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioAnalyzer {
    /**
     *
     * @param is inputstream to convert into ByteArrayInputStream
     * @return ByteArrayInputStream of InputStream
     */
    public static ByteArrayInputStream copyFromInputStream(InputStream is) {
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
