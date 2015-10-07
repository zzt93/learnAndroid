package com.example.zzt.tagdaily.fileChooser;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.zzt.tagdaily.FolderFragment;
import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.WithFragment;
import com.example.zzt.tagdaily.logic.FileInfo;

import java.io.File;
import java.util.ArrayList;

public class FilerChooser extends Activity {

    private FolderFragment folderFragment;
    private ArrayList<FileInfo> fatherDirInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filer_chooser);

        createAndInitDir(fatherDirInfos);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.folder_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            folderFragment = FolderFragment.newInstance(
                    fatherDirInfos, R.layout.file_chooser_row,
                    new String[]{FileInfo.LOGO, FileInfo.NAME, FileInfo.LAST_MODIFIED},
                    new int[]{R.id.logo, R.id.desc1, R.id.desc2});
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            folderFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction
                    .add(R.id.folder_fragment_container, folderFragment)
                    .commit();
        }
    }

    private void createAndInitDir(ArrayList<FileInfo> fatherDirInfos) {
        WithFragment.collectFile(fatherDirInfos, fileChooserBaseDir());
    }

    public static File fileChooserBaseDir() {
        return Environment.getDataDirectory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filer_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    public void openFile(String minmeType) {
//
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType(minmeType);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        // special intent for Samsung file manager
//        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
//        // if you want any file type, you can skip next line
//        sIntent.putExtra("CONTENT_TYPE", minmeType);
//        sIntent.addCategory(Intent.CATEGORY_DEFAULT);
//
//        Intent chooserIntent;
//        if (getPackageManager().resolveActivity(sIntent, 0) != null){
//            // it is device with samsung file manager
//            chooserIntent = Intent.createChooser(sIntent, "Open file");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
//        }
//        else {
//            chooserIntent = Intent.createChooser(intent, "Open file");
//        }
//
//        try {
//            startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
//        }
//    }
}
