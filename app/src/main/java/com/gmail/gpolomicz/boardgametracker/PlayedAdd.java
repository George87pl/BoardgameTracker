package com.gmail.gpolomicz.boardgametracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class PlayedAdd extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GPDEB";
    public static final int LOADER_ID = 0;
    private ListView collection_list;
    List<BGEntry> bgList;
    BGCollectionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_played_add);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        setTitle("Select game to play");

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

        String[] projection = {BoardgameContract.Columns._ID,
                BoardgameContract.Columns.BOARDGAME_NAME,
                BoardgameContract.Columns.BOARDGAME_DESCRIPTION,
                BoardgameContract.Columns.BOARDGAME_RATING,
                BoardgameContract.Columns.BOARDGAME_IMAGE,
                BoardgameContract.Columns.BOARDGAME_PUBDATE,
        };

        switch (i) {
            case LOADER_ID:

                return new CursorLoader(PlayedAdd.this, BoardgameContract.CONTENT_URI, projection, null, null, BoardgameContract.Columns.BOARDGAME_NAME);

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

            bgList = new ArrayList<>();

            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(BoardgameContract.Columns._ID));
                name = cursor.getString(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_NAME));
                image = cursor.getString(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_IMAGE));

                bgList.add(new BGEntry(id, name, null, 0, image, null));

//                Log.d(TAG, "[" + id + ", " + name + ", " + description + ", " + image + ", " + pubDate + "]");
            }
        } else {
            Log.d(TAG, "CURSOR NULL");
        }

        collection_list = findViewById(R.id.select_game);
        mAdapter = new BGCollectionAdapter(PlayedAdd.this, R.layout.list_record_played_add, bgList);
        collection_list.setAdapter(mAdapter);

        collection_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayedAdd.this, SelectPlayers.class);
                intent.putExtra("game", bgList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

    }

}
