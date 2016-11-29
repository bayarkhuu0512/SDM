package com.skytel.sdm.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.mindorks.paracamera.Camera;

public class CameraActivity extends Activity {
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera = new Camera(this);
        camera.builder()
                .setDirectory(Environment.getExternalStorageState())
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000);
        camera.takePicture();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
            //    picFrame.setImageBitmap(bitmap);
                Toast.makeText(this.getApplicationContext(),"Picture taken!",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
            Intent output = getIntent();
            output.putExtra("data",bitmap);
            setResult(RESULT_OK, output);
            finish();
        }

    }
}
