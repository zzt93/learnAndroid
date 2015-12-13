package com.example.zzt.tagdaily.view.fileChooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.logic.fileChooser.MenuDetail;

public class InfoDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_dialog);

        setTitle(R.string.file_info);

        Intent intent = getIntent();
        TextView name = (TextView) findViewById(R.id.file_name_content);
        name.setText(intent.getStringExtra(MenuDetail.NAME));
        TextView size = (TextView) findViewById(R.id.file_size_content);
        size.setText(intent.getStringExtra(MenuDetail.SIZE));
        TextView time = (TextView) findViewById(R.id.file_time_content);
        time.setText(intent.getStringExtra(MenuDetail.TIME));

    }
}
