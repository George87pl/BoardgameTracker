package com.gmail.gpolomicz.boardgametracker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BGImport extends AppCompatActivity implements GetBggJsonData.OnDataAvailable {
    private static final String TAG = "GPDEB";

    EditText username;
    ListView importListView;
    Button buttonImport;
    List<BGEntry> data;
    boolean[] checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgimport);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        username = findViewById(R.id.bg_import);
        Button buttonSearch = findViewById(R.id.button_search);
        buttonImport = findViewById(R.id.button_import);
        buttonImport.setVisibility(View.GONE);
        importListView = findViewById(R.id.list_view_import);

        if(savedInstanceState != null) {
            username.setText(savedInstanceState.getString("username"));
            checked = savedInstanceState.getBooleanArray("checked");

            downloadContent();
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadContent();
            }
        });

    }

    @Override
    public void onDataAvailable(final List<BGEntry> data, DownloadStatus status) {

        BGImportAdapter adapter = new BGImportAdapter(BGImport.this, R.layout.record_import_item, data);
        importListView.setAdapter(adapter);

        if (status == DownloadStatus.OK) {

            this.data = data;

            if(checked != null) {
                for (int i=0; i<data.size(); i++) {
                    data.get(i).setChecked(checked[i]);
                }
            }

            buttonImport.setVisibility(View.VISIBLE);
            buttonImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ContentResolver contentResolver = getContentResolver();
                    ContentValues values = new ContentValues();

                    for (BGEntry game : data) {

                        if (game.isChecked()) {
                            if (game.getImage() != null) {
                                String mCurrentPhotoPath = null;
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

                                values.put(BoardgameContract.Columns.BOARDGAME_NAME, game.getName());
                                values.put(BoardgameContract.Columns.BOARDGAME_DESCRIPTION, game.getDescription());
                                values.put(BoardgameContract.Columns.BOARDGAME_RATING, game.getRating());
                                values.put(BoardgameContract.Columns.BOARDGAME_PUBDATE, game.getPubDate());
                                contentResolver.insert(BoardgameContract.CONTENT_URI, values);

                                new BGDownloadBitmap(mCurrentPhotoPath).execute(game.getImage());
                            }
                        }
                    }

                    Intent intent = new Intent(BGImport.this, CollectionActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            Log.e(TAG, "onDownloadComplete: failed " + status);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void downloadContent() {
        if (username.length() > 0) {
            if (isOnline()) {
                GetBggJsonData getBggJsonData = new GetBggJsonData("https://bgg-json.azurewebsites.net/collection/", BGImport.this);
                getBggJsonData.execute(username.getText().toString());

                hideKeyboardFrom(BGImport.this, username);

            } else {
                Toast.makeText(BGImport.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (username != null) {
            outState.putString("username", username.getText().toString());

            if(data != null && data.size() > 0) {
                checked = new boolean[data.size()];

                for (int i = 0; i < data.size(); i++) {
                    checked[i] = data.get(i).isChecked();
                }
                outState.putBooleanArray("checked", checked);
            }
        }
    }
}
