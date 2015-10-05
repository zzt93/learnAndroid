package com.example.zzt.tagdaily.logic;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by zzt on 10/2/15.
 * <p>
 * Usage:
 */
public class FileInfo {
    public static final String NAME = "name";
    public static final String LOGO = "logo";
    public static final String LAST_MODIFIED = "lastModify";

    private static DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
    private File file;
    private int id;

    public FileInfo(File file, int id) {
        this.file = file;
        this.id = id;
    }

    public HashMap<String, String> convertFileMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(NAME, file.getName());
        map.put(LAST_MODIFIED, sdf.format(new Date(file.lastModified())));
        return map;
    }

    public HashMap<String, String> convertFolderMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(LOGO, "" + id);
        map.put(NAME, file.getName());
        return map;
    }

    public boolean isDir() {
        return file.isDirectory();
    }

    public File toFile() {
        return file;
    }
}
