package com.example.zzt.tagdaily.crypt;

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

    public static final int ITERATION_COUNT = 2000;
    private static final int PASSWORD_ITERATIONS = 2000;
    private static final int PASSWORD_BITS = 160;

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
    public static CryptInfo deriveSecretKey(String password, int keyLen, String cryptAlgo) {
        byte[] salt = getSalt(keyLen);
        SecretKey secretKey = initSecretKey(password, keyLen, cryptAlgo, salt);
        return new CryptInfo(salt, secretKey);
    }

    public static byte[] getSalt(int keyLen) {
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
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            // TODO: 10/15/15 test whether upper secret key doesn't have a iv
            keyBytes = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        return new SecretKeySpec(keyBytes, cryptAlgo);
    }

    public static CryptInfo deriveSecretKey(String password) {
        return deriveSecretKey(password, Crypt.KEY_BITS, Crypt.CRYPT_ALGO);
    }

    public static SecretKey recoverSecretKey(String password, byte[] salt) {
        return initSecretKey(password, Crypt.KEY_BITS, Crypt.CRYPT_ALGO, salt);
    }

    public static String hashPassword(String password) {
        // generate random salt
        // use salt size at least as long as hash
        byte salt[] = DeriveKey.getSalt(Crypt.KEY_BITS);

        // generate Hash
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PASSWORD_ITERATIONS, PASSWORD_BITS);
        // we would like this to be "PBKDF2WithHmacSHA512" instead? which version of Android
        // Provider implements it?
        SecretKeyFactory skf;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("impossible", e);
        }
        byte[] hash;
        try {
            hash = skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("wrong key", e);
        }
        return Crypt.toBase64(salt) + Crypt.CHAR_NOT_BASE64 +   Crypt.toBase64(hash);
    }

    public static void main(String[] args) {
        // @tested salt is different every time invoke it
        getSalt(Crypt.KEY_BITS);
    }
}
