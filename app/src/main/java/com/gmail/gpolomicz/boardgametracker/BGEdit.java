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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BGEdit extends AppCompatActivity {
    private static final String TAG = "GPDEB";

    TextView rating;
    ImageView bg_image_edit;
    EditText bg_name_edit, bg_description_edit;
    SeekBar bg_rating_edit;
    BGEntry game;
    Uri selectedImage;
    String mCurrentPhotoPath;
    boolean newPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgedit);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bg_image_edit = findViewById(R.id.pl_image_edit);
        bg_name_edit = findViewById(R.id.pl_name_edit);
        bg_rating_edit = findViewById(R.id.bg_rating_edit);
        rating = findViewById(R.id.rating);
        bg_description_edit = findViewById(R.id.bg_description_edit);

        game = (BGEntry) getIntent().getSerializableExtra("game");

        if (game.getImage() != null) {
            Picasso.get().load("file://" + game.getImage()).into(bg_image_edit);
        }
        bg_name_edit.setText(game.getName());
        rating.setText(String.valueOf(game.getRating()));
        bg_description_edit.setText(game.getDescription());


        bg_rating_edit.setProgress((game.getRating()));
        bg_rating_edit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                rating.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
                if (bg_name_edit.length() > 0) {
                    values.put(BoardgameContract.Columns.BOARDGAME_NAME, bg_name_edit.getText().toString());
                    game.setName(bg_name_edit.getText().toString());

                    values.put(BoardgameContract.Columns.BOARDGAME_RATING, rating.getText().toString());
                    game.setRating(Integer.valueOf(rating.getText().toString()));

                    values.put(BoardgameContract.Columns.BOARDGAME_DESCRIPTION, bg_description_edit.getText().toString());
                    game.setDescription(bg_description_edit.getText().toString());

                } else {
                    Toast.makeText(this, "Boardgame name needed", Toast.LENGTH_SHORT).show();
                }

                if (newPhoto) {
                    if(game.getImage() != null) {
                        File file = new File(game.getImage());
                        boolean deleted = file.delete();
                    }

                    try {
                        Bitmap bitmap = ((BitmapDrawable)bg_image_edit.getDrawable()).getBitmap();
                        bitmap = getProportionalBitmap(bitmap, 400, "X");
                        createImageFile();

                        OutputStream fOut = new FileOutputStream(mCurrentPhotoPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.close(); // do not forget to close the stream

                        values.put(BoardgameContract.Columns.BOARDGAME_IMAGE, mCurrentPhotoPath);
                        game.setImage(mCurrentPhotoPath);


                    } catch (Exception e) {
                        Log.d(TAG, "onOptionsItemSelected: error");
                    }
                }

                int count = contentResolver.update(BoardgameContract.buildTaskUri(game.getId()), values, null, null);

                Intent intent = new Intent(BGEdit.this, BGDetails.class);
                intent.putExtra("game", game);
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
                    bg_image_edit.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    bg_image_edit.setImageURI(selectedImage);
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
}
