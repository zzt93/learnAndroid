package com.example.zzt.tagdaily.fileChooser;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import com.example.zzt.tagdaily.BuildConfig;
import com.example.zzt.tagdaily.FirstActivity;
import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.RemindDialog;
import com.example.zzt.tagdaily.crypt.DecryptedFile;
import com.example.zzt.tagdaily.imageViewer.ImageActivity;
import com.example.zzt.tagdaily.logic.Category;
import com.example.zzt.tagdaily.logic.Default;
import com.example.zzt.tagdaily.logic.UIFileInfo;
import com.example.zzt.tagdaily.crypt.FileLink;
import com.example.zzt.tagdaily.logic.UriUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileChooserActivity extends Activity implements
        FolderFragment.FolderFragmentInteractionListener,
        DetailFileFragment.DetailFragmentInteractionListener {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private static final int DELETE_DECRYPTED_REQUEST_CODE = 2;
    public static final String IMAGE_PATH = "imagePath";

    private static String thisClass = FileChooserActivity.class.getCanonicalName();
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
        addFile(defaultFiles, f);
    }

    public static void addFile(ArrayList<UIFileInfo> childDirInfo, File f) {
        if (!f.isDirectory()) {
            return;
        }
        for (File file : f.listFiles()) {
            UIFileInfo uiFileInfo = createUiFileInfo(file);
            childDirInfo.add(uiFileInfo);
        }
    }

    private static UIFileInfo createUiFileInfo(File file) {
        UIFileInfo UIFileInfo;
        if (file.isDirectory()) {
            UIFileInfo = new UIFileInfo(file, R.drawable.ic_folder_open_black_24dp);
        } else {
            UIFileInfo = new UIFileInfo(file, R.drawable.ic_insert_drive_file_black_24dp);
        }
        return UIFileInfo;
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
                Intent intent = intentWithChooser(Intent.ACTION_GET_CONTENT);
                if (intentSafe(intent)) {
                    startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
                    Log.i(thisClass, "when will it run to here");
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
     * @param action The action string
     */
    private Intent intentWithChooser(String action) {
        Intent intent = new Intent(action);

        String title = getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(intent, title);

        intent.setType("*/*");
        return chooser;
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_FILE_REQUEST_CODE:
                    Uri fileUri = data.getData();

                    if (BuildConfig.DEBUG) {
//                FileUtility.deleteAllFiles(currentSelectedDir());
                    }

                    // create a file save the content under the related folder
                    String path = UriUtility.getPath(this, fileUri);
                    String name = FileLink.getNameFromPath(path);
                    FileLink file;
                    File saveFile;
                    try {
                        saveFile = new File(currentSelectedDir(), name);
                        file = new FileLink(saveFile, path, password);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        Log.e(thisClass, "File write failed: " + e.toString());
                        return;
                    }
                    try {
                        file.encrypt();
                        if (!file.deleteOriginal()) {
                            throw new RuntimeException("fail to delete original file");
                        }
                    } catch (IOException e) {
                        Log.e(thisClass, "File encrypt failed: " + e.toString());
                    } catch (NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeyException e) {
                        Log.e(thisClass, " failed: " + e.toString());
                    }
                    addToChildInfo(saveFile);
                    // update the view as if it is clicked
                    folderFragmentClick(fatherIndex);
                    // add to child
                    break;

                case DELETE_DECRYPTED_REQUEST_CODE:

                    Uri uri = data.getData();
                    String filePath = UriUtility.getPath(this, uri);
                    if (filePath != null) {
                        // TODO: 12/8/15 delete when destroy?
//                        new File(filePath).delete();
                    }

                    break;
            }
        } else {
            Log.e(thisClass, " fail to start activity " + data);
        }
    }

    private void addToChildInfo(File saveFile) {
        ArrayList<UIFileInfo> uiFileInfos = childDirInfo.get(fatherIndex);
        uiFileInfos.add(createUiFileInfo(saveFile));
        childDirInfo.put(fatherIndex, uiFileInfos);
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
            addFile(files, dir);
        }
//        ArrayList<UIFileInfo> files = new ArrayList<>();
//        addFile(files, dir);
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

        if (position >= childDirInfo.get(fatherIndex).size()) {
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
            } catch (UnrecoverableEntryException e) {
                Log.e(thisClass, " failed: " + e.toString());
            } catch (FileNotFoundException e) {
                Log.e(thisClass, " failed: " + e.toString());
            } catch (IOException e) {
                Log.e(thisClass, " failed: " + e.toString());

            }
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra(IMAGE_PATH, fileLink.getLinkedFilePath());

            if (intentSafe(intent)) {
                startActivityForResult(intent, DELETE_DECRYPTED_REQUEST_CODE);
            }

        }
    }

    private String getMimeType(String path) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(path);
        if (ext.isEmpty()) {
            ext = path.substring(path.lastIndexOf('.') + 1);
        }
        if (ext.equals("jpg")) {
            ext = "jpeg";
        }
        String mimeType = map.getMimeTypeFromExtension(ext);

        if (mimeType == null) {
            mimeType = "*/*";
        }
        return mimeType;
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
