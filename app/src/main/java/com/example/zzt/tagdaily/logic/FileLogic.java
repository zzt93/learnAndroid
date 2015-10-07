package com.example.zzt.tagdaily.logic;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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
        if (file.exists()) {
            return;
        }
        this.path = path;
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        outputStreamWriter.write(path);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    public FileLogic(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            throw new RuntimeException("Invalid FileLogic");
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        path = br.readLine();
        br.close();
    }


    public static String getNameFromPath(String original) {
        return original.substring(original.lastIndexOf(File.separator));
    }

    public void encrypt() {

    }

    public void decrypt() {

    }

    public String getPath() {
        return path;
    }
}
