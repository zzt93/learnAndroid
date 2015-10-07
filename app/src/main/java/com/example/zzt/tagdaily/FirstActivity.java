package com.example.zzt.tagdaily;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class FirstActivity extends Activity {

    public static final String EXTRA_MESSAGE = "com.example.zzt.tagdaily.MESSAGE";
    public static boolean debug = false;
    private static String thisClass = FirstActivity.class.getName();

    /**
     * some fundamental setup for the activity,
     * such as declaring the user interface (defined in an XML layout file),
     * defining member variables, and configuring some of the UI.
     *
     * @param savedInstanceState -- the saved system state
     *
     * 1. In order for the Android system to restore the
     * state of the views in your activity,
     * each view must have a unique ID,
     * supplied by the android:id attribute.
     * 2. activity will be destroyed and recreated each time the user rotates the screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_first, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.with_action_search:
                if (debug) {
                    Log.d(thisClass, "action search");
                }
//                openSearch();
                return true;
            case R.id.with_action_add:
                if (debug) {
                    Log.d(thisClass, "action add");
                }
//                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String msg = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }

    public void showTransparent(View view) {
        Intent intent = new Intent(this, TransparentTheme.class);
        startActivity(intent);
    }

    public void showDialog(View view) {
        Intent intent = new Intent(this, RemindDialog.class);
        startActivity(intent);
    }

    public void showFragment(View view) {
        Intent intent = new Intent(this, WithFragment.class);
        startActivity(intent);
    }
}
