package com.example.zzt.tagdaily.logic;


import android.util.Base64;
import android.util.Log;

import com.example.zzt.tagdaily.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by zzt on 10/5/15.
 * <p>
 * Usage:
 */
public class Crypt {


    private static final String thisClass = Crypt.class.getCanonicalName();
    public static final String CRYPT_ALGO = "AES";
    public static final String MODE_PADDING = "/CBC/PKCS5Padding";
    public static int BLOCK_SIZE;
    public static final String CHAR_NOT_BASE64 = "]";

    static {
        try {
            BLOCK_SIZE = Cipher.getInstance(CRYPT_ALGO + MODE_PADDING).getBlockSize();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(thisClass, "" + e);
            BLOCK_SIZE = 0;
        }
    }

    public static final int KEY_BYTES = BLOCK_SIZE;
    public static final int KEY_BITS = BLOCK_SIZE * 8;




    private Cipher cipher;
    private SecretKey secretKey;

    public Crypt(SecretKey secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException {
        this(Crypt.CRYPT_ALGO, secretKey);
    }

    private Crypt(String algo, SecretKey secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, KeyStoreException {
        this.secretKey = secretKey;
        // for now this class is used to encrypt password, so may be no need
        // to change one, so I make it only one for this class
        //using a password-based key-derivation function such as PBKDF #2, Bcrypt or Scrypt.
        cipher = Cipher.getInstance(algo + MODE_PADDING);
    }


    public String encrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            Log.d(thisClass, toHex(iv));
            if (BuildConfig.DEBUG && iv.length != BLOCK_SIZE) {
                throw new RuntimeException("iv.length != blockSize");
            }
            byte[] textEncrypted = cipher.doFinal(text);
            String s1 = new String(textEncrypted, Default.ENCODING_UTF8);
            System.out.println(s1);
            return toBase64(iv) + CHAR_NOT_BASE64 + toBase64(textEncrypted);

        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            Log.e(thisClass, "error: " + e);
        }
        return "";
    }

    public String decrypt(String s) throws NoSuchAlgorithmException {
        try {
            String[] split = s.split(CHAR_NOT_BASE64);
            byte[] iv = fromBase64(split[0]);
            byte[] encryptText = fromBase64(s.substring(BLOCK_SIZE));

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] textDecrypted = cipher.doFinal(encryptText);
            String str = new String(textDecrypted, Default.ENCODING_UTF8);
            System.out.println(str);
            return str;
        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            e.printStackTrace();
            Log.e(thisClass, "error: " + e);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        Crypt crypt;
        try {
            crypt = new Crypt(DeriveKey.deriveSecretKey("1234567890"));
            String encrypt = crypt.encrypt("a message");
            System.out.println(crypt.decrypt(encrypt));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public static String toHex(byte[] bytes) {
        StringBuilder buff = new StringBuilder();
        for (byte b : bytes) {
            buff.append(String.format("%02X", b));
        }

        return buff.toString();
    }

    public static String toBase64(byte[] raw) {
        return Base64.encodeToString(raw, Base64.NO_WRAP);
    }

    public static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }


}
