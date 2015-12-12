package com.example.zzt.tagdaily.logic.fileChooser;

import android.util.Log;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.logic.mis.Category;
import com.example.zzt.tagdaily.logic.mis.Default;
import com.example.zzt.tagdaily.logic.mis.UIFileInfo;
import com.example.zzt.tagdaily.view.fileChooser.FileChooserActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zzt on 12/12/15.
 * <p/>
 * The class handle the logic of file chooser
 */
public class FileChooserBL {

    FileChooserActivity fileChooserActivity;
    private String thisClass = FileChooserBL.class.getCanonicalName();

    public FileChooserBL(FileChooserActivity fileChooserActivity) {
        this.fileChooserActivity = fileChooserActivity;
    }

    public void createAndInitDir(Category[] values, ArrayList<UIFileInfo> fatherDirInfos, HashMap<Integer, ArrayList<UIFileInfo>> childDirInfo) {
        File dir = this.encryptedFileDir();
        for (File file : dir.listFiles()) {
            Log.i(thisClass, file.getName());
        }
        for (Category cate : values) {
            File f = new File(this.encryptedFileDir(), cate.getName());
            if (!f.exists()) {
                boolean res = f.mkdir();
                if (!res) {
                    throw new RuntimeException("can't make dir");
                }
            }
            fatherDirInfos.add(new UIFileInfo(f, R.mipmap.ic_launcher));
        }

        // init detail file
        File f = new File(this.encryptedFileDir(), values[Default.DEFAULT_FOLDER_I].getName());
        ArrayList<UIFileInfo> defaultFiles = new ArrayList<>();
        childDirInfo.put(Default.DEFAULT_FOLDER_I, defaultFiles);
        UIFileInfo.addFile(defaultFiles, f);
    }

    /**
     * Using internal storage to store the file of encrypted file
     *
     * @return The internal storage directory of this app
     * @see com.example.zzt.tagdaily.logic.crypt.FileEncryption#initEncryptedPath(String)
     */
    public File encryptedFileDir() {
        return fileChooserActivity.getFilesDir();
    }

    public File getEncryptedFilePath(File dir, String name) {
        return new File(dir, name);
    }
}
