package com.dtapps.sugarcop;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import static com.dtapps.sugarcop.UserContract.*;
import static com.dtapps.sugarcop.FoodContract.*;
import static com.dtapps.sugarcop.SugarHistoryContract.*;

/**
 * Created by jon.durao on 10/6/2017.
 */

public class Provider extends ContentProvider {
    private UserHelper userHelper;
    private FoodHelper foodHelper;
    private SugarHistoryHelper sugarHistoryHelper;
    public static final UriMatcher uriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.dtapps.sugarcop.provider";
    public final static Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final int USER = 100;
    public static final int USER_USERNAME = 101;
    public static final int FOOD = 200;
    public static final int FOOD_ID = 201;
    public static final int SUGAR = 300;
    public static final int SUGAR_ID = 301;


    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CONTENT_AUTHORITY, UserEntry.USER_TABLE_NAME, USER);
        uriMatcher.addURI(CONTENT_AUTHORITY, UserEntry.USER_TABLE_NAME + "/#", USER_USERNAME);
        uriMatcher.addURI(CONTENT_AUTHORITY, FoodEntry.FOOD_TABLE_NAME, FOOD);
        uriMatcher.addURI(CONTENT_AUTHORITY, FoodEntry.FOOD_TABLE_NAME + "/#", FOOD_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, SugarHistoryEntry.SUGAR_TABLE_NAME, SUGAR);
        uriMatcher.addURI(CONTENT_AUTHORITY, SugarHistoryEntry.SUGAR_TABLE_NAME + "/#", SUGAR_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        try {
            userHelper = UserHelper.getInstance(getContext());
            foodHelper = FoodHelper.getInstance(getContext());
            sugarHistoryHelper = SugarHistoryHelper.getInstance(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int matcher = uriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase database;
        Cursor cursor;

        switch (matcher){
            case USER:
                queryBuilder.setTables(UserEntry.USER_TABLE_NAME);
                database = userHelper.getReadableDatabase();
                break;
            case USER_USERNAME:
                queryBuilder.setTables(UserEntry.USER_TABLE_NAME);
                long userId = getUserId(uri);
                queryBuilder.appendWhere(UserEntry.Columns.USER_USERNAME + "=" + userId);
                database = userHelper.getReadableDatabase();
                break;
            case FOOD:
                queryBuilder.setTables(FoodEntry.FOOD_TABLE_NAME);
                database = foodHelper.getReadableDatabase();
                break;
            case FOOD_ID:
                queryBuilder.setTables(FoodEntry.FOOD_TABLE_NAME);
                long foodId = getFoodId(uri);
                queryBuilder.appendWhere(FoodEntry.Columns.FOOD_ID + "=" + foodId);
                database = foodHelper.getReadableDatabase();
                break;
            case SUGAR:
                queryBuilder.setTables(SugarHistoryEntry.SUGAR_TABLE_NAME);
                database = sugarHistoryHelper.getReadableDatabase();
                break;
            case SUGAR_ID:
                queryBuilder.setTables(SugarHistoryEntry.SUGAR_TABLE_NAME);
                long sugarId = getSugarId(uri);
                queryBuilder.appendWhere(SugarHistoryEntry.Columns.SUGAR_ID + "=" + sugarId);
                database = sugarHistoryHelper.getReadableDatabase();
                break;
            default:
                database = null;
        }

        if (database != null){
            cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } else {
            throw new IllegalArgumentException("No BBDD present: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case USER:
                return UserContract.CONTENT_TYPE;
            case USER_USERNAME:
                return UserContract.CONTENT_ITEM_TYPE;
            case FOOD:
                return FoodContract.CONTENT_TYPE;
            case FOOD_ID:
                return FoodContract.CONTENT_ITEM_TYPE;
            case SUGAR:
                return SugarHistoryContract.CONTENT_TYPE;
            case SUGAR_ID:
                return SugarHistoryContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase database;
        Uri returnUri;
        long recordId;

        switch (match){
            case USER:
                database = userHelper.getWritableDatabase();
                recordId = database.insert(UserEntry.USER_TABLE_NAME, null, values);
                if (recordId >= 0){
                    returnUri = buildUserUri(recordId);
                } else {
                    throw new SQLException("Failed to insert " + uri.toString());
                }
                break;
            case FOOD:
                database = foodHelper.getWritableDatabase();
                recordId = database.insert(FoodEntry.FOOD_TABLE_NAME, null, values);
                if (recordId >= 0){
                    returnUri = buildFoodUri(recordId);
                } else {
                    throw new SQLException("Failed to insert " + uri.toString());
                }
                break;
            case SUGAR:
                database = sugarHistoryHelper.getWritableDatabase();
                recordId = database.insert(SugarHistoryEntry.SUGAR_TABLE_NAME, null, values);
                if (recordId >= 0){
                    returnUri = buildSugarUri(recordId);
                } else {
                    throw new SQLException("Failed to insert " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }

        if (recordId >= 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase database;
        int count;

        String selectionCriteria;

        switch (match) {
            case USER:
                database = userHelper.getWritableDatabase();
                count = database.delete(UserEntry.USER_TABLE_NAME, selection, selectionArgs);
                break;
            case USER_USERNAME:
                database = userHelper.getWritableDatabase();
                long taskId = getUserId(uri);
                selectionCriteria = UserEntry.Columns.USER_USERNAME + "=" + taskId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += "AND (" + selection + ")";
                }
                count = database.delete(UserEntry.USER_TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case FOOD:
                database = foodHelper.getWritableDatabase();
                count = database.delete(FoodEntry.FOOD_TABLE_NAME, selection, selectionArgs);
                break;
            case FOOD_ID:
                database = foodHelper.getWritableDatabase();
                long taskIdFood = getFoodId(uri);
                selectionCriteria = FoodEntry.Columns.FOOD_ID + "=" + taskIdFood;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += "AND (" + selection + ")";
                }
                count = database.delete(FoodEntry.FOOD_TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case SUGAR:
                database = foodHelper.getWritableDatabase();
                count = database.delete(FoodEntry.FOOD_TABLE_NAME, selection, selectionArgs);
                break;
            case SUGAR_ID:
                database = sugarHistoryHelper.getWritableDatabase();
                long taskIdSugar = getSugarId(uri);
                selectionCriteria = SugarHistoryEntry.Columns.SUGAR_ID + "=" + taskIdSugar;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += "AND (" + selection + ")";
                }
                count = database.delete(SugarHistoryEntry.SUGAR_TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }

        if (count >= 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase database;
        int count;

        String selectionCriteria;

        switch (match) {
            case USER:
                database = userHelper.getWritableDatabase();
                count = database.update(UserEntry.USER_TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_USERNAME:
                database = userHelper.getWritableDatabase();
                long taskId = getUserId(uri);
                selectionCriteria = UserEntry.Columns.USER_USERNAME + "=" + taskId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += "AND (" + selection + ")";
                }
                count = database.update(UserEntry.USER_TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            case FOOD:
                database = foodHelper.getWritableDatabase();
                count = database.update(FoodEntry.FOOD_TABLE_NAME, values, selection, selectionArgs);
                break;
            case FOOD_ID:
                database = foodHelper.getWritableDatabase();
                long taskIdFood = getFoodId(uri);
                selectionCriteria = FoodEntry.Columns.FOOD_ID + "=" + taskIdFood;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += "AND (" + selection + ")";
                }
                count = database.update(FoodEntry.FOOD_TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            case SUGAR:
                database = sugarHistoryHelper.getWritableDatabase();
                count = database.update(SugarHistoryEntry.SUGAR_TABLE_NAME, values, selection, selectionArgs);
                break;
            case SUGAR_ID:
                database = foodHelper.getWritableDatabase();
                long taskIdSugar = getSugarId(uri);
                selectionCriteria = SugarHistoryEntry.Columns.SUGAR_ID + "=" + taskIdSugar;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += "AND (" + selection + ")";
                }
                count = database.update(SugarHistoryEntry.SUGAR_TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }

        if (count >= 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}
