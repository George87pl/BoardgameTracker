package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BGAdd extends AppCompatActivity {
    private static final String TAG = "GPDEB";

    TextView rating;
    SeekBar bg_rating_add;
    private EditText name, description;
    private ImageView image;
    private String imageUrl, mCurrentPhotoPath;
    Uri selectedImage;
    ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgadd);
        setTitle("New Game");

        name = findViewById(R.id.pl_name_edit);
        description = findViewById(R.id.bg_description_add);
        rating = findViewById(R.id.rating);
        bg_rating_add = findViewById(R.id.bg_rating_add);
        image = findViewById(R.id.pl_image_edit);

        bg_rating_add.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        if(savedInstanceState != null) {
            selectedImage = savedInstanceState.getParcelable("uri");
            image.setImageURI(selectedImage);
        }

        BGEntry game = (BGEntry) getIntent().getSerializableExtra("game");
        if (game != null) {
            Picasso.get().load(game.getImage()).into(image);
            name.setText(game.getName());
            description.setText(game.getDescription());
            imageUrl = game.getImage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bg_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.bg_add_search:
                Intent intent = new Intent(BGAdd.this, BGSearch.class);
                startActivity(intent);
                break;

            case R.id.bg_add_accept:

                if (name.getText().length() > 0) {

                    values = new ContentValues();

                    if (imageUrl != null) {
                        createImageFile();
                        saveData();
                        new DownloadBitmap().execute(imageUrl);
                    } else if (selectedImage != null) {
                        try {
                            createImageFile();
                            saveData();

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            bitmap = getProportionalBitmap(bitmap, 400, "X");

                            OutputStream fOut = new FileOutputStream(mCurrentPhotoPath);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.close(); // do not forget to close the stream

                            Intent done = new Intent(BGAdd.this, CollectionActivity.class);
                            startActivity(done);

                        } catch (Exception e) {
                            Log.d(TAG, "onOptionsItemSelected: error");
                        }
                    } else {
                        saveData();
                        Intent done = new Intent(BGAdd.this, CollectionActivity.class);
                        startActivity(done);
                    }
                } else {
                    Toast.makeText(this, "Game name is required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

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

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Intent done = new Intent(BGAdd.this, CollectionActivity.class);
            startActivity(done);
        }
    }

    public void pickPhoto(View v) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1); //one can be replaced with any action code
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    image.setImageURI(selectedImage);
                }

                break;
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
            values.put(BoardgameContract.Columns.BOARDGAME_IMAGE, mCurrentPhotoPath);

        } catch (IOException e) {
            Log.d(TAG, "onOptionsItemSelected: IOException " + e);
        }
    }

    void saveData() {
        ContentResolver contentResolver = getContentResolver();

        values.put(BoardgameContract.Columns.BOARDGAME_NAME, name.getText().toString());
        values.put(BoardgameContract.Columns.BOARDGAME_DESCRIPTION, description.getText().toString());
        values.put(BoardgameContract.Columns.BOARDGAME_RATING, rating.getText().toString());
        contentResolver.insert(BoardgameContract.CONTENT_URI, values);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(selectedImage != null) {
            outState.putParcelable("uri", selectedImage);
        }
    }
}
