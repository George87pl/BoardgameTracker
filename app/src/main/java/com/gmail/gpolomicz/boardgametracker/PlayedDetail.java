package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayedDetail extends AppCompatActivity implements AppDialog.DialogEvents {
    private static final String TAG = "GPDEB";

    ImageView playedImage, fullImage, fullBG;
    TextView name_bg, description, datePlay, winner;
    ListView detail_list_pl;
    PlayedEntry played;
    public static final int DELETE_DIALOG_ID = 1;
    List<PlayerEntry> playerList;
    boolean fullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_played_detail);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        playedImage = findViewById(R.id.played_image);
        fullImage = findViewById(R.id.full_screen_image);
        fullBG = findViewById(R.id.full_screen_bg);
        name_bg = findViewById(R.id.name_bg);
        description = findViewById(R.id.note_pl);
        detail_list_pl = findViewById(R.id.detail_list_pl);
        datePlay = findViewById(R.id.date_play);

        played = (PlayedEntry) getIntent().getSerializableExtra("played");
        playerList = new ArrayList<>();


        if (played != null) {

            if (played.getImage() != null) {

                Picasso.get()
                        .load("file://" + played.getImage())
                        .fit()
                        .centerCrop()
                        .into(playedImage);
            } else if (played.getGame().getImage() != null) {

                Picasso.get()
                        .load("file://" + played.getGame().getImage())
                        .fit()
                        .centerCrop()
                        .into(playedImage);

            } else {
                playedImage.getLayoutParams().height = 150;
                Picasso.get()
                        .load(R.drawable.bg)
                        .fit()
                        .centerCrop()
                        .into(playedImage);
            }

            name_bg.setTextColor(Color.WHITE);
            name_bg.setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK);
            name_bg.setText(played.getGame().getName());

            if (played.getNote() != null) {
                if (!played.getNote().equals("")) {
                    description.setText(played.getNote());
                } else {
                    description.setVisibility(View.GONE);
                }
            }

            datePlay.setText(played.getDate());

            ContentResolver contentResolver = getContentResolver();

            String[] projection = {GamePlayerContract.Columns._ID,
                    GamePlayerContract.Columns.GAME_PLAYER_PLAYER,
                    GamePlayerContract.Columns.GAME_PLAYER_WINNER
            };

            String selection = GamePlayerContract.Columns.GAME_PLAYER_GAME + " = " + played.getId();

            Cursor cursor = contentResolver.query(GamePlayerContract.CONTENT_URI, projection, selection, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    String[] projectionPlayer = {PlayerContract.Columns._ID,
                            PlayerContract.Columns.PLAYER_NAME,
                            PlayerContract.Columns.PLAYER_IMAGE
                    };

                    int idGamePlayer = cursor.getInt(cursor.getColumnIndex(GamePlayerContract.Columns.GAME_PLAYER_PLAYER));
                    Cursor cursorPlayer = contentResolver.query(PlayerContract.buildPlayerUri(idGamePlayer), projectionPlayer, null, null, null);

                    int idPlayer, winnerPlayer;
                    String namePlayer, imagePlayer;

                    if (cursorPlayer != null) {

                        while (cursorPlayer.moveToNext()) {

                            idPlayer = cursorPlayer.getInt(cursorPlayer.getColumnIndex(PlayerContract.Columns._ID));
                            namePlayer = cursorPlayer.getString(cursorPlayer.getColumnIndex(PlayerContract.Columns.PLAYER_NAME));
                            imagePlayer = cursorPlayer.getString(cursorPlayer.getColumnIndex(PlayerContract.Columns.PLAYER_IMAGE));
                            winnerPlayer = cursor.getInt(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_WINNER));

                            playerList.add(new PlayerEntry(idPlayer, namePlayer, null, imagePlayer));

                            if (winnerPlayer == 1) {
                                playerList.get(playerList.size()-1).setWinner(true);
                            }
                        }

                        cursorPlayer.close();
                    }
                }

                cursor.close();
            }
        }

        if (playerList.size() > 0) {
            PlayerListAdapter mAdapter = new PlayerListAdapter(this, R.layout.list_record_players_result, playerList);
            detail_list_pl.setAdapter(mAdapter);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("fullScreen")) {
                fullScreen();
            }
        }

        playedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullScreen();
            }
        });

        fullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullImage.setVisibility(View.GONE);
                fullBG.setVisibility(View.GONE);
                fullScreen = false;
            }
        });
    }

    @Override
    public void onPositiveDialogResoult(int dialogId, Bundle args) {


        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(PlayedContract.buildPlayedUri(played.getId()), null, null);

        String selection = GamePlayerContract.Columns.GAME_PLAYER_GAME + " = " + played.getId();
        contentResolver.delete(GamePlayerContract.CONTENT_URI, selection, null);

        if (played.getImage() != null) {
            File file = new File(played.getImage());
            boolean deleted = file.delete();
        }

        Intent intent = new Intent(PlayedDetail.this, PlayedList.class);
        startActivity(intent);
    }

    @Override
    public void onNegativeDialogResoult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_played:

                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
                args.putString(AppDialog.DIALOG_MESSAGE, "Remove?");
                args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

                dialog.setArguments(args);
                dialog.show(getFragmentManager(), null);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void fullScreen () {
        fullScreen = true;

        if (played.getImage() != null) {
            Picasso.get()
                    .load("file://" + played.getImage())
                    .into(fullImage);

            fullBG.setVisibility(View.VISIBLE);
            fullImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("fullScreen", fullScreen);
    }
}
