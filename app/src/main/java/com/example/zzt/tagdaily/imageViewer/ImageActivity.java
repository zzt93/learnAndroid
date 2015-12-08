package com.example.zzt.tagdaily.imageViewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.fileChooser.FileChooserActivity;

import java.io.File;

public class ImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        String path = intent.getStringExtra(FileChooserActivity.IMAGE_PATH);
        File imgFile = new File(path);

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(myBitmap);
        }
    }
}
