package com.example.zzt.tagdaily.view.fileChooser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zzt.tagdaily.FirstActivity;
import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.RemindDialog;
import com.example.zzt.tagdaily.logic.crypt.DecryptedFile;
import com.example.zzt.tagdaily.logic.crypt.EncryptionException;
import com.example.zzt.tagdaily.logic.crypt.FileEncryption;
import com.example.zzt.tagdaily.logic.fileChooser.FileChooserBL;
import com.example.zzt.tagdaily.logic.fileChooser.MenuDelete;
import com.example.zzt.tagdaily.logic.fileChooser.MenuDetail;
import com.example.zzt.tagdaily.logic.fileChooser.MenuOp;
import com.example.zzt.tagdaily.logic.fileChooser.MenuRecover;
import com.example.zzt.tagdaily.logic.mis.Category;
import com.example.zzt.tagdaily.logic.mis.Default;
import com.example.zzt.tagdaily.logic.mis.FileUtility;
import com.example.zzt.tagdaily.logic.mis.UIFileInfo;
import com.example.zzt.tagdaily.logic.mis.UriUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class implements the view of file manager by folderFragment and detailFragment
 */
public class FileChooserActivity extends Activity implements
        FolderFragment.FolderFragmentInteractionListener,
        DetailFileFragment.DetailFragmentInteractionListener {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private static final int DELETE_DECRYPTED_REQUEST_CODE = 2;
    public static final String IMAGE_URI = "imageUri";
    public static final String IMAGE_PATH = "imagePath";

    private static String thisClass = FileChooserActivity.class.getCanonicalName();
    private FolderFragment folderFragment;
    private DetailFileFragment detailFragment;

    private ArrayList<UIFileInfo> fatherDirInfos = new ArrayList<>();
    private int fatherIndex = Default.DEFAULT_FOLDER_I;
    private HashMap<Integer, ArrayList<UIFileInfo>> childDirInfo = new HashMap<>();

    /**
     * This field have to be static because if I jump to another
     * activity and back, this activity may be re-created without password
     */
    private static String password;
    private static ArrayList<String> delPaths = new ArrayList<>();
    private FileChooserBL fileChooserBL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);

        Intent intent = getIntent();
        if (password == null) {
            password = intent.getStringExtra(FirstActivity.PASSWORD);
        }
        if (password == null) {
            showPasswordDialog();
        }

        // set the secretKey for this fragment to encrypt/decrypt
        fileChooserBL = new FileChooserBL(this);
        fileChooserBL.createAndInitDir(Category.values(), fatherDirInfos, childDirInfo);
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

    private void showPasswordDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Password");
        alert.setMessage("Enter password again");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                password = input.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString(FirstActivity.PASSWORD, password);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            password = savedInstanceState.getString(FirstActivity.PASSWORD);
        }
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
                    Log.i(thisClass, file1.getName());
                }
                for (File file1 : d1.listFiles()) {
                    Log.i(thisClass, file1.getName());
                }
                break;
            case R.id.with_action_add:
                // show chooser to show file
                Intent intent = IntentUtil.intentWithChooser(Intent.ACTION_GET_CONTENT);
                if (IntentUtil.intentSafe(intent, this)) {
                    startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
                    Log.i(thisClass, "when will app run to here");
                } else {
                    // remind use that no apps to open to add
                    Intent remind = new Intent(this, RemindDialog.class);
                    remind.putExtra(RemindDialog.REMIND_MSG, "no app to get content");
                    startActivity(remind);
                }
                break;
            case R.id.with_action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
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
        Log.i(thisClass, "in FileChooser result");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_FILE_REQUEST_CODE:
                    Uri fileUri = data.getData();

