package com.dtapps.sugarcop;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.dtapps.sugarcop.FoodContract.FoodEntry.*;
import static com.dtapps.sugarcop.common.Common.commonCreateFoodMValues;
import static com.dtapps.sugarcop.common.Common.commonCreateFoodObjectCursor;

/**
 * Created by jon.durao on 10/18/2017.
 */

public class FoodHelper extends SQLiteOpenHelper{
    public Context mContext;
    public static FoodHelper mFoodInstance = null;
    private static String FOOD_DATABASE_PATH;
    private SQLiteDatabase checkDB = null;
    private Cursor cursor = null;
    private List<Food> foodUser = new ArrayList<>();

    private FoodHelper(Context context) throws IOException{
        super(context, Database.FOOD_DATABASE_NAME, null, Database.FOOD_DATABASE_VERSION);
        this.mContext = context;

        FOOD_DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";

        copyDataBase();
    }

    static FoodHelper getInstance (Context context) throws IOException {
        if (mFoodInstance == null){
            mFoodInstance = new FoodHelper(context);
        }

        return mFoodInstance;
    }

    private boolean checkDataBase() {
        String selectionIdArgsAux[] = new String[1];
        int idAux = Database.FOOD_DATABASE_MAX_NEW;

        try{
            String myPath = FOOD_DATABASE_PATH + Database.FOOD_DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch(SQLException e){
            //database does't exist yet.
        }

        if(checkDB != null){
            if (checkDB.getVersion() != Database.FOOD_DATABASE_VERSION){
                selectionIdArgsAux[0] = String.valueOf(Database.FOOD_DATABASE_MAX);
                String selectionId = Columns.FOOD_ID + " > (?)";

                cursor = checkDB.query(FOOD_TABLE_NAME, null, selectionId,
                        selectionIdArgsAux, null, null, null);

                if (cursor.moveToFirst()){
                    do {
                        idAux++;
                        foodUser.add(commonCreateFoodObjectCursor(cursor));
                        MainActivity.OLD_NEW_IDS.put(cursor.getString(0), String.valueOf(idAux));
                    } while (cursor.moveToNext());

                }

                checkDB.execSQL("DROP TABLE IF EXISTS " + FOOD_TABLE_NAME);
                return false;
            } else {
                checkDB.close();
                return true;
            }
        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }
    
    private void copyUserData(){
        String myPath = FOOD_DATABASE_PATH + Database.FOOD_DATABASE_NAME;
        checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        for(Food f1:foodUser){
            checkDB.insert(FOOD_TABLE_NAME, null, commonCreateFoodMValues(f1));
        }

        checkDB.close();
        cursor.close();
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(Database.FOOD_DATABASE_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(FOOD_DATABASE_PATH + Database.FOOD_DATABASE_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);

        if (cursor!=null){
            copyUserData();
        }

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            /*db.execSQL(String.format(Queries.FOOD_TABLE_CREATE, FOOD_TABLE_NAME, Columns.FOOD_ID,
                    Columns.FOOD_NAME, Columns.FOOD_BRAND, Columns.FOOD_CATEGORIES,
                    Columns.FOOD_BARCODE, Columns.FOOD_UNIT, Columns.FOOD_SUGAR_HUNDRED,
                    Columns.FOOD_SUGAR_PORTION, Columns.FOOD_QUANTITY_PORTION, Columns.FOOD_SUGAR_ITEM,
                    Columns.FOOD_QUANTITY, Columns.FOOD_CONFIRMED, Columns.FOOD_CONFIRMED_BY_USER));*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
