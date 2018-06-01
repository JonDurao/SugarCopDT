package com.dtapps.sugarcop;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.dtapps.sugarcop.Provider.CONTENT_AUTHORITY;
import static com.dtapps.sugarcop.Provider.CONTENT_AUTHORITY_URI;

/**
 * Created by jon.durao on 11/2/2017.
 */

public class SugarHistoryContract {
    public static abstract class SugarHistoryEntry implements BaseColumns {
        public static final String SUGAR_TABLE_NAME = "sugarHistory";

        public static class Database{
            public static final String SUGAR_DATABASE_NAME = "SugarHistory.db";
            public static final int SUGAR_DATABASE_VERSION = 1;

            private Database(){}
        }

        public static class Queries{
            public static final String  SUGAR_TABLE_CREATE = "CREATE TABLE %s (%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL);";
            public static final String  SUGAR_TABLE_DELETE = " DROP TABLE IF EXISTS %s;";

            private Queries() {}
        }

        public static class Columns{
            public static final String SUGAR_ID = "_id";
            public static final String SUGAR_DATE = "Date";
            public static final String SUGAR_SUGAR = "Sugar";

            private Columns() {}
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, SugarHistoryEntry.SUGAR_TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + SugarHistoryEntry.SUGAR_TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + SugarHistoryEntry.SUGAR_TABLE_NAME;

    static Uri buildSugarUri(long sugarId){
        return ContentUris.withAppendedId(CONTENT_URI, sugarId);
    }

    static long getSugarId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
