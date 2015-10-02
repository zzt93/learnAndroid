package com.example.zzt.tagdaily;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zzt.tagdaily.logic.Category;
import com.example.zzt.tagdaily.logic.FileInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WithFragment extends Activity implements
        FolderFragment.FolderFragmentInteractionListener,
        DetailFileFragment.DetailFragmentInteractionListener {

    private static String thisClass = WithFragment.class.getCanonicalName();
    private FolderFragment folderFragment;
    private DetailFileFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);

        createIfNotExist(Category.values());
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.folder_fragment_container) != null
                && findViewById(R.id.file_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            folderFragment = FolderFragment.newInstance("first", "fragment");
            detailFragment = DetailFileFragment.newInstance("first", "fragment");
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            folderFragment.setArguments(getIntent().getExtras());
            detailFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction
                    .add(R.id.folder_fragment_container, folderFragment);
            fragmentTransaction
                    .add(R.id.file_fragment_container, detailFragment).commit();
        }

    }

    private void createIfNotExist(Category[] values) {
//        File
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with, menu);
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

    @Override
    public void folderFragmentClick(int position) {
        Log.d(thisClass, position + " is choose");

//        DetailFileFragment fileFragment = (DetailFileFragment)
//                getFragmentManager().findFragmentById(R.id.file_fragment_container);
        ArrayList<FileInfo> files = folderFragment.getListView();
        FileInfo fileInfo = files.get(position);
        updateDetailFileInfo(fileInfo.toFile());
    }

    private void updateDetailFileInfo(File dir) {
        ArrayList<FileInfo> files = new ArrayList<>();
        if (FirstActivity.debug) {
            File file = new File(this.getFilesDir(), "test");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            files.add(new FileInfo(file, R.drawable.ic_folder_open_black_24dp));
        }
        for (File f : dir.listFiles()) {
            FileInfo fileInfo;
            if (!f.isDirectory()) {
                fileInfo = new FileInfo(f, R.drawable.ic_insert_drive_file_black_24dp);
            } else {
                fileInfo = new FileInfo(f, R.drawable.ic_folder_open_black_24dp);
            }
            files.add(fileInfo);
        }

        detailFragment.clearListView();
        detailFragment.addListView(files)
                .notifyDataSetChanged();
    }

    @Override
    public void detailFragmentClick(int position) {
        Log.d(thisClass, position + " is choose");

//        FolderFragment folders = (FolderFragment)
//                getFragmentManager().findFragmentById(R.id.folder_fragment_container);

        ArrayList<FileInfo> files = detailFragment.getListView();
        FileInfo fileInfo = files.get(position);
        if (fileInfo.isDir()) {
            folderFragment.clearListView();
            folderFragment.addListView(files)
                    .notifyDataSetChanged();

            updateDetailFileInfo(fileInfo.toFile());
        } else {
            // de-encrypt file and show it
        }
    }
}
