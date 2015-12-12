package com.example.zzt.tagdaily.logic.fileChooser;

import com.example.zzt.tagdaily.logic.crypt.FileEncryption;

/**
 * Created by zzt on 12/12/15.
 * <p/>
 * Usage:
 */
public class MenuDetail implements MenuOp {
    private static MenuOp detail;

    @Override
    public void operate(FileEncryption fileEncryption) {

    }

    public static MenuOp getInstance() {
        if (detail == null) {
            detail = new MenuDetail();
        }
        return detail;
    }
}
