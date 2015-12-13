package com.example.zzt.tagdaily.logic.fileChooser;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.logic.crypt.FileEncryption;
import com.example.zzt.tagdaily.view.mis.DialogUtil;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

/**
 * Created by zzt on 12/12/15.
 * <p/>
 * Usage:
 */
public class MenuDelete implements MenuOp {

    private static MenuDelete delete;
    private final Context context;

    public MenuDelete(Context context) {
        this.context = context;
    }


    @Override
    public boolean operate(final FileEncryption fileEncryption) {
        final boolean[] res = new boolean[1];
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                res[0] = fileEncryption.deleteEncrypted() & fileEncryption.deleteSaveFile();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        };
        DialogUtil.getEnsureResult(context,
                context.getResources().getString(R.string.file_delete_prompt), okListener, cancelListener);
        return res[1];
    }
}
