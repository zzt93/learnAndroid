package com.example.zzt.tagdaily.logic;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by zzt on 10/6/15.
 * <p/>
 * Usage:
 * <p/>
 * an abstraction like a link <br>
 * its content is the information(now is path) of a target file
 * it will encrypt/decrypt target file
 */
public class FileLogic {

    private File file;
    private String path;

    public FileLogic(File file, String path) throws IOException {
        this.file = file;
        /**
         * when will file already exist:
         *  1. re-add a already added file
         *  2. reuse a added
         */
        if (file.exists()) {
            existFileLogic(file);
        }
        this.path = path;
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        outputStreamWriter.write(path);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    public FileLogic(File file) throws IOException {
        if (!file.exists()) {
            throw new RuntimeException("Invalid FileLogic");
        }
        existFileLogic(file);
    }

    private void existFileLogic(File file) throws IOException {
        this.file = file;
        BufferedReader br = new BufferedReader(new FileReader(file));
        path = br.readLine();
        br.close();
    }


    public static String getNameFromPath(String original) {
        return original.substring(original.lastIndexOf(File.separator) + 1);
    }

    public static String getDirFromPath(String original) {
        return original.substring(0, original.lastIndexOf(File.separator));
    }
    public static String[] getDirNameFromPath(String original) {
        int lastIndexOf = original.lastIndexOf(File.separator);
        return new String[]{
                original.substring(0, lastIndexOf),
                original.substring(lastIndexOf + 1)
        };
    }



    public void encrypt()
            throws FileNotFoundException {
        Cipher des = null;
        try {
            des = Cipher.getInstance(Crypt.CRYPT_ALGO + "/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e("", "wrong Cipher argument" + e);
        }
        // TODO encrypt that file under original folder and delete original file??
        String[] dirNameFromPath = getDirNameFromPath(path);
        String newPath = dirNameFromPath[0] + Default.DEFAULT_PREFIX + dirNameFromPath[1];
        FileOutputStream outputStream = new FileOutputStream(newPath);
        CipherOutputStream cipherOutputStream
                = new CipherOutputStream(outputStream, des);

    }

    public void decrypt() {

    }

    public String getPath() {
        return path;
    }
}
