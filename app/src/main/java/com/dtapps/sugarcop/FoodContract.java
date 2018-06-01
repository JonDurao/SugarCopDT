package com.dtapps.sugarcop;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.dtapps.sugarcop.Provider.CONTENT_AUTHORITY;
import static com.dtapps.sugarcop.Provider.CONTENT_AUTHORITY_URI;

/**
 * Created by jon.durao on 10/18/2017.
 */

public class FoodContract {
    public static abstract class FoodEntry implements BaseColumns {
        public static final String FOOD_TABLE_NAME = "food";

        public static class Database{
            public static final String FOOD_DATABASE_NAME = "FoodDDBB.db";
            public static final int FOOD_DATABASE_VERSION = 1;
            public static final int FOOD_DATABASE_MAX_NEW = 20577;
            public static final int FOOD_DATABASE_MAX = 20577;

            private Database(){}
        }

        public static class Queries{
            public static final String  FOOD_TABLE_CREATE = "CREATE TABLE %s (%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT," +
                    "%s TEXT," +
                    "%s TEXT NOT NULL," +
                    "%s REAL," +
                    "%s REAL," +
                    "%s REAL," +
                    "%s REAL," +
                    "%s REAL," +
                    "%s INTEGER NOT NULL," +
                    "%s INTEGER NOT NULL," +
                    "%s TEXT," +
                    "%s TEXT);";
            public static final String  FOOD_TABLE_DELETE = " DROP TABLE IF EXISTS %s;";

            private Queries() {}
        }

        public static class Columns{
            public static final String FOOD_ID = "_id";
            public static final String FOOD_NAME = "Name";
            public static final String FOOD_BRAND = "Brand";
            public static final String FOOD_CATEGORIES = "Categories";
            public static final String FOOD_BARCODE = "Barcode";
            public static final String FOOD_UNIT = "Unit";
            public static final String FOOD_SUGAR_HUNDRED = "SugarPerHundred";
            public static final String FOOD_SUGAR_PORTION = "SugarPerPortion";
            public static final String FOOD_QUANTITY_PORTION = "QuantityPortion";
            public static final String FOOD_SUGAR_ITEM = "SugarPerItem";
            public static final String FOOD_QUANTITY = "Quantity";
            public static final String FOOD_CONFIRMED = "Confirmed";
            public static final String FOOD_CONFIRMED_BY_USER = "ConfirmedByThisUser";
            public static final String FOOD_NAME_NORMALIZED = "NormalizedName";
            public static final String FOOD_BRAND_NORMALIZED = "NormalizedBrand";

            private Columns() {}
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, FoodEntry.FOOD_TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + FoodEntry.FOOD_TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + FoodEntry.FOOD_TABLE_NAME;

    static Uri buildFoodUri(long foodId){
        return ContentUris.withAppendedId(CONTENT_URI, foodId);
    }

    static long getFoodId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
