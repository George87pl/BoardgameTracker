package com.gmail.gpolomicz.boardgametracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class BGDownloadBitmap extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "GPDEB";

    private String mCurrentPhotoPath;

    BGDownloadBitmap(String currentPhotoPath) {
        mCurrentPhotoPath = currentPhotoPath;
    }

    protected Bitmap doInBackground(String... urls) {

        OutputStream fOut;
        Bitmap bitmap = null;

        try {
            URL url = new URL(urls[0]);
            fOut = new FileOutputStream(mCurrentPhotoPath);

            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.close(); // do not forget to close the stream

        } catch (Exception e) {
            Log.d(TAG, "onOptionsItemSelected: e: " + e);
        }
        return bitmap;
    }
}

