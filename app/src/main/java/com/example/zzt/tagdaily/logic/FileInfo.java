package com.example.zzt.tagdaily.logic;

import com.example.zzt.tagdaily.DetailFileFragment;
import com.example.zzt.tagdaily.FolderFragment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzt on 10/2/15.
 * <p>
 * Usage:
 */
public class FileInfo {
    private static DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
    private File file;
    private int id;

    public FileInfo(File file, int id) {
        this.file = file;
        this.id = id;
    }

    public HashMap<String, String> convertFileMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DetailFileFragment.TITLE, file.getName());
        map.put(DetailFileFragment.INFO, sdf.format(new Date(file.lastModified())));
        return map;
    }

    public HashMap<String, String> convertFolderMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(FolderFragment.LOGO, "" + id);
        map.put(FolderFragment.NAME, file.getName());
        return map;
    }

    public boolean isDir() {
        return file.isDirectory();
    }

    public File toFile() {
        return file;
    }
}
