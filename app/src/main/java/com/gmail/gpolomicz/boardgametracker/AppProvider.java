package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *  Provider for TaskTimer app. This is the only that know about {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {
    private static final String TAG = "GPDEB";

    private AppDatabase mOpenHelper;
    public static final UriMatcher sUriMatcher = builUriMatcher();

    static final String CONTENT_AUTHORITY = "com.gmail.gpolomicz.boardgametracker.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final int BOARDGAMES = 100;
    public static final int BOARDGAMES_ID = 101;

    public static final int PLAYERS = 200;
    public static final int PLAYERS_ID = 201;

    public static final int PLAYED = 300;
    public static final int PLAYED_ID = 301;

    public static final int GAME_PLAYER = 400;
    public static final int GAME_PLAYER_ID = 401;

    private static UriMatcher builUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // eg. content://com.gmail.gpolomicz.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, BoardgameContract.TABLE_NAME, BOARDGAMES);

        // eg. content://com.gmail.gpolomicz.tasktimer.provider/Tasks/8
        matcher.addURI(CONTENT_AUTHORITY, BoardgameContract.TABLE_NAME + "/#", BOARDGAMES_ID);

        matcher.addURI(CONTENT_AUTHORITY, PlayerContract.TABLE_NAME, PLAYERS);
        matcher.addURI(CONTENT_AUTHORITY, PlayerContract.TABLE_NAME + "/#", PLAYERS_ID);

        matcher.addURI(CONTENT_AUTHORITY, PlayedContract.TABLE_NAME, PLAYED);
        matcher.addURI(CONTENT_AUTHORITY, PlayedContract.TABLE_NAME + "/#", PLAYED_ID);

        matcher.addURI(CONTENT_AUTHORITY, GamePlayerContract.TABLE_NAME, GAME_PLAYER);
        matcher.addURI(CONTENT_AUTHORITY, GamePlayerContract.TABLE_NAME + "/#", GAME_PLAYER_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//        Log.d(TAG, "query: called with URI " +uri);
        final int match = sUriMatcher.match(uri);
//        Log.d(TAG, "query: match is " +match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case BOARDGAMES:
                queryBuilder.setTables(BoardgameContract.TABLE_NAME);
                break;

            case BOARDGAMES_ID:
                queryBuilder.setTables(BoardgameContract.TABLE_NAME);
                long taskId = BoardgameContract.getTaskId(uri);
                queryBuilder.appendWhere(BoardgameContract.Columns._ID + " = " + taskId);
                break;

            case PLAYERS:
                queryBuilder.setTables(PlayerContract.TABLE_NAME);
                break;

            case PLAYERS_ID:
                queryBuilder.setTables(PlayerContract.TABLE_NAME);
                long playerId = PlayerContract.getPlayerId(uri);
                queryBuilder.appendWhere(PlayerContract.Columns._ID + " = " + playerId);
                break;

            case PLAYED:
                queryBuilder.setTables(PlayedContract.TABLE_NAME);
                break;

            case PLAYED_ID:
                queryBuilder.setTables(PlayedContract.TABLE_NAME);
                long playedId = PlayedContract.getPlayedId(uri);
                queryBuilder.appendWhere(PlayedContract.Columns._ID + " = " + playedId);
                break;

            case GAME_PLAYER:
                queryBuilder.setTables(GamePlayerContract.TABLE_NAME);
                break;

            case GAME_PLAYER_ID:
                queryBuilder.setTables(GamePlayerContract.TABLE_NAME);
                long gamePlayerId = GamePlayerContract.getGamePlayerId(uri);
                queryBuilder.appendWhere(GamePlayerContract.Columns._ID + " = " + gamePlayerId);
                break;

            default:
                throw new IllegalArgumentException("Unknow URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
//        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount());

//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOARDGAMES:
                return BoardgameContract.CONTENT_TYPE;

            case BOARDGAMES_ID:
                return BoardgameContract.CONTENT_ITEM_TYPE;

            case PLAYERS:
                return PlayerContract.CONTENT_TYPE;

            case PLAYERS_ID:
                return PlayerContract.CONTENT_ITEM_TYPE;

            case PLAYED:
                return PlayedContract.CONTENT_TYPE;

            case PLAYED_ID:
                return PlayedContract.CONTENT_ITEM_TYPE;

            case GAME_PLAYER:
                return PlayedContract.CONTENT_TYPE;

            case GAME_PLAYER_ID:
                return PlayedContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("unknow Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
//        Log.d(TAG, "Entering insert, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
//        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;

        switch (match) {
            case BOARDGAMES:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(BoardgameContract.TABLE_NAME, null, values);
                if(recordId >= 0) {
                    returnUri = BoardgameContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " +uri.toString());
                }
                break;

            case PLAYERS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(PlayerContract.TABLE_NAME, null, values);
                if(recordId >= 0) {
                    returnUri = PlayerContract.buildPlayerUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " +uri.toString());
                }
                break;

            case PLAYED:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(PlayedContract.TABLE_NAME, null, values);
                if(recordId >= 0) {
                    returnUri = PlayedContract.buildPlayedUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " +uri.toString());
                }
                break;

            case GAME_PLAYER:
                db = mOpenHelper.getWritableDatabase();

                recordId = db.insert(GamePlayerContract.TABLE_NAME, null, values);
                if(recordId >= 0) {
                    returnUri = GamePlayerContract.buildGamePlayerUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " +uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknow uri: " + uri);
        }

        if (recordId >= 0) {
            // something was inserted
//            Log.d(TAG, "insert: Settings notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }

//        Log.d(TAG, "Existing insert, returning  " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match) {
            case BOARDGAMES:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(BoardgameContract.TABLE_NAME, selection, selectionArgs);
                break;

            case BOARDGAMES_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = BoardgameContract.getTaskId(uri);
                selectionCriteria = BoardgameContract.Columns._ID + " = " + taskId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.delete(BoardgameContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case PLAYERS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(PlayerContract.TABLE_NAME, selection, selectionArgs);
                break;

            case PLAYERS_ID:
                db = mOpenHelper.getWritableDatabase();
                long playerId = PlayerContract.getPlayerId(uri);
                selectionCriteria = PlayerContract.Columns._ID + " = " + playerId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.delete(PlayerContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case PLAYED:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(PlayedContract.TABLE_NAME, selection, selectionArgs);
                break;

            case PLAYED_ID:
                db = mOpenHelper.getWritableDatabase();
                long playedId = PlayedContract.getPlayedId(uri);
                selectionCriteria = PlayedContract.Columns._ID + " = " + playedId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.delete(PlayedContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case GAME_PLAYER:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(GamePlayerContract.TABLE_NAME, selection, selectionArgs);
                break;

            case GAME_PLAYER_ID:
                db = mOpenHelper.getWritableDatabase();
                long gamePlayerId = GamePlayerContract.getGamePlayerId(uri);
                selectionCriteria = GamePlayerContract.Columns._ID + " = " + gamePlayerId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.delete(GamePlayerContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknow uri: " + uri);
        }

        if (count > 0) {
            // something was deleted
            Log.d(TAG, "delete: Settings notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: nothing deleted");
        }

        Log.d(TAG, "Existing delete, returning " + count);
        return count;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match) {
            case BOARDGAMES:
                db = mOpenHelper.getWritableDatabase();
                Log.d(TAG, "update: NAME: " + BoardgameContract.TABLE_NAME + " VALUES: " +values+ " SELECTION: " +selection+ " ARG: " +selectionArgs );
                count = db.update(BoardgameContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case BOARDGAMES_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = BoardgameContract.getTaskId(uri);
                selectionCriteria = BoardgameContract.Columns._ID + " = " + taskId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(BoardgameContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case PLAYERS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(PlayerContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case PLAYERS_ID:
                db = mOpenHelper.getWritableDatabase();
                long timingsId = PlayerContract.getPlayerId(uri);
                selectionCriteria = PlayerContract.Columns._ID + " = " + timingsId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(PlayerContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case PLAYED:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(PlayedContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case PLAYED_ID:
                db = mOpenHelper.getWritableDatabase();
                long playedId = PlayedContract.getPlayedId(uri);
                selectionCriteria = PlayedContract.Columns._ID + " = " + playedId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(PlayedContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case GAME_PLAYER:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(GamePlayerContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case GAME_PLAYER_ID:
                db = mOpenHelper.getWritableDatabase();
                long gamePlayerId = GamePlayerContract.getGamePlayerId(uri);
                selectionCriteria = GamePlayerContract.Columns._ID + " = " + gamePlayerId;

                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(GamePlayerContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknow uri: " + uri);
        }

        if (count > 0) {
            // something was updated
            Log.d(TAG, "delete: Settings notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing updated");
        }

        Log.d(TAG, "Existing update, returning " + count);
        return count;
    }
}

