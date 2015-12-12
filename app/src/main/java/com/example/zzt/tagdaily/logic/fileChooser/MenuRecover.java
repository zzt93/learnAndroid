package com.example.zzt.tagdaily.logic.fileChooser;

import com.example.zzt.tagdaily.logic.crypt.FileEncryption;

/**
 * Created by zzt on 12/12/15.
 * <p/>
 * Usage:
 */
public class MenuRecover implements MenuOp {
    private static MenuOp recover;

    @Override
    public void operate(FileEncryption fileEncryption) {

    }

    public static MenuOp getInstance() {
        if (recover == null) {
            recover = new MenuRecover();
        }
        return recover;
    }
}
