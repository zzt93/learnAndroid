package com.example.zzt.tagdaily.logic.crypt;

import java.io.IOException;

import javax.crypto.CipherInputStream;

/**
 * Created by zzt on 10/11/15.
 * <p>
 * Usage:
 */
public class DecryptedFile {
    CipherInputStream cipherInputStream;

    public DecryptedFile(CipherInputStream cipherInputStream) {
        this.cipherInputStream = cipherInputStream;
    }

    public int read() throws IOException {
        return cipherInputStream.read();
    }

    public int available() throws IOException {
        return cipherInputStream.available();
    }

    public void mark(int readlimit) {
        cipherInputStream.mark(readlimit);
    }

    public void reset() throws IOException {
        cipherInputStream.reset();
    }

    public int read(byte[] buffer) throws IOException {
        return cipherInputStream.read(buffer);
    }

    public boolean markSupported() {
        return cipherInputStream.markSupported();
    }

    public long skip(long byteCount) throws IOException {
        return cipherInputStream.skip(byteCount);
    }

    public void close() throws IOException {
        cipherInputStream.close();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        return cipherInputStream.read(buf, off, len);
    }
}
