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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BGDetails extends AppCompatActivity implements AppDialog.DialogEvents {
    private static final String TAG = "GPDEB";

    ImageView image_bg;
    TextView name_bg, rating_bg, description_bg;
    ListView detail_list_bg;
    BGEntry game;
    public static final int DELETE_DIALOG_ID = 1;
    List<PlayedEntry> playedList;
    PlayedListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgdetails);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        image_bg = findViewById(R.id.played_image);
        name_bg = findViewById(R.id.name_bg);
        rating_bg = findViewById(R.id.rating_bg);
        description_bg = findViewById(R.id.note_pl);
        detail_list_bg = findViewById(R.id.detail_list_bg);

        game = (BGEntry) getIntent().getSerializableExtra("game");

        if (game != null) {

            if (game.getImage() != null) {
                Picasso.get()
                        .load("file://" + game.getImage())
                        .fit()
                        .centerCrop()
                        .into(image_bg);
            } else {
//                image_bg.setImageResource(R.drawable.no_image);

                Picasso.get()
                        .load(R.drawable.bg)
                        .fit()
                        .centerCrop()
                        .into(image_bg);
            }

            name_bg.setTextColor(Color.WHITE);
            name_bg.setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK);
            name_bg.setText(game.getName());

            if (game.getRating() != -1.0) {
                rating_bg.setText(String.valueOf(game.getRating()));
            } else {
                rating_bg.setVisibility(View.INVISIBLE);
            }
            description_bg.setText(game.getDescription());
        }

        ContentResolver contentResolver = getContentResolver();

        String[] projection = {PlayedContract.Columns._ID,
                PlayedContract.Columns.PLAYED_DATE,
                PlayedContract.Columns.PLAYED_IMAGE,
                PlayedContract.Columns.PLAYED_NOTE};

        String selection = PlayedContract.Columns.PLAYED_GAME + " = " + game.getId();

        Cursor cursor = contentResolver.query(PlayedContract.CONTENT_URI, projection, selection, null, PlayedContract.Columns._ID + " DESC");

        if (cursor != null) {

            playedList = new ArrayList<>();

            int idPlayed;
            String date, image, note;

            while(cursor.moveToNext()) {
                idPlayed = cursor.getInt(cursor.getColumnIndex(PlayedContract.Columns._ID));
                date = cursor.getString(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_DATE));
                image = cursor.getString(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_IMAGE));
                note = cursor.getString(cursor.getColumnIndex(PlayedContract.Columns.PLAYED_NOTE));

                playedList.add(new PlayedEntry(idPlayed, game, null, note, date, image));
            }

            cursor.close();
        }

        mAdapter = new PlayedListAdapter(BGDetails.this, R.layout.list_record_played, playedList);
        detail_list_bg.setAdapter(mAdapter);

        detail_list_bg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BGDetails.this, PlayedDetail.class);
                intent.putExtra("played", playedList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPositiveDialogResoult(int dialogId, Bundle args) {


        ContentResolver contentResolver = getContentResolver();

        String[] projection = {PlayedContract.Columns._ID};
        String selection = PlayedContract.Columns.PLAYED_GAME + " = " + game.getId();

        Cursor cursor = contentResolver.query(PlayedContract.CONTENT_URI, projection, selection, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Toast.makeText(this, "Cant't delete. There is played saves in that game.", Toast.LENGTH_LONG).show();
            } else {

                contentResolver.delete(BoardgameContract.buildTaskUri(game.getId()), null, null);

                if (game.getImage() != null) {
                    File file = new File(game.getImage());
                    boolean deleted = file.delete();
                }

                Intent intent = new Intent(BGDetails.this, CollectionActivity.class);
                startActivity(intent);
            }
            cursor.close();
        }
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
                Intent intent = new Intent(BGDetails.this, BGEdit.class);
                intent.putExtra("game", game);
                startActivity(intent);
                break;

            case R.id.delete_bg:

                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
                args.putString(AppDialog.DIALOG_MESSAGE, "Remove " + game.getName() + "?");
                args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

                dialog.setArguments(args);
                dialog.show(getFragmentManager(), null);

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
