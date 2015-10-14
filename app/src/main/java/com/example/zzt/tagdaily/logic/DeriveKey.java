package com.example.zzt.tagdaily.logic;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zzt on 10/14/15.
 * <p>
 * Usage: utility to derive key from password which user input
 */
public class DeriveKey {

    public static final int ITERATION_COUNT = 1000;

    /**
     * Note that for PBKDF2, you would be wise to keep to ASCII passwords only.
     * This is due to the fact that the PBKDF2 implementation by Oracle
     * does not use UTF-8 encoding.
     *
     * Used to derive the secret key
     *
     * @param password To derive key
     * @param keyLen Length
     * @param cryptAlgo the aim algorithm which use this secret key
     * @return SecretKey of cryptAlgo
     */
    public static SecretKey deriveSecretKey(String password, int keyLen, String cryptAlgo) {
        byte[] salt = getSalt(keyLen);
        return initSecretKey(password, keyLen, cryptAlgo, salt);
    }

    private static byte[] getSalt(int keyLen) {
        int saltLength = keyLen / 8; // same size as key output
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);
        return salt;
    }

    private static SecretKey initSecretKey(String password, int keyLen, String cryptAlgo, byte[] salt) {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                ITERATION_COUNT, keyLen);
        SecretKeyFactory keyFactory;
        byte[] keyBytes;
        try {
            keyFactory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        return new SecretKeySpec(keyBytes, cryptAlgo);
    }

    public static SecretKey deriveSecretKey(String password) {
        return deriveSecretKey(password, Crypt.KEY_BITS, Crypt.CRYPT_ALGO);
    }

    public static SecretKey recoverSecretKey(String password, byte[] salt) {
        return initSecretKey(password, Crypt.KEY_BITS, Crypt.CRYPT_ALGO, salt);
    }

    public static void main(String[] args) {
        // @tested salt is different every time invoke it
        getSalt(Crypt.KEY_BITS);
    }
}
