package com.example.zzt.tagdaily.logic;

import java.io.File;

/**
 * Created by zzt on 11/3/15.
 * <p/>
 * Usage:
 */
public class FileUtility {
    public static boolean deleteFolder(File folder) {
        return folder.delete() & deleteAllFiles(folder);
    }

    public static boolean deleteAllFiles(File folder) {
        File[] files = folder.listFiles();
        boolean res = true;
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    res &= f.delete();
                }
            }
        }
        return res;
    }
}
