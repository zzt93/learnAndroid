package com.example.zzt.tagdaily;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zzt.tagdaily.crypt.DecryptedFile;
import com.example.zzt.tagdaily.logic.Category;
import com.example.zzt.tagdaily.logic.Default;
import com.example.zzt.tagdaily.logic.FileUtility;
import com.example.zzt.tagdaily.logic.UIFileInfo;
import com.example.zzt.tagdaily.crypt.FileLink;
import com.example.zzt.tagdaily.logic.UriUtility;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WithFragment extends Activity implements
        FolderFragment.FolderFragmentInteractionListener,
        DetailFileFragment.DetailFragmentInteractionListener {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private static String thisClass = WithFragment.class.getCanonicalName();
    private FolderFragment folderFragment;
    private DetailFileFragment detailFragment;
    private ArrayList<UIFileInfo> fatherDirInfos = new ArrayList<>();
    private int fatherIndex = Default.DEFAULT_FOLDER_I;
    private HashMap<Integer, ArrayList<UIFileInfo>> childDirInfo = new HashMap<>();
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);

        Intent intent = getIntent();
        password = intent.getStringExtra(FirstActivity.PASSWORD);
        // set the secretKey for this fragment to encrypt/decrypt

        createAndInitDir(Category.values(), fatherDirInfos, childDirInfo);
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

            folderFragment = FolderFragment.newInstance(
                    fatherDirInfos, R.layout.with_icon,
                    new String[]{UIFileInfo.LOGO, UIFileInfo.NAME},
                    new int[]{R.id.logo, R.id.desc1}
            );
            detailFragment = DetailFileFragment.newInstance(childDirInfo.get(fatherIndex), "fragment");
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            folderFragment.setArguments(getIntent().getExtras());
            detailFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the '#fragment_container' FrameLayout
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction
                    .add(R.id.folder_fragment_container, folderFragment);
            fragmentTransaction
                    .add(R.id.file_fragment_container, detailFragment).commit();
        }

    }

    private void createAndInitDir(Category[] values, ArrayList<UIFileInfo> fatherDirInfos, HashMap<Integer, ArrayList<UIFileInfo>> childDirInfo) {
        File dir = this.getBaseDir();
        for (File file : dir.listFiles()) {
            System.out.println(file.getName());
        }
        for (Category cate : values) {
            File f = new File(this.getBaseDir(), cate.getName());
            if (!f.exists()) {
                boolean res = f.mkdir();
                if (!res) {
                    throw new RuntimeException("can't make dir");
                }
            }
            fatherDirInfos.add(new UIFileInfo(f, R.mipmap.ic_launcher));
        }

        // init detail file
        File f = new File(this.getBaseDir(), values[Default.DEFAULT_FOLDER_I].getName());
        ArrayList<UIFileInfo> defaultFiles = new ArrayList<>();
        childDirInfo.put(Default.DEFAULT_FOLDER_I, defaultFiles);
        collectFile(defaultFiles, f);
    }

    public static void collectFile(ArrayList<UIFileInfo> childDirInfo, File f) {
        if (!f.isDirectory()) {
            return;
        }
        for (File file : f.listFiles()) {
            UIFileInfo UIFileInfo;
            if (file.isDirectory()) {
                UIFileInfo = new UIFileInfo(file, R.drawable.ic_folder_open_black_24dp);
            } else {
                UIFileInfo = new UIFileInfo(file, R.drawable.ic_insert_drive_file_black_24dp);
            }
            childDirInfo.add(UIFileInfo);
        }
    }

    private File getBaseDir() {
        return this.getFilesDir();
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

        switch (id) {
            case R.id.with_action_search:
                File d = new File("/storage/emulated/0/DCIM/Camera/");
                File d1 = new File("/storage/emulated/0/DCIM/");
                for (File file1 : d.listFiles()) {
                    System.out.println(file1);
                }
                for (File file1 : d1.listFiles()) {
                    System.out.println(file1);
                }
                break;
            case R.id.with_action_add:
                // show chooser to show file
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // TODO show different app to add -- a global var to remember current category
                prepareType(intent);
                if (intentSafe(intent)) {
                    startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
                } else {
                    // remind use that no apps to open to add
                    Intent remind = new Intent(this, RemindDialog.class);
                    remind.putExtra(RemindDialog.REMIND_MSG, "no app to open root directory");
                    startActivity(remind);
                }
                break;
            case R.id.with_action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * prepare the intent for pick some file
     * <p/>
     * the app should show: camera, music, media, file manager
     *
     * @param intent The intent
     */
    private void prepareType(Intent intent) {
        intent.setType("*/*");
    }

    /**
     * The action will take after another activity reply to this one
     *
     * @param requestCode -- the key to get result
     * @param resultCode  -- result state
     * @param data        -- intent to get data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();

            if (BuildConfig.DEBUG) {
                FileUtility.deleteAllFiles(currentSelectedDir());
            }

            // create a file save the content under the related folder
            String path = UriUtility.getPath(this, fileUri);
            String name = FileLink.getNameFromPath(path);
            FileLink file;
            try {
                file = new FileLink(new File(currentSelectedDir(), name), path, password);
            } catch (IOException | NoSuchAlgorithmException e) {
                Log.e(thisClass, "File write failed: " + e.toString());
                return;
            }
            try {
                file.encrypt();
                // TODO: 11/4/15 recover it after check
//                file.deleteOriginal();
            } catch (IOException e) {
                Log.e(thisClass, "File encrypt failed: " + e.toString());
            } catch (NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeyException e) {
                Log.e(thisClass, " failed: " + e.toString());
            }
            folderFragmentClick(fatherIndex);
            // add to child
        }
    }

    private File currentSelectedDir() {
        return currentSelectedFile(fatherDirInfos, fatherIndex);
    }

    private boolean intentSafe(Intent intent) {
        //intent.resolveActivity(getPackageManager()) != null
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

    @Override
    public void folderFragmentClick(int position) {
        // TODO: 10/7/15 long time press listener
        Log.d(thisClass, position + " is choose");
        // update chosen index
        fatherIndex = position;
        // update detail info ui
        UIFileInfo UIFileInfo = fatherDirInfos.get(position);
        updateDetailFileInfo(UIFileInfo.toFile());
    }

    private void updateDetailFileInfo(File dir) {
        ArrayList<UIFileInfo> files = childDirInfo.get(fatherIndex);
        if (files == null) {
            files = new ArrayList<>();
            collectFile(files, dir);
        }

        detailFragment.clearListView();
        detailFragment.addListView(files)
                .notifyDataSetChanged();
    }

    @Override
    public void detailFragmentClick(int position) {
        // TODO: 10/7/15 handle long time press
        Log.d(thisClass, position + " is choose");

//        FolderFragment folders = (FolderFragment)
//                getFragmentManager().findFragmentById(R.id.folder_fragment_container);

        if (position >= childDirInfo.size()) {
            return;
        }
        UIFileInfo UIFileInfo = childDirInfo.get(fatherIndex).get(position);
        if (UIFileInfo.isDir()) {
            folderFragment.clearListView();
            folderFragment.addListView(childDirInfo.get(fatherIndex))
                    .notifyDataSetChanged();

            updateDetailFileInfo(UIFileInfo.toFile());
        } else {
            // decrypt file and show it
            FileLink fileLink;
            try {
                fileLink = new FileLink(UIFileInfo.toFile(), password);
            } catch (IOException e) {
                Log.e(thisClass, "can't read file" + e);
                return;
            }
            DecryptedFile decryptedFile;
            try {
                if (fileLink.largeLinkedSize()) {
                    decryptedFile = fileLink.decryptPart();
                } else {
                    fileLink.decryptAll();
                }
            } catch (InvalidAlgorithmParameterException e) {
                Log.e(thisClass, " failed: " + e.toString());
            } catch (InvalidKeyException e) {
                Log.e(thisClass, " failed: " + e.toString());
            } catch (NoSuchAlgorithmException e) {
                Log.e(thisClass, " failed: " + e.toString());
            } catch (IOException e) {
                Log.e(thisClass, " failed: " + e.toString());
            } catch (UnrecoverableEntryException e) {
                Log.e(thisClass, " failed: " + e.toString());
            }
            Uri path = Uri.parse(fileLink.getLinkedFilePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // TODO set type by suffix
            intent.setDataAndType(path, "*/*");

            if (intentSafe(intent)) {
                startActivity(intent);
            }

            // TODO: 11/4/15 will not run to here without view is finished?
            fileLink.deleteOriginal();
        }
    }
//
//    public Intent setIntentChooser(Intent intent, Uri path, String type) {
//        intent.setDataAndType(path, type);
//        String title = getResources().getString(R.string.chooser_title);
//        // Create intent to show chooser
//        return Intent.createChooser(intent, title);
//    }


    public File currentSelectedFile(ArrayList<UIFileInfo> UIFileInfos, int index) {
        return UIFileInfos.get(index).toFile();
    }
}
