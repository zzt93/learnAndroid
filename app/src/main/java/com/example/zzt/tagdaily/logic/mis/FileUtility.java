package com.example.zzt.tagdaily.logic.mis;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zzt on 11/3/15.
 * <p>
 * Usage:
 */
public class FileUtility {
    public static boolean deleteFolder(File folder) {
        return folder.delete() & deleteAllFiles(folder);
    }

    public static boolean deleteAllFiles(File folder) {
        File[] files = folder.listFiles();
        boolean res = true;
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
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

    private static String[] units = {"B", "KB", "MB", "GB"};
    private static int[] size = {1, 1024, 1024 * 1024, 1024 * 1024 * 1024};

    public static String getFileSizeStr(File file) {
        long length = file.length();
        int digitGroups = (int) (Math.log10(length) / Math.log10(1024));
        if(digitGroups > units.length - 1
                && digitGroups < 0) {
            throw new RuntimeException("invalid file size");
        }
        return new DecimalFormat("#,##0.#").format(length / size[digitGroups]) + " " + units[digitGroups];
    }

    private static DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

    public static String getCreateTime(File file) {
        return sdf.format(new Date(file.lastModified()));
    }
}
