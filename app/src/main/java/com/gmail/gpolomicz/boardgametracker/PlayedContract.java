package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.gmail.gpolomicz.boardgametracker.AppProvider.CONTENT_AUTHORITY;
import static com.gmail.gpolomicz.boardgametracker.AppProvider.CONTENT_AUTHORITY_URI;

public class PlayedContract {

    static final String TABLE_NAME = "Played";

    // Task fields
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String PLAYED_GAME = "Game";
        public static final String PLAYED_DATE = "Date";
        public static final String PLAYED_NOTE = "Note";
        public static final String PLAYED_WINNER = "Winner";
        public static final String PLAYED_IMAGE = "Image";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." +CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildPlayedUri (long playedId) {
        return ContentUris.withAppendedId(CONTENT_URI, playedId);
    }

    public static long getPlayedId(Uri uri) {
        return ContentUris.parseId(uri);
    }

}
