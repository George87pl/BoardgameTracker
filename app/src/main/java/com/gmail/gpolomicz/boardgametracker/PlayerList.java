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

public class PlayerList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GPDEB";
    public static final int LOADER_ID = 0;
    private ListView player_list;
    ArrayList<PlayerEntry> playerList;
    PlayerListAdapter mAdapter;
    int sort;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        setTitle("Players");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerList.this, PlayerAdd.class);
                startActivity(intent);
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(PlayerList.this, PlayerList.class);

        pref = getApplicationContext().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = pref.edit();


        switch (item.getItemId()) {
            case R.id.sort_name:
                editor.putInt("playersort", 0);
                editor.apply();
                startActivity(intent);
                break;

            case R.id.sort_scores:
                editor.putInt("playersort", 0);
                editor.apply();
                startActivity(intent);
                break;

            case R.id.sort_playing:
                editor.putInt("playersort", 0);
                editor.apply();
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
//        Log.d(TAG, "onCreateLoader: id: " + i);

        String[] projection = {PlayerContract.Columns._ID,
                PlayerContract.Columns.PLAYER_NAME,
                PlayerContract.Columns.PLAYER_NOTE,
                PlayerContract.Columns.PLAYER_IMAGE,
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
                        return new CursorLoader(PlayerList.this, PlayerContract.CONTENT_URI, projection, null, null, PlayerContract.Columns.PLAYER_NAME);
                }

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called invalid loader id " + i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        int id;
        String name;
        String note;
        String image;

        if (cursor != null) {
//            Log.d(TAG, "onCreate: nr of rows: " + cursor.getCount());

            playerList = new ArrayList<>();

            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(PlayerContract.Columns._ID));
                name = cursor.getString(cursor.getColumnIndex(PlayerContract.Columns.PLAYER_NAME));
                note = cursor.getString(cursor.getColumnIndex(PlayerContract.Columns.PLAYER_NOTE));
                image = cursor.getString(cursor.getColumnIndex(PlayerContract.Columns.PLAYER_IMAGE));

                playerList.add(new PlayerEntry(id, name, note, image));

//                Log.d(TAG, "[" + id + ", " + name + ", " + note + ", " + image + "]");
            }
        } else {
            Log.d(TAG, "CURSOR NULL");
        }

        player_list = findViewById(R.id.player_list);
        mAdapter = new PlayerListAdapter(PlayerList.this, R.layout.list_record_player, playerList);
        player_list.setAdapter(mAdapter);

        player_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayerList.this, PlayerDetail.class);
                intent.putExtra("player", playerList.get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

    }

}
