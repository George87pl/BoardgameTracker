package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayedAddResult extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GPDEB";

    public static final int LOADER_ID = 0;
    static final int REQUEST_TAKE_PHOTO = 1;
    ListView playerResult;
    SelectPlayerWinnerAdapter mAdapter;
    TextView game_select;
    EditText playedNote;
    ImageView image_select, playedImage;
    List<PlayerEntry> playerList;
    ArrayList<Integer> playerIds, winner;
    BGEntry game;
    PlayedEntry played;
    ContentValues values;
    String mCurrentPhotoPath;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_played_add_result);
        setTitle("New play");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        playerResult = findViewById(R.id.players_result);
        game_select = findViewById(R.id.game_name);
        image_select = findViewById(R.id.game_image);
        playedNote = findViewById(R.id.played_note);
        playedImage = findViewById(R.id.played_image_add);

        if (savedInstanceState != null) {
            winner = savedInstanceState.getIntegerArrayList("winner");

            if((selectedImage = savedInstanceState.getParcelable("uri")) != null) {
                playedImage.setImageURI(selectedImage);
                mCurrentPhotoPath = savedInstanceState.getString("path");
            }
        }

        playerIds = getIntent().getIntegerArrayListExtra("playerIds");
        game = (BGEntry) getIntent().getSerializableExtra("game");
        game_select.setText(game.getName());

        played = new PlayedEntry();
        values = new ContentValues();

        if (game.getImage() != null) {
            Picasso.get()
                    .load("file://" + game.getImage())
                    .fit()
                    .centerCrop()
                    .into(image_select);
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        playerResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (playerList.get(position).isWinner()) {
                    playerList.get(position).setWinner(false);
                } else {
                    playerList.get(position).setWinner(true);
                }

                mAdapter.notifyDataSetChanged();
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

                String date = new SimpleDateFormat("dd:MM:yyyy").format(new Date());

                values.put(PlayedContract.Columns.PLAYED_GAME, game.getId());
                values.put(PlayedContract.Columns.PLAYED_NOTE, playedNote.getText().toString());
                values.put(PlayedContract.Columns.PLAYED_DATE, date);

                if (selectedImage != null) {
                    values.put(PlayedContract.Columns.PLAYED_IMAGE, mCurrentPhotoPath);
                }

                ContentResolver contentResolver = getContentResolver();
                Uri uri = contentResolver.insert(PlayedContract.CONTENT_URI, values);

                if (playerList.size() > 0) {
                    ContentValues valuesPlayer;

                    for (PlayerEntry player : playerList) {

                        valuesPlayer = new ContentValues();
                        valuesPlayer.put(GamePlayerContract.Columns.GAME_PLAYER_GAME, ContentUris.parseId(uri));
                        valuesPlayer.put(GamePlayerContract.Columns.GAME_PLAYER_PLAYER, player.getId());
                        if (player.isWinner()) {
                            valuesPlayer.put(GamePlayerContract.Columns.GAME_PLAYER_WINNER, 1);
                        }
                        contentResolver.insert(GamePlayerContract.CONTENT_URI, valuesPlayer);
                    }
                }

                Intent done = new Intent(PlayedAddResult.this, PlayedList.class);
                startActivity(done);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String[] projection = {PlayerContract.Columns._ID,
                PlayerContract.Columns.PLAYER_NAME,
                PlayerContract.Columns.PLAYER_IMAGE,
        };

        switch (i) {
            case LOADER_ID:

                return new CursorLoader(PlayedAddResult.this, PlayerContract.CONTENT_URI, projection, null, null, PlayerContract.Columns.PLAYER_NAME);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called invalid loader id " + i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        int id;
        String name;
        String image;

        if (cursor != null) {
//            Log.d(TAG, "onCreate: nr of rows: " + cursor.getCount());

            playerList = new ArrayList<>();

            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(PlayerContract.Columns._ID));
                name = cursor.getString(cursor.getColumnIndex(PlayerContract.Columns.PLAYER_NAME));
                image = cursor.getString(cursor.getColumnIndex(PlayerContract.Columns.PLAYER_IMAGE));

                if (playerIds != null) {
                    for (Integer num : playerIds) {
                        if (id == num) {
                            playerList.add(new PlayerEntry(id, name, null, image));
                        }
                    }
                }

                if(winner != null) {
                    for (PlayerEntry player : playerList) {
                        for (Integer num : winner) {
                            if (player.getId() == num) {
                                player.setWinner(true);
                            }
                        }
                    }
                }
            }

            played.setGame(game);
            played.setPlayers(playerList);

            mAdapter = new SelectPlayerWinnerAdapter(this, R.layout.list_record_players_result, playerList);
            playerResult.setAdapter(mAdapter);

        } else {
            Log.d(TAG, "CURSOR NULL");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (playerIds != null) {
            LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this);
        }
    }

    public void pickPhoto(View v) {
        dispatchTakePictureIntent();
    }

    private boolean dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri image = FileProvider.getUriForFile(this,
                        "com.gmail.gpolomicz.boardgametracker.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                return true;
            }
        }
        return false;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File f = new File(mCurrentPhotoPath);
            selectedImage = Uri.fromFile(f);
            playedImage.setImageURI(selectedImage);
            playedImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        winner = new ArrayList<>();

        if (playerList != null) {
            for (PlayerEntry player : playerList) {
                if (player.isWinner()) {
                    winner.add(player.getId());
                }
            }
        }

        outState.putIntegerArrayList("winner", winner);
        outState.putParcelable("uri", selectedImage);
        outState.putString("path", mCurrentPhotoPath);
    }
}
