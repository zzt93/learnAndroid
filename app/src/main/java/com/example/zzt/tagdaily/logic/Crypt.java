package com.example.zzt.tagdaily.logic;


import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

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
    public static final String MODE_PADDING = "/CBC/PKCS5Padding";

    private final String thisClass = this.getClass().getCanonicalName();
    /**
     * the string as sign to store/get from KeyStore
     */
    private final String alias;

    private final int blockSize;
    private Cipher cipher;

    public Crypt(String algo)
            throws NoSuchAlgorithmException, NoSuchPaddingException, KeyStoreException {
        KeyGenerator keygenerator = KeyGenerator.getInstance(algo);
        // for now this class is used to encrypt password, so may be no need
        // to change one, so I make it only one for this class
        alias = thisClass;
        if (!KeyStores.hasAlias(alias)) {
            SecretKey secretKey = keygenerator.generateKey();
            KeyStores.storeSecretKey(secretKey, alias);
        }
        cipher = Cipher.getInstance(algo + MODE_PADDING);
        blockSize = cipher.getBlockSize();
    }


    public String encrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);

            SecretKey secretKey = KeyStores.getSecretKey(alias);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            Log.d(thisClass, toHex(iv));
            byte[] textEncrypted = cipher.doFinal(text);
            String s1 = new String(textEncrypted, Default.ENCODING_UTF8);
            System.out.println(s1);
            return toBase64(iv) + toBase64(textEncrypted);

        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            Log.e("Crypt", "error: " + e);
        } catch (UnrecoverableEntryException | KeyStoreException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String decrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] iv = fromBase64(s.substring(0, blockSize));
            byte[] encryptText = fromBase64(s.substring(blockSize));

            SecretKey secretKey = KeyStores.getSecretKey(alias);
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
            Log.e("Crypt", "error: " + e);
        } catch (InvalidAlgorithmParameterException | KeyStoreException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        Crypt crypt;
        try {
            crypt = new Crypt(CRYPT_ALGO);
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
