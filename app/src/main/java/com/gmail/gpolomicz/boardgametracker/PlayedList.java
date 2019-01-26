package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class PlayedList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GPDEB";
    public static final int LOADER_ID = 0;
    private ListView played_list;
    List<PlayedEntry> playedList;
    PlayedListAdapter mAdapter;
    int sort;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_played_list);
        setTitle("Played");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayedList.this, PlayedAdd.class);
                startActivity(intent);
            }
        });

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
//        Log.d(TAG, "onCreateLoader: id: " + i);

        String[] projection = {PlayedContract.Columns._ID,
                PlayedContract.Columns.PLAYED_GAME,
                PlayedContract.Columns.PLAYED_DATE,
                PlayedContract.Columns.PLAYED_NOTE,
                PlayedContract.Columns.PLAYED_IMAGE
        };

        switch (i) {
            case LOADER_ID:

                SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
                if(pref != null) {
                    sort = pref.getInt("playersort", 0);
                } else {
                    sort = 0;
                }

                switch (sort) {
                    case 0:
                        return new CursorLoader(PlayedList.this, PlayedContract.CONTENT_URI, projection, null, null, null);
                }

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called invalid loader id " + i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        int id, idGame;
        long gameId;
        String note, image;
        String date;
        String bgName, bgDescription, bgImage, bgPubDate;
        int bgRating;
        BGEntry game = null;

        if (cursor != null) {
//            Log.d(TAG, "onCreate: nr of rows: " + cursor.getCount());

            playedList = new ArrayList<>();

            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(PlayedContract.Columns._ID));
                gameId = cursor.getInt(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_GAME));
                date = cursor.getString(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_DATE));
                note = cursor.getString(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_NOTE));
                image = cursor.getString(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_IMAGE));

                ContentResolver contentResolver = getContentResolver();

                String[] projection = {BoardgameContract.Columns._ID,
                        BoardgameContract.Columns.BOARDGAME_NAME,
                        BoardgameContract.Columns.BOARDGAME_DESCRIPTION,
                        BoardgameContract.Columns.BOARDGAME_RATING,
                        BoardgameContract.Columns.BOARDGAME_IMAGE,
                        BoardgameContract.Columns.BOARDGAME_PUBDATE,
                };

                Cursor cursorGame = contentResolver.query(BoardgameContract.buildTaskUri(gameId), projection, null, null, null);

                if(cursorGame != null) {
                    cursorGame.moveToFirst();

                    idGame = cursorGame.getInt(cursorGame.getColumnIndex(BoardgameContract.Columns._ID));
                    bgName = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_NAME));
                    bgDescription = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_DESCRIPTION));
                    bgRating = cursorGame.getInt(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_RATING));
                    bgImage = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_IMAGE));
                    bgPubDate = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_PUBDATE));

                    game = new BGEntry(idGame, bgName, bgDescription, bgRating, bgImage, bgPubDate);
                    cursorGame.close();
                }

                playedList.add(new PlayedEntry(id, game, null, note, date, image));
            }
        } else {
            Log.d(TAG, "CURSOR NULL");
        }

        played_list = findViewById(R.id.played_list);
        mAdapter = new PlayedListAdapter(PlayedList.this, R.layout.list_record_played, playedList);
        played_list.setAdapter(mAdapter);

        played_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayedList.this, PlayedDetail.class);
                intent.putExtra("played", playedList.get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

    }

}
