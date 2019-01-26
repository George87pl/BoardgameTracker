package com.gmail.gpolomicz.boardgametracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GPDEB";

    public static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button collection = findViewById(R.id.button_collection);
        Button players = findViewById(R.id.button_players);
        Button played = findViewById(R.id.button_played);
        Button stats = findViewById(R.id.button_stats);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                switch (v.getId()) {
                    case R.id.button_collection:
                        intent = new Intent(MainActivity.this, CollectionActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.button_players:
                        intent = new Intent(MainActivity.this, PlayerList.class);
                        startActivity(intent);
                        break;
                    case R.id.button_played:
                        intent = new Intent(MainActivity.this, PlayedList.class);
                        startActivity(intent);
                        break;
                    case R.id.button_stats:

                        break;
                }
            }
        };
        collection.setOnClickListener(listener);
        players.setOnClickListener(listener);
        played.setOnClickListener(listener);
        stats.setOnClickListener(listener);
    }
}
