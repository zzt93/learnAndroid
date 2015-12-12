package com.example.zzt.tagdaily.logic.crypt;

import javax.crypto.SecretKey;

/**
 * Created by zzt on 10/14/15.
 * <p>
 * Usage:
 */
public class CryptInfo {
    private byte[] salt;
    private SecretKey secretKey;

    public CryptInfo(byte[] salt, SecretKey secretKey) {
        this.salt = salt;
        this.secretKey = secretKey;
    }

    public byte[] getSalt() {
        return salt;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
