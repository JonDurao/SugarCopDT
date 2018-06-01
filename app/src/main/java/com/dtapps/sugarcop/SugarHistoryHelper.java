package com.dtapps.sugarcop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.dtapps.sugarcop.SugarHistoryContract.SugarHistoryEntry.SUGAR_TABLE_NAME;

/**
 * Created by jon.durao on 11/2/2017.
 */

public class SugarHistoryHelper extends SQLiteOpenHelper {
    public static SugarHistoryHelper instance = null;

    private SugarHistoryHelper(Context context){
        super(context, SugarHistoryContract.SugarHistoryEntry.Database.SUGAR_DATABASE_NAME, null, SugarHistoryContract.SugarHistoryEntry.Database.SUGAR_DATABASE_VERSION);
    }

    static SugarHistoryHelper getInstance (Context context){
        if (instance == null){
            instance = new SugarHistoryHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(SugarHistoryContract.SugarHistoryEntry.Queries.SUGAR_TABLE_CREATE,
                SUGAR_TABLE_NAME,
                SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_ID,
                SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_DATE,
                SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_SUGAR));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(SUGAR_TABLE_NAME, null, null);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
