package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerDetail extends AppCompatActivity implements AppDialog.DialogEvents {
    private static final String TAG = "GPDEB";

    ImageView image_pl;
    TextView name_pl, note_pl;
    ListView detail_list_bg;
    PlayerEntry player;
    public static final int DELETE_DIALOG_ID = 1;
    List<PlayedEntry> playedList;
    BGEntry bgEntry;
    PlayedListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        image_pl = findViewById(R.id.played_image);
        name_pl = findViewById(R.id.name_pl);
        note_pl = findViewById(R.id.note_pl);
        detail_list_bg = findViewById(R.id.detail_list_bg);

        player = (PlayerEntry) getIntent().getSerializableExtra("player");

        if (player != null) {

            if (player.getImage() != null) {
                Picasso.get()
                        .load("file://" + player.getImage())
                        .fit()
                        .centerCrop()
                        .into(image_pl);
            } else {

                image_pl.getLayoutParams().height = 100;

                Picasso.get()
                        .load(R.drawable.bg)
                        .fit()
                        .centerCrop()
                        .into(image_pl);
            }

            name_pl.setTextColor(Color.WHITE);
            name_pl.setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK);
            name_pl.setText(player.getName());

            note_pl.setText(player.getNote());
        }

        ContentResolver contentResolver = getContentResolver();

        String[] projection = {GamePlayerContract.Columns.GAME_PLAYER_GAME};
        String selection = GamePlayerContract.Columns.GAME_PLAYER_PLAYER + " = " + player.getId();

        Cursor cursor = contentResolver.query(GamePlayerContract.CONTENT_URI, projection, selection, null, GamePlayerContract.Columns._ID + " DESC");

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                Log.d(TAG, "onCreate: " + cursor.getCount());

                playedList = new ArrayList<>();

                int idPlayed;

                while (cursor.moveToNext()) {
                    idPlayed = cursor.getInt(cursor.getColumnIndex(GamePlayerContract.Columns.GAME_PLAYER_GAME));

                    String[] projectionPlayed = {PlayedContract.Columns._ID,
                            PlayedContract.Columns.PLAYED_GAME,
                            PlayedContract.Columns.PLAYED_DATE,
                            PlayedContract.Columns.PLAYED_IMAGE,
                            PlayedContract.Columns.PLAYED_NOTE};

                    Cursor cursorPlayed = contentResolver.query(PlayedContract.buildPlayedUri(idPlayed), projectionPlayed, null, null, null);

                    if (cursorPlayed != null) {

                        String date, image, note;
                        int game;

                        while (cursorPlayed.moveToNext()) {
                            game = cursorPlayed.getInt(cursorPlayed.getColumnIndex(PlayedContract.Columns.PLAYED_GAME));
                            date = cursorPlayed.getString(cursorPlayed.getColumnIndex(PlayedContract.Columns.PLAYED_DATE));
                            image = cursorPlayed.getString(cursorPlayed.getColumnIndex(PlayedContract.Columns.PLAYED_IMAGE));
                            note = cursorPlayed.getString(cursorPlayed.getColumnIndex(PlayedContract.Columns.PLAYED_NOTE));

                            String[] projectionGame = {BoardgameContract.Columns._ID,
                                    BoardgameContract.Columns.BOARDGAME_NAME,
                                    BoardgameContract.Columns.BOARDGAME_RATING,
                                    BoardgameContract.Columns.BOARDGAME_DESCRIPTION,
                                    BoardgameContract.Columns.BOARDGAME_IMAGE};

                            Cursor cursorGame = contentResolver.query(BoardgameContract.buildTaskUri(game), projectionGame, null, null, null);

                            if (cursorGame != null) {

                                int idGame, ratingGame;
                                String nameGame, descGame, imageGame;

                                while (cursorGame.moveToNext()) {
                                    idGame = cursorGame.getInt(cursorGame.getColumnIndex(BoardgameContract.Columns._ID));
                                    nameGame = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_NAME));
                                    ratingGame = cursorGame.getInt(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_RATING));
                                    descGame = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_DESCRIPTION));
                                    imageGame = cursorGame.getString(cursorGame.getColumnIndex(BoardgameContract.Columns.BOARDGAME_IMAGE));

                                    bgEntry = new BGEntry(idGame, nameGame, descGame, ratingGame, imageGame, null);
                                }
                                cursorGame.close();

                                playedList.add(new PlayedEntry(idPlayed, bgEntry, null, note, date, image));
                            }
                            cursorPlayed.close();
                        }
                    }
                }
                mAdapter = new PlayedListAdapter(PlayerDetail.this, R.layout.list_record_played, playedList);
                detail_list_bg.setAdapter(mAdapter);
            }
            cursor.close();
        }

        detail_list_bg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayerDetail.this, PlayedDetail.class);
                intent.putExtra("played", playedList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPositiveDialogResoult(int dialogId, Bundle args) {


        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(PlayerContract.buildPlayerUri(player.getId()), null, null);

        if (player.getImage() != null) {
            File file = new File(player.getImage());
            boolean deleted = file.delete();
        }

        Intent intent = new Intent(PlayerDetail.this, PlayerList.class);
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
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_bg:
                Intent intent = new Intent(PlayerDetail.this, PlayerEdit.class);
                intent.putExtra("player", player);
                startActivity(intent);
                break;

            case R.id.delete_bg:

                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
                args.putString(AppDialog.DIALOG_MESSAGE, "Remove " + player.getName() + "?");
                args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

                dialog.setArguments(args);
                dialog.show(getFragmentManager(), null);

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
