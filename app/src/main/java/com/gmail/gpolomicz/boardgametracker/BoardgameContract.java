package com.gmail.gpolomicz.boardgametracker;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.gmail.gpolomicz.boardgametracker.AppProvider.CONTENT_AUTHORITY;
import static com.gmail.gpolomicz.boardgametracker.AppProvider.CONTENT_AUTHORITY_URI;

public class BoardgameContract {

    static final String TABLE_NAME = "Boardgames";

    // Task fields
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String BOARDGAME_ID = "BggId";
        public static final String BOARDGAME_NAME = "Name";
        public static final String BOARDGAME_DESCRIPTION = "Description";
        public static final String BOARDGAME_RATING = "Rating";
        public static final String BOARDGAME_IMAGE = "Image";
        public static final String BOARDGAME_PUBDATE = "Pubdate";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." +CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTaskUri (long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }

    public static long getTaskId(Uri uri) {
        return ContentUris.parseId(uri);
    }

}
