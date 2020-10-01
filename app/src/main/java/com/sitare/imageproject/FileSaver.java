package com.sitare.imageproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class FileSaver {
    private final String TAG = "FileSaver";
    public File saveUriToFile(Context context, Uri uri) {
        SimpleDateFormat format = new SimpleDateFormat("YYYY_MM_dd_HH_mm");
        String imageDate = format.format(new Date());
        File file = new File(context.getFilesDir(), "/img_" + imageDate + ".jpg");
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, output);
            output.flush();
            output.close();
            Log.v(TAG, "File path: " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            Log.v(TAG, "Error" +e.getMessage());
            return null;
        }
    }
}
