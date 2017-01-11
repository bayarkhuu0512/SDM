package com.skytel.sdm.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by altanchimeg on 7/14/2016.
 */
public class BitmapSaver {
    public static void saveBitmapToFile(Bitmap bitmap, String imageName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM),
                imageName);
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);

            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File readBitmapFromFile(String imageName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM),
                imageName);
    }

}
