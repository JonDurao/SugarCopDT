package com.dtapps.sugarcop;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.dtapps.sugarcop.Provider.*;

/**
 * Created by jon.durao on 10/6/2017.
 */

public class UserContract {
    public static abstract class UserEntry implements BaseColumns{
        public static final String USER_TABLE_NAME = "users";

        public static class Database{
            public static final String USER_DATABASE_NAME = "Users.db";
            public static final int USER_DATABASE_VERSION = 1;

            private Database(){}
        }

        public static class Queries{
            public static final String  USER_TABLE_CREATE = "CREATE TABLE %s (%s TEXT PRIMARY KEY NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER," +
                    "%s INTEGER," +
                    "%s REAL," +
                    "%s REAL);";
            public static final String  USER_TABLE_DELETE = " DROP TABLE IF EXISTS %s;";

            private Queries() {}
        }

        public static class Columns{
            public static final String USER_AGE = "Age";
            public static final String USER_GENRE = "Genre";
            public static final String USER_HEIGHT = "Height";
            public static final String USER_NAME = "Name";
            public static final String USER_SUGAR = "SugarPercentage";
            public static final String USER_SURNAME = "Surname";
            public static final String USER_USERNAME = "Username";
            public static final String USER_WEIGHT = "Weight";

            private Columns() {}
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, UserEntry.USER_TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + UserEntry.USER_TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + UserEntry.USER_TABLE_NAME;

    static Uri buildUserUri(long userId){
        return ContentUris.withAppendedId(CONTENT_URI, userId);
    }

    static long getUserId(Uri uri){
        return ContentUris.parseId(uri);
    }
}