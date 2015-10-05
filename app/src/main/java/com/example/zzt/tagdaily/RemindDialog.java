package com.example.zzt.tagdaily;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RemindDialog extends Activity {

    public static final String REMIND_MSG = "remind msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_dialog);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(REMIND_MSG);
        if (null == msg) {
            return;
        }
        TextView textView = (TextView) findViewById(R.id.remind_text_view);
        textView.setTextSize(30);
        textView.setText(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remind_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.with_action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
