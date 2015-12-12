package com.example.zzt.tagdaily.logic.mis;

import com.example.zzt.tagdaily.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by zzt on 10/2/15.
 * <p>
 * Usage:
 * the class store the file information for ui
 */
public class UIFileInfo {
    public static final String NAME = "name";
    public static final String LOGO = "logo";
    public static final String LAST_MODIFIED = "lastModify";

    private static DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
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

    public static void addFile(ArrayList<UIFileInfo> uiFileInfos, File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            UIFileInfo uiFileInfo = createUiFileInfo(file);
            uiFileInfos.add(uiFileInfo);
        }
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
