package com.example.zzt.tagdaily.logic;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by zzt on 10/5/15.
 * <p/>
 * Usage:
 */
public class Crypt {


    private SecretKey desKey;
    private Cipher desCipher;

    public Crypt(String algo)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        KeyGenerator keygenerator = KeyGenerator.getInstance(algo);
        // TODO: 10/7/15 save desKey
        desKey = keygenerator.generateKey();
        desCipher = Cipher.getInstance(algo);
    }

    public String encrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);

            desCipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] textEncrypted = desCipher.doFinal(text);
            String s1 = new String(textEncrypted);
            System.out.println(s1);
            return s1;

        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            Log.e("Crypt", "error: " + e);
        }
        return s;
    }
    public String decrypt(String s) throws NoSuchAlgorithmException {
        try {
            byte[] text = s.getBytes(Default.ENCODING_UTF8);

            desCipher.init(Cipher.DECRYPT_MODE, desKey);
            byte[] textDecrypted = desCipher.doFinal(text);

            String str = new String(textDecrypted);
            System.out.println(str);
            return str;
        } catch (BadPaddingException
                | UnsupportedEncodingException
                | IllegalBlockSizeException
                | InvalidKeyException e) {
            Log.e("Crypt", "error: " + e);
        }
        return s;
    }
}
