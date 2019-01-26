package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerAdd extends AppCompatActivity {
    private static final String TAG = "GPDEB";

    private EditText name, note;
    private ImageView image;
    private String mCurrentPhotoPath;
    Uri selectedImage;
    ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_add);
        setTitle("New Player");

        name = findViewById(R.id.pl_name_edit);
        note = findViewById(R.id.player_note_add);
        image = findViewById(R.id.pl_image_edit);

        if (savedInstanceState != null) {
            selectedImage = savedInstanceState.getParcelable("uri");
            image.setImageURI(selectedImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_bg:

                if (name.getText().length() > 0) {

                    values = new ContentValues();

                    if (selectedImage != null) {
                        try {
                            createImageFile();
                            saveData();

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            bitmap = getProportionalBitmap(bitmap, 400, "X");

                            OutputStream fOut = new FileOutputStream(mCurrentPhotoPath);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.close(); // do not forget to close the stream

                            Intent done = new Intent(PlayerAdd.this, PlayerList.class);
                            startActivity(done);

                        } catch (Exception e) {
                            Log.d(TAG, "onOptionsItemSelected: error");
                        }
                    } else {
                        saveData();
                        Intent done = new Intent(PlayerAdd.this, PlayerList.class);
                        startActivity(done);
                    }
                } else {
                    Toast.makeText(this, "Player name is required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pickPhoto(View v) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1); //one can be replaced with any action code
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    image.setImageURI(selectedImage);
                }
                break;
        }
    }

    public Bitmap getProportionalBitmap(Bitmap bitmap, int newDimensionXorY, String XorY) {
        if (bitmap == null) {
            return null;
        }

        float xyRatio = 0;
        int newWidth = 0;
        int newHeight = 0;

        if (XorY.toLowerCase().equals("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        } else if (XorY.toLowerCase().equals("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }

    void createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */);

            mCurrentPhotoPath = image.getAbsolutePath();
            values.put(PlayerContract.Columns.PLAYER_IMAGE, mCurrentPhotoPath);

        } catch (IOException e) {
            Log.d(TAG, "onOptionsItemSelected: IOException " + e);
        }
    }

    void saveData() {
        ContentResolver contentResolver = getContentResolver();

        values.put(PlayerContract.Columns.PLAYER_NAME, name.getText().toString());
        values.put(PlayerContract.Columns.PLAYER_NOTE, note.getText().toString());

        contentResolver.insert(PlayerContract.CONTENT_URI, values);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(selectedImage != null) {
            outState.putParcelable("uri", selectedImage);
        }
    }
}
