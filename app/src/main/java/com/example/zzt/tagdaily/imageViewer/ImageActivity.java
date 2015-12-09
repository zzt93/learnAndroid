package com.example.zzt.tagdaily.imageViewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.zzt.tagdaily.R;
import com.example.zzt.tagdaily.fileChooser.FileChooserActivity;
import com.squareup.picasso.Picasso;

public class ImageActivity extends Activity {

    private String path;
    private static String thisClass = ImageActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        String uriStr = intent.getStringExtra(FileChooserActivity.IMAGE_URI);
        path = intent.getStringExtra(FileChooserActivity.IMAGE_PATH);
        Uri uri = Uri.parse(uriStr);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Point size = getScreenSize();
        Picasso.with(this).load(uri).resize(size.x, size.y).into(imageView);
//            Picasso.with(this)
//                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
//                    .resize(100, 100)
//                    .into(imageView);

//            this method is too large for opengl to render
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            imageView.setImageURI(Uri.fromFile(imgFile));
//            imageView.setImageBitmap(myBitmap);

//            test image view
//            imageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e(thisClass, "home press");
                setReturn();
                break;
            default:
                throw new RuntimeException("no such item");

        }
        return super.onOptionsItemSelected(item);
    }

    private void setReturn() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FileChooserActivity.IMAGE_PATH, path);
        setResult(Activity.RESULT_OK, returnIntent);
//        finish();
    }

    private Point getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


}
