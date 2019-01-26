package com.gmail.gpolomicz.boardgametracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "GPDEB";

    private static final String DATABASE_NAME = "BoardgameTracker.db";
    private static final int DATABASE_VERSION = 1;
    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; //Use a string variable to facilitate logging
        sSQL = "CREATE TABLE " + BoardgameContract.TABLE_NAME + " ("
                + BoardgameContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + BoardgameContract.Columns.BOARDGAME_ID + " INTEGER, "
                + BoardgameContract.Columns.BOARDGAME_NAME + " TEXT NOT NULL, "
                + BoardgameContract.Columns.BOARDGAME_DESCRIPTION + " TEXT, "
                + BoardgameContract.Columns.BOARDGAME_RATING + " INTEGER, "
                + BoardgameContract.Columns.BOARDGAME_IMAGE + " TEXT, "
                + BoardgameContract.Columns.BOARDGAME_PUBDATE + " INTEGER);";

        Log.d(TAG, "sSQL: " + sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TABLE " + PlayerContract.TABLE_NAME + " ("
                + PlayerContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + PlayerContract.Columns.PLAYER_NAME + " TEXT NOT NULL, "
                + PlayerContract.Columns.PLAYER_NOTE + " TEXT, "
                + PlayerContract.Columns.PLAYER_IMAGE + " TEXT); ";

        Log.d(TAG, "sSQL: " + sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TABLE " + PlayedContract.TABLE_NAME + " ("
                + PlayedContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + PlayedContract.Columns.PLAYED_GAME + " INTEGER NOT NULL, "
                + PlayedContract.Columns.PLAYED_NOTE + " TEXT, "
                + PlayedContract.Columns.PLAYED_DATE + " TEXT, "
                + PlayedContract.Columns.PLAYED_IMAGE + " TEXT);";

        Log.d(TAG, "sSQL: " + sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TABLE " + GamePlayerContract.TABLE_NAME + " ("
                + GamePlayerContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + GamePlayerContract.Columns.GAME_PLAYER_GAME + " INTEGER NOT NULL, "
                + GamePlayerContract.Columns.GAME_PLAYER_PLAYER + " INTEGER NOT NULL, "
                + GamePlayerContract.Columns.GAME_PLAYER_WINNER + " INTEGER);";

        Log.d(TAG, "sSQL: " + sSQL);
        db.execSQL(sSQL);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");

        switch (oldVersion) {
            case 1:
                // upgrade logic from version 1
//                addTimingsTable(db);
                //przechodzi przez wszystkie wcześniejsze wersje

            case 2:
                // upgrade logic from version 1
//                addDurationView(db);
                break;

            default:
                throw new IllegalStateException("onUpgrade() with unknow newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }

//    private void addTimingsTable(SQLiteDatabase db) {
//
//        String sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
//                + TimingsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
//                + TimingsContract.Columns.TIMINGS_TASK_ID + " INTEGER NOT NULL, "
//                + TimingsContract.Columns.TIMINGS_START_TIME + " INTEGER, "
//                + TimingsContract.Columns.TIMINGS_DURATION + " INTEGER);";
//
//        Log.d(TAG, "sSQL: " + sSQL);
//        db.execSQL(sSQL);
//
//        sSQL = "CREATE TRIGGER Remove_Task"
//                + " AFTER DELETE ON " + TasksContract.TABLE_NAME
//                + " FOR EACH ROW"
//                + " BEGIN"
//                + " DELETE FROM " +TimingsContract.TABLE_NAME
//                + " WHERE " + TimingsContract.Columns.TIMINGS_TASK_ID + " = OLD." + TasksContract.Columns._ID + ";"
//                + " END;";
//
//        Log.d(TAG, "sSQL: " + sSQL);
//        db.execSQL(sSQL);
//    }

//    private void addDurationView(SQLiteDatabase db) {
//
//        String sSQL = "CREATE VIEW " + DurationsContract.TABLE_NAME
//                + " AS SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns._ID + ", "
//                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_NAME + ", "
//                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_DESCRIPTION + ", "
//                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", "
//                + " DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", 'unixepoch')"
//                + " AS " + DurationsContract.Columns.DURATIONS_START_DATE + ", "
//                + " SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_DURATION + ")"
//                + " AS " + DurationsContract.Columns.DURATIONS_DURATION
//                + " FROM " + TasksContract.TABLE_NAME + " JOIN " + TimingsContract.TABLE_NAME
//                + " ON " + TasksContract.TABLE_NAME + "." + TasksContract.Columns._ID + " = "
//                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_TASK_ID
//                + " GROUP BY " + DurationsContract.Columns.DURATIONS_START_DATE + ", " + DurationsContract.Columns.DURATIONS_NAME
//                + ";";
//
//        Log.d(TAG, "sSQL: " + sSQL);
//        db.execSQL(sSQL);
//    }
}
