package com.dtapps.sugarcop;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static com.dtapps.sugarcop.UserContract.UserEntry.*;

/**
 * Created by jon.durao on 10/6/2017.
 */

public class UserHelper extends SQLiteOpenHelper {
    public static UserHelper instance = null;

    private UserHelper(Context context){
        super(context, Database.USER_DATABASE_NAME, null, Database.USER_DATABASE_VERSION);
    }

    static UserHelper getInstance(Context context){
        if (instance == null){
            instance = new UserHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(Queries.USER_TABLE_CREATE, USER_TABLE_NAME, Columns.USER_USERNAME, Columns.USER_NAME,
                                    Columns.USER_SURNAME, Columns.USER_GENRE, Columns.USER_AGE, Columns.USER_HEIGHT,
                                    Columns.USER_WEIGHT ,Columns.USER_SUGAR));
    }

    private static List<ContentValues> insertMockData(){
        List<ContentValues> valuesList = new ArrayList<>();

        for (int i = 0; i<6; i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Columns.USER_USERNAME, "Username Prueba " + i);
            contentValues.put(Columns.USER_NAME, "Nombre Prueba " + i);
            contentValues.put(Columns.USER_SURNAME, "Apellido Prueba " + i);
            contentValues.put(Columns.USER_GENRE, "Male");
            contentValues.put(Columns.USER_AGE, i);
            contentValues.put(Columns.USER_HEIGHT, i + 100);
            contentValues.put(Columns.USER_WEIGHT, (101.2f+i));
            contentValues.put(Columns.USER_SUGAR, (29.5f+i));

            valuesList.add(contentValues);
        }

        return valuesList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(USER_TABLE_NAME, null, null);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
