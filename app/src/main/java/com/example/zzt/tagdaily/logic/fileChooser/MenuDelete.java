package com.example.zzt.tagdaily.logic.fileChooser;

import com.example.zzt.tagdaily.logic.crypt.FileEncryption;

/**
 * Created by zzt on 12/12/15.
 * <p/>
 * Usage:
 */
public class MenuDelete implements MenuOp {

    private static MenuDelete delete;

    private MenuDelete() {
    }

    public static MenuDelete getInstance() {
        if (delete == null) {
            delete = new MenuDelete();
        }
        return delete;
    }

    @Override
    public void operate(FileEncryption fileEncryption) {

    }
}
