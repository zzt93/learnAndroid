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

import com.example.zzt.tagdaily.logic.Category;
import com.example.zzt.tagdaily.logic.Crypt;
import com.example.zzt.tagdaily.logic.Default;
import com.example.zzt.tagdaily.logic.DeriveKey;
import com.example.zzt.tagdaily.logic.FileInfo;
import com.example.zzt.tagdaily.logic.FileLink;
import com.example.zzt.tagdaily.logic.UriUtility;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

public class WithFragment extends Activity implements
        FolderFragment.FolderFragmentInteractionListener,
        DetailFileFragment.DetailFragmentInteractionListener {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    public static final int PS_DEFAULT_VALUE = 0;
    private static String thisClass = WithFragment.class.getCanonicalName();
    private FolderFragment folderFragment;
    private DetailFileFragment detailFragment;
    private ArrayList<FileInfo> fatherDirInfos = new ArrayList<>();
    private int fatherIndex = Default.DEFAULT_FOLDER_I;
    private ArrayList<FileInfo> childDirInfo = new ArrayList<>();
    private SecretKey secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);

        Intent intent = getIntent();
        String password = intent.getStringExtra(FirstActivity.PASSWORD);
        // set the secretKey for this fragment to encrypt/decrypt
        secretKey = DeriveKey.deriveSecretKey(password);

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
                    new String[]{FileInfo.LOGO, FileInfo.NAME},
                    new int[]{R.id.logo, R.id.desc1}
            );
            detailFragment = DetailFileFragment.newInstance(childDirInfo, "fragment");
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

    private void createAndInitDir(Category[] values, ArrayList<FileInfo> fatherDirInfos, ArrayList<FileInfo> childDirInfo) {
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
            fatherDirInfos.add(new FileInfo(f, R.mipmap.ic_launcher));
        }

        // init detail file
        File f = new File(this.getBaseDir(), values[Default.DEFAULT_FOLDER_I].getName());
        collectFile(childDirInfo, f);
    }

    public static void collectFile(ArrayList<FileInfo> childDirInfo, File f) {
        if (!f.isDirectory()) {
            return;
        }
        for (File file : f.listFiles()) {
            FileInfo fileInfo;
            if (file.isDirectory()) {
                fileInfo = new FileInfo(file, R.drawable.ic_folder_open_black_24dp);
            } else {
                fileInfo = new FileInfo(file, R.drawable.ic_insert_drive_file_black_24dp);
            }
            childDirInfo.add(fileInfo);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST_CODE) if (resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            // create a file save the content under the related folder
            // TODO: 10/7/15 getPath ?
            String path = UriUtility.getPath(this, fileUri);
            String name = FileLink.getNameFromPath(path);
            FileLink file;
            try {
                file = new FileLink(new File(currentSelectedDir(), name), path, secretKey);
            } catch (IOException | NoSuchAlgorithmException e) {
                Log.e(thisClass, "File write failed: " + e.toString());
                return;
            }
            try {
                file.encrypt();
            } catch (IOException e) {
                Log.e(thisClass, "File write failed: " + e.toString());
            } catch (NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeyException e) {
                e.printStackTrace();
            }
            folderFragmentClick(fatherIndex);
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
        // TODO: 10/7/15 long time press
        Log.d(thisClass, position + " is choose");

        fatherIndex = position;

//        DetailFileFragment fileFragment = (DetailFileFragment)
//                getFragmentManager().findFragmentById(R.id.file_fragment_container);
        FileInfo fileInfo = fatherDirInfos.get(position);
        updateDetailFileInfo(fileInfo.toFile());
    }

    private void updateDetailFileInfo(File dir) {
        ArrayList<FileInfo> files = new ArrayList<>();

        collectFile(files, dir);

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
        FileInfo fileInfo = childDirInfo.get(position);
        if (fileInfo.isDir()) {
            folderFragment.clearListView();
            folderFragment.addListView(childDirInfo)
                    .notifyDataSetChanged();

            updateDetailFileInfo(fileInfo.toFile());
        } else {
            // decrypt file and show it
            FileLink fileLink;
            try {
                fileLink = new FileLink(fileInfo.toFile(), secretKey);
            } catch (IOException e) {
                Log.e(thisClass, "can't read file" + e);
                return;
            }
            try {
                fileLink.decryptAll();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            }
            Uri path = Uri.parse(fileLink.getLinkedFilePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // TODO set type by suffix
            intent.setDataAndType(path, "*/*");

            if (intentSafe(intent)) {
                startActivity(intent);
            }
            try {
                fileLink.encrypt();
            } catch (IOException e) {
                Log.e(thisClass, "" + e);
            } catch (NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeyException e) {
                Log.e(thisClass, "" + e);
                e.printStackTrace();
            }
        }
    }
//
//    public Intent setIntentChooser(Intent intent, Uri path, String type) {
//        intent.setDataAndType(path, type);
//        String title = getResources().getString(R.string.chooser_title);
//        // Create intent to show chooser
//        return Intent.createChooser(intent, title);
//    }


    public File currentSelectedFile(ArrayList<FileInfo> fileInfos, int index) {
        return fileInfos.get(index).toFile();
    }
}
