package com.example.zzt.tagdaily.view.mis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.zzt.tagdaily.R;

/**
 * Created by zzt on 12/13/15.
 * <p/>
 * Usage:
 */
public class DialogUtil {
    public static void getEnsureResult(Context context, String ensureMes,
                                       DialogInterface.OnClickListener okListener,
                                       DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getResources().getString(R.string.file_confirm));
        alert.setMessage(ensureMes);

        alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), okListener);

        alert.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), cancelListener);
        alert.show();
    }
}