//                    if (BuildConfig.DEBUG) {
//                FileUtility.deleteAllFiles(currentSelectedDir());
//                    }

                    // create a file save the content under the related folder
                    String path = UriUtility.getPath(this, fileUri);
                    String name = FileUtility.getNameFromPath(path);
                    FileEncryption file;
                    File saveFile;
                    try {
                        saveFile = fileChooserBL.getEncryptedFilePath(currentSelectedDir(), name);
                        file = new FileEncryption(saveFile, path, fileUri.toString(), password);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        Log.e(thisClass, "File write failed: " + e.toString());
                        return;
                    }
                    try {
                        file.encrypt();
                        if (!file.deleteOriginal()) {
                            throw new EncryptionException("fail to delete original file");
                        }
                    } catch (IOException e) {
                        Log.e(thisClass, "File encrypt failed: " + e.toString());
                    } catch (NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeyException e) {
                        Log.e(thisClass, " failed: " + e.toString());
                    }
                    // add new file to child list
                    addToChildInfo(saveFile);
                    // update the view as if it is clicked
                    folderFragmentClick(fatherIndex);
                    break;

                case DELETE_DECRYPTED_REQUEST_CODE:

                    String filePath = data.getStringExtra(IMAGE_PATH);
                    if (filePath != null) {
                        if (!new File(filePath).delete()) {
                            throw new EncryptionException("fail to delete original file");
                        }
                    }

                    break;
            }
        } else {
            Log.e(thisClass, " fail to start activity " + data);
        }
    }

    private void addToChildInfo(File saveFile) {
        ArrayList<UIFileInfo> uiFileInfos = childDirInfo.get(fatherIndex);
        uiFileInfos.add(UIFileInfo.createUiFileInfo(saveFile));
        childDirInfo.put(fatherIndex, uiFileInfos);
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
            UIFileInfo.addFile(files, dir);
        }
//        ArrayList<UIFileInfo> files = new ArrayList<>();
//        addFile(files, dir);
        detailFragment.clearListView();
        detailFragment.addListView(files)
                .notifyDataSetChanged();
    }

    @Override
    public void detailFragmentClick(int position) {
        Log.d(thisClass, position + " is choose");


        if (position >= childDirInfo.get(fatherIndex).size()) {
            throw new RuntimeException("position < childDirInfo.get(fatherIndex).size()");
        }
        UIFileInfo UIFileInfo = childDirInfo.get(fatherIndex).get(position);
        if (UIFileInfo.isDir()) {
            folderFragment.clearListView();
            folderFragment.addListView(childDirInfo.get(fatherIndex))
                    .notifyDataSetChanged();

            updateDetailFileInfo(UIFileInfo.toFile());
        } else {
            // decrypt file and show it
            FileEncryption fileEncryption;
            try {
                fileEncryption = new FileEncryption(UIFileInfo.toFile(), password);
            } catch (IOException e) {
                Log.e(thisClass, "can't read file" + e);
                return;
            }
            DecryptedFile decryptedFile;
            try {
                if (fileEncryption.largeLinkedSize()) {
                    decryptedFile = fileEncryption.decryptPart();
                } else {
                    fileEncryption.decryptAll();
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

            // add file path of to be deleted file
            delPaths.add(fileEncryption.getEncryptedFilePath());

            // start app to view different file
//            Intent intent = getMyImgIntent(this, fileEncryption);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(fileEncryption.getFileUri()),
                    FileUtility.getMimeType(fileEncryption.getLinkedFilePath()));

            if (IntentUtil.intentSafe(intent, this)) {
                startActivityForResult(intent, DELETE_DECRYPTED_REQUEST_CODE);
            }

        }
    }

    @Override
    public void detailFragmentLongPress(int position) {
        Log.d(thisClass, position + " is choose");

        UIFileInfo UIFileInfo = childDirInfo.get(fatherIndex).get(position);
        if (UIFileInfo.isDir()) {
        } else {
            FileEncryption fileEncryption;
            try {
                fileEncryption = new FileEncryption(UIFileInfo.toFile(), password);
            } catch (IOException e) {
                Log.e(thisClass, "can't read file" + e);
                return;
            }
            showMenuDialog(fileEncryption);
        }
    }

    private void showMenuDialog(final FileEncryption fileEncryption) {
        final CharSequence[] operations = {
                getString(R.string.file_delete),
                getString(R.string.file_recover),
                getString(R.string.file_detail)
        };
        final CharSequence[] operationsPrompt = {
                getString(R.string.file_deleted),
                getString(R.string.file_recovered),
                getString(R.string.file_detail_show)
        };
        final MenuOp[] menuOps = {
                MenuDelete.getInstance(),
                MenuDetail.getInstance(),
                MenuRecover.getInstance(),
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(operations, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                menuOps[item].operate(fileEncryption);
                Toast.makeText(getApplicationContext(), operationsPrompt[item], Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private File currentSelectedDir() {
        return currentSelectedFile(fatherDirInfos, fatherIndex);
    }

    private File currentSelectedFile(ArrayList<UIFileInfo> UIFileInfos, int index) {
        return UIFileInfos.get(index).toFile();
    }
}
