package com.example.zzt.tagdaily.crypt;


import android.util.Base64;
import android.util.Log;

import com.example.zzt.tagdaily.BuildConfig;
import com.example.zzt.tagdaily.logic.Default;

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
 * <p/>
 * Usage:
 */
public class Crypt {


    private static final String thisClass = Crypt.class.getCanonicalName();
    public static final String CRYPT_ALGO = "AES";
    public static final String MODE_PADDING = "/CBC/PKCS5Padding";
    public static int BLOCK_SIZE;
    /**
     * char not in base64 for android is + /
     */
    public static final char CHAR_NOT_BASE64 = ']';

    static {
        if (BuildConfig.DEBUG) {
            testCharForBase64();
        }
        try {
            BLOCK_SIZE = Cipher.getInstance(CRYPT_ALGO + MODE_PADDING).getBlockSize();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(thisClass, "" + e);
            BLOCK_SIZE = 0;
        }
    }

    private static void testCharForBase64() {
        char c = CHAR_NOT_BASE64;
//        https://en.wikipedia.org/wiki/Base64
        if (Character.isDigit(c) || Character.isLetter(c)
                || c == '+' || c == '/') {
            throw new RuntimeException("wrong char for base64");
        }
    }

    public static final int KEY_BYTES = BLOCK_SIZE;
    public static final int KEY_BITS = BLOCK_SIZE * 8;
    private final String algo;
    private final String password;


    public Crypt(String password)
            throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException {
        this(Crypt.CRYPT_ALGO, password);
    }

    /**
     * @param algo valid algorithm(only block cipher algorithm is valid):
     *             AES, DES, 3DES
     */
    private Crypt(String algo, String password) {
        this.algo = algo;
        this.password = password;
    }

    /**
     * Tested:
     * - Base64 usage
     * - salt/iv is random every time
     * - recover key is right
     */
    public static void testCrypt() {
        Crypt crypt;
        try {
            crypt = new Crypt("asdf");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
            return;
        }
        String s = "";
        String to_encrypt = "I am a student";
        try {
            s = crypt.encrypt(to_encrypt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            if (!to_encrypt.equals(crypt.decrypt(s))) {
                throw new RuntimeException("test wrong");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public String encrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);
            byte[] iv = DeriveKey.getRandomByte(KEY_BITS);

            Cipher cipher = Cipher.getInstance(algo + MODE_PADDING);
            CryptInfo cryptInfo = DeriveKey.deriveSecretKey(password);
            SecretKey secretKey = cryptInfo.getSecretKey();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            if (BuildConfig.DEBUG) {
                Log.d(thisClass, toHex(iv));
                if (iv.length != BLOCK_SIZE) {
                    throw new RuntimeException("iv.length != blockSize");
                }
            }
            byte[] textEncrypted = cipher.doFinal(text);
            String s1 = new String(textEncrypted, Default.ENCODING_UTF8);
            System.out.println(s1);
            // can't use new String(bytes, encoding) for no encoding those bytes
            // will be explained correctly
            return toBase64(cryptInfo.getSalt()) + CHAR_NOT_BASE64 +
                    toBase64(iv) + CHAR_NOT_BASE64 + toBase64(textEncrypted);

        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            Log.e(thisClass, "error: " + e);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String decrypt(String s) throws NoSuchAlgorithmException {
        try {
            String[] split = s.split("" + CHAR_NOT_BASE64);
            if (BuildConfig.DEBUG && split.length != 3) {
                throw new IllegalArgumentException("decrypt string is broken");
            }
            byte[] salt = fromBase64(split[0]);
            byte[] iv = fromBase64(split[1]);
            byte[] encryptText = fromBase64(split[2]);

            Cipher cipher = Cipher.getInstance(algo + MODE_PADDING);
            SecretKey secretKey = DeriveKey.recoverSecretKey(password, salt);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] textDecrypted = cipher.doFinal(encryptText);
            String str = new String(textDecrypted, Default.ENCODING_UTF8);
            if (BuildConfig.DEBUG) {
                System.out.println(str);
            }
            return str;
        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            e.printStackTrace();
            Log.e(thisClass, "error: " + e);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        Crypt crypt;
        try {
            crypt = new Crypt("1234567890");
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
