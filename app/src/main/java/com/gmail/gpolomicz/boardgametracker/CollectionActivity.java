package com.gmail.gpolomicz.boardgametracker;

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
import android.widget.Toast;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GPDEB";
    public static final int LOADER_ID = 0;
    private ListView collection_list;
    ArrayList<BGEntry> bgList;
    BGCollectionAdapter mAdapter;
    int sort;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Collection");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CollectionActivity.this, BGAdd.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_import, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(CollectionActivity.this, CollectionActivity.class);

        pref = getApplicationContext().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = pref.edit();


        switch (item.getItemId()) {
            case R.id.sort_name:
                editor.putInt("sort", 0);
                editor.apply();
//                intent.putExtra("sort", 0);
                startActivity(intent);
                break;

            case R.id.sort_rating:
                editor.putInt("sort", 1);
                editor.apply();
//                intent.putExtra("sort", 1);
                startActivity(intent);
                break;

            case R.id.sort_playing:
                editor.putInt("sort", 2);
                editor.apply();
//                intent.putExtra("sort", 2);
                startActivity(intent);
                break;

            case R.id.import_bgg:
                Intent intent2 = new Intent(CollectionActivity.this, BGImport.class);
                startActivity(intent2);
                break;
        }

        return super.onOptionsItemSelected(item);
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

                SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
                if (pref != null) {
                    sort = pref.getInt("sort", 0);
                } else {
                    sort = 0;
                }

                switch (sort) {
                    case 0:
                        return new CursorLoader(CollectionActivity.this, BoardgameContract.CONTENT_URI, projection, null, null, BoardgameContract.Columns.BOARDGAME_NAME);

                    case 1:
                        return new CursorLoader(CollectionActivity.this, BoardgameContract.CONTENT_URI, projection, null, null, BoardgameContract.Columns.BOARDGAME_RATING + " DESC");

                    case 2:
                        return new CursorLoader(CollectionActivity.this, BoardgameContract.CONTENT_URI, projection, null, null, BoardgameContract.Columns.BOARDGAME_NAME);

                }

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called invalid loader id " + i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        int id;
        String name;
        String description;
        int rating;
        String image;
        String pubDate;

        if (cursor != null) {
//            Log.d(TAG, "onCreate: nr of rows: " + cursor.getCount());

            bgList = new ArrayList<>();

            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(BoardgameContract.Columns._ID));
                name = cursor.getString(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_NAME));
                description = cursor.getString(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_DESCRIPTION));
                rating = cursor.getInt(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_RATING));
                image = cursor.getString(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_IMAGE));
                pubDate = cursor.getString(cursor.getColumnIndex(BoardgameContract.Columns.BOARDGAME_PUBDATE));

                bgList.add(new BGEntry(id, name, description, rating, image, pubDate));

//                Log.d(TAG, "[" + id + ", " + name + ", " + description + ", " + image + ", " + pubDate + "]");
            }
        } else {
            Log.d(TAG, "CURSOR NULL");
        }

        collection_list = findViewById(R.id.collection_list);
//        collection_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mAdapter = new BGCollectionAdapter(CollectionActivity.this, R.layout.list_record, bgList);
        collection_list.setAdapter(mAdapter);

        collection_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectionActivity.this, BGDetails.class);
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
