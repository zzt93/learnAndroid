package com.example.zzt.tagdaily.view.fileChooser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.example.zzt.tagdaily.R;

import java.util.List;

/**
 * Created by zzt on 12/12/15.
 */
public class IntentUtil {

    /**
     * prepare the intent for pick some file
     * <p/>
     * the app should show: camera, music, media, file manager
     *
     * @param action The action string
     */
    public static Intent intentWithChooser(String action) {
        Intent intent = new Intent(action);

        String title = Resources.getSystem().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(intent, title);

        intent.setType("*/*");
        return chooser;
    }
    //
//    public Intent setIntentChooser(Intent intent, Uri path, String type) {
//        intent.setDataAndType(path, type);
//        String title = getResources().getString(R.string.chooser_title);
//        // Create intent to show chooser
//        return Intent.createChooser(intent, title);
//    }


    public static boolean intentSafe(Intent intent, Context context) {
        //intent.resolveActivity(getPackageManager()) != null
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }


}
