package com.example.zzt.tagdaily.logic;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by zzt on 10/5/15.
 * <p>
 * Usage:
 */
public class Crypt {


    public static final String CRYPT_ALGO = "AES";
    public static final String MODE_CRYPT = "CBC";
    public static final String PADDING = "PKCS5Padding";
    public static final String SPLITOR = "/";
    private final String alias;

    private Cipher cipher;
    // TODO add iv to output
    private byte[] iv;
    private String thisClass = this.getClass().getCanonicalName();

    public Crypt(String algo)
            throws NoSuchAlgorithmException, NoSuchPaddingException, KeyStoreException {
        KeyGenerator keygenerator = KeyGenerator.getInstance(algo);
        // TODO: 10/7/15 change key in regular (2**48 blocks -- 128bits for AES)
        SecretKey secretKey = keygenerator.generateKey();
        alias = toString();
        KeyStores.storeSecretKey(secretKey, alias);
        cipher = Cipher.getInstance(algo + SPLITOR + MODE_CRYPT + SPLITOR + PADDING);
    }


    public byte[] encrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);

            SecretKey secretKey = KeyStores.getSecretKey(alias);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            iv = cipher.getIV();
            Log.d(thisClass, toHex(iv));
            //            String s1 = new String(textEncrypted);
//            System.out.println(s1);
            return cipher.doFinal(text);

        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            Log.e("Crypt", "error: " + e);
        } catch (UnrecoverableEntryException | KeyStoreException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public byte[] decrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);

            SecretKey secretKey = KeyStores.getSecretKey(alias);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

//            String str = new String(textDecrypted);
//            System.out.println(str);
            return cipher.doFinal(text);
        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            e.printStackTrace();
            Log.e("Crypt", "error: " + e);
        } catch (InvalidAlgorithmParameterException | KeyStoreException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static void main(String[] args) {
        Crypt crypt;
        try {
            crypt = new Crypt(CRYPT_ALGO);
            byte[] encrypt = crypt.encrypt("a message");
            System.out.println(
                    Arrays.toString(crypt.decrypt(new String(encrypt, Default.ENCODING_UTF8))));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | KeyStoreException e) {
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

}
