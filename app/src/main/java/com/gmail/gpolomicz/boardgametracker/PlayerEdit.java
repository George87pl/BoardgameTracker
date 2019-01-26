package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerEdit extends AppCompatActivity {
    private static final String TAG = "GPDEB";

    ImageView pl_image_edit;
    EditText pl_name_edit, pl_note_edit;

    PlayerEntry player;
    Uri selectedImage;
    String mCurrentPhotoPath;
    boolean newPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_edit);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pl_image_edit = findViewById(R.id.pl_image_edit);
        pl_name_edit = findViewById(R.id.pl_name_edit);
        pl_note_edit = findViewById(R.id.pl_note_edit);

        player = (PlayerEntry) getIntent().getSerializableExtra("player");

        if (savedInstanceState != null) {
            selectedImage = savedInstanceState.getParcelable("uri");
            pl_image_edit.setImageURI(selectedImage);
            newPhoto = true;
        } else {
            if (player.getImage() != null) {
                Picasso.get().load("file://" + player.getImage()).into(pl_image_edit);
            }
        }

        pl_name_edit.setText(player.getName());
        pl_note_edit.setText(player.getNote());

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

                ContentResolver contentResolver = getContentResolver();

                ContentValues values = new ContentValues();
                if (pl_name_edit.length() > 0) {
                    values.put(PlayerContract.Columns.PLAYER_NAME, pl_name_edit.getText().toString());
                    player.setName(pl_name_edit.getText().toString());

                    values.put(PlayerContract.Columns.PLAYER_NOTE, pl_note_edit.getText().toString());
                    player.setNote(pl_note_edit.getText().toString());

                } else {
                    Toast.makeText(this, "Player name needed", Toast.LENGTH_SHORT).show();
                }

                if (newPhoto) {

                    if(player.getImage() != null) {
                        File file = new File(player.getImage());
                        boolean deleted = file.delete();
                    }

                    try {
                        Bitmap bitmap = ((BitmapDrawable) pl_image_edit.getDrawable()).getBitmap();
                        bitmap = getProportionalBitmap(bitmap, 400, "X");
                        createImageFile();

                        OutputStream fOut = new FileOutputStream(mCurrentPhotoPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.close(); // do not forget to close the stream

                        values.put(PlayerContract.Columns.PLAYER_IMAGE, mCurrentPhotoPath);
                        player.setImage(mCurrentPhotoPath);


                    } catch (Exception e) {
                        Log.d(TAG, "onOptionsItemSelected: error");
                    }
                }

                int count = contentResolver.update(PlayerContract.buildPlayerUri(player.getId()), values, null, null);

                Intent intent = new Intent(PlayerEdit.this, PlayerDetail.class);
                intent.putExtra("player", player);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pickPhoto(View v) {
        newPhoto = true;
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1); //one can be replaced with any action code
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    pl_image_edit.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    pl_image_edit.setImageURI(selectedImage);
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

        } catch (IOException e) {
            Log.d(TAG, "onOptionsItemSelected: IOException " + e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(selectedImage != null) {
            outState.putParcelable("uri", selectedImage);
        }
    }
}

