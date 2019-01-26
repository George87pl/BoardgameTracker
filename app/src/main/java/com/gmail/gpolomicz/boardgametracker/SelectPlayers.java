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
import java.util.List;

public class SelectPlayers extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GPDEB";
    public static final int LOADER_ID = 0;
    private ListView selectPlayers;
    List<PlayerEntry> playerList;
    SelectPlayersAdapter mAdapter;
    ArrayList<Integer> playersIds;
    boolean[] checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_players);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Select Players");

        if (savedInstanceState != null) {
            checked = savedInstanceState.getBooleanArray("checked");
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_players, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(SelectPlayers.this, PlayedAddResult.class);
        intent.putExtra("game", getIntent().getSerializableExtra("game"));

        switch (item.getItemId()) {
            case R.id.skip:
                startActivity(intent);
                break;

            case R.id.save:
                
                playersIds = new ArrayList<>();

                for (PlayerEntry player : playerList) {
                    if (player.isChecked()) {
                        playersIds.add(player.getId());
                    }
                }
                
                if(playersIds.size() > 0) {
                    intent.putExtra("playerIds", playersIds);


                    startActivity(intent);
                } else {
                    Toast.makeText(this, "0 Players selected", Toast.LENGTH_SHORT).show();
                }

                
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
                PlayerContract.Columns.PLAYER_IMAGE,
        };

        switch (i) {
            case LOADER_ID:

                return new CursorLoader(SelectPlayers.this, PlayerContract.CONTENT_URI, projection, null, null, PlayerContract.Columns.PLAYER_NAME);

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

                playerList.add(new PlayerEntry(id, name, null, image));

//                Log.d(TAG, "[" + id + ", " + name + ", " + note + ", " + image + "]");
            }
        } else {
            Log.d(TAG, "CURSOR NULL");
        }

        if (checked != null) {
            for (int i = 0; i < playerList.size(); i++) {
                playerList.get(i).setChecked(checked[i]);
            }
        }

        selectPlayers = findViewById(R.id.select_players);
        mAdapter = new SelectPlayersAdapter(SelectPlayers.this, R.layout.list_record_select_players, playerList);
        selectPlayers.setAdapter(mAdapter);

        selectPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (playerList.get(position).isChecked()) {
                    playerList.get(position).setChecked(false);
                } else {
                    playerList.get(position).setChecked(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (playerList != null && playerList.size() > 0) {
            checked = new boolean[playerList.size()];

            for (int i = 0; i < playerList.size(); i++) {
                checked[i] = playerList.get(i).isChecked();
            }
            outState.putBooleanArray("checked", checked);
        }
    }
}
