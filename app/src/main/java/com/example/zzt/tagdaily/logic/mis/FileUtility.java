package com.example.zzt.tagdaily.logic.mis;

import android.webkit.MimeTypeMap;

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

    public static String getMimeType(String path) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(path);
        if (ext.isEmpty()) {
            ext = path.substring(path.lastIndexOf('.') + 1);
        }
        if (ext.equals("jpg")) {
            ext = "jpeg";
        }
        String mimeType = map.getMimeTypeFromExtension(ext);

        if (mimeType == null) {
            mimeType = "*/*";
        }
        return mimeType;
    }

    /*
            File utility
         */
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
}
