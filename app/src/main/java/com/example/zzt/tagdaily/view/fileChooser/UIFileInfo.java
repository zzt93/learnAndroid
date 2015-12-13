package com.example.zzt.tagdaily.view.fileChooser;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.logic.mis.FileUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zzt on 10/2/15.
 * <p/>
 * Usage:
 * the class store the file information for ui
 */
public class UIFileInfo {
    public static final String NAME = "name";
    public static final String LOGO = "logo";
    public static final String LAST_MODIFIED = "lastModify";

    private File file;
    private int id;

    public UIFileInfo(File file, int id) {
        this.file = file;
        this.id = id;
    }

    public static UIFileInfo createUiFileInfo(File file) {
        UIFileInfo UIFileInfo;
        if (file.isDirectory()) {
            UIFileInfo = new UIFileInfo(file, R.drawable.ic_folder_open_black_24dp);
        } else {
            UIFileInfo = new UIFileInfo(file, R.drawable.ic_insert_drive_file_black_24dp);
        }
        return UIFileInfo;
    }

    public static ArrayList<UIFileInfo> addFileFrom(File dir) {
        if (!dir.isDirectory()) {
            throw new RuntimeException("wrong usage of directory");
        }

        ArrayList<UIFileInfo> uiFileInfos = new ArrayList<>();
        for (File file : dir.listFiles()) {
            UIFileInfo uiFileInfo = createUiFileInfo(file);
            uiFileInfos.add(uiFileInfo);
        }
        return uiFileInfos;
    }

    public HashMap<String, String> convertFileMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(NAME, file.getName());
        map.put(LAST_MODIFIED, FileUtility.getCreateTime(file));
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
