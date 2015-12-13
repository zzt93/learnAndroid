package com.example.zzt.tagdaily.logic.fileChooser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.logic.crypt.FileEncryption;
import com.example.zzt.tagdaily.logic.mis.FileUtility;
import com.example.zzt.tagdaily.view.fileChooser.FileChooserActivity;
import com.example.zzt.tagdaily.view.fileChooser.InfoDialog;
import com.example.zzt.tagdaily.view.mis.DialogUtil;

import java.io.File;

/**
 * Created by zzt on 12/12/15.
 * <p/>
 * Usage:
 */
public class MenuDetail implements MenuOp {
    public static final String NAME = "name";
    public static final String SIZE = "size";
    public static final String TIME = "time";
    private static MenuOp detail;
    private final Context context;

    public MenuDetail(Context context) {
        this.context = context;
    }


    /**
     * MenuDetail will show the following information:
     * - original file name
     * - file size
     * - add time
     *
     * @param fileEncryption The object contain the information of encrypted file
     * @return is success
     */
    @Override
    public boolean operate(FileEncryption fileEncryption) {

        String filePath = fileEncryption.getLinkedFilePath();
        String nameFromPath = FileUtility.getNameFromPath(filePath);
        File encrypted = new File(fileEncryption.getEncryptedFilePath());
        String sizeStr = FileUtility.getFileSizeStr(encrypted);
        String addTime = FileUtility.getCreateTime(encrypted);

        Intent intent = new Intent(context, InfoDialog.class);
        intent.putExtra(NAME, nameFromPath);
        intent.putExtra(SIZE, sizeStr);
        intent.putExtra(TIME, addTime);

        context.startActivity(intent);

        return true;
    }

}
