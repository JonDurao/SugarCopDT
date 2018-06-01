package com.dtapps.sugarcop.common;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.dtapps.sugarcop.Food;
import com.dtapps.sugarcop.FoodContract;
import com.dtapps.sugarcop.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.dtapps.sugarcop.FoodContract.FoodEntry.Columns.*;

public class Common extends CommonData{
    protected static String mLastEtValue;

    public static int commonGetColor (Activity activity, int color){
        return ContextCompat.getColor(activity, color);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.getResources().getColor(color, null);
        } else {
            return activity.getColor(color);
        }*/
    }

    public static String commonEliminateBrackets(String source){
        if (source.contains("{") || source.contains("}")){
            int startS = source.indexOf("{");
            int endS = source.indexOf("}");
            return source.substring(startS + 1, endS);
        } else {
            return source;
        }
    }

    public static List commonPreferencesIdList(SharedPreferences settings, String sharedProperty){
        List<String> recentList = new ArrayList<>();
        String recents = settings.getString(sharedProperty, null);
        String[] recentFoods;

        if (recents != null) {
            recentFoods = commonEliminateBrackets(recents).split(",");

            for (String auxRecent : recentFoods)
                recentList.add(auxRecent);
        }

        return recentList;
    }

    public static void commonDeleteFromPreferences(SharedPreferences settings, SharedPreferences.Editor editor,
                                                 String id, String sharedProperty){
        List<String> recentList = commonPreferencesIdList(settings, sharedProperty);
        StringBuilder finalCurrent = new StringBuilder();

        if (recentList != null){
            if (recentList.contains(id)){
                recentList.remove(recentList.lastIndexOf(id));
            }
        }

        for (int idAux = (recentList.size()-1); idAux >= 0; idAux--){
            finalCurrent.append(recentList.get(idAux));

            if (idAux != 0)
                finalCurrent.append(",");
        }

        editor.putString(sharedProperty, finalCurrent.toString());
        editor.commit();
    }

    public static void commonAddToPreferencesList(boolean careDuplicates, SharedPreferences settings,
                                              SharedPreferences.Editor editor, String id,
                                              int maxLength, String sharedProperty){
        List<String> recentList = commonPreferencesIdList(settings, sharedProperty);
        StringBuilder finalCurrent = new StringBuilder();

        if (recentList.size() != 0) {
            boolean presentFood = false;

            if (recentList.contains(id))
                presentFood = true;

            if (!careDuplicates){
                recentList.add(id);
            } else if (!presentFood && careDuplicates) {
                    if (maxLength == 0) {
                        recentList.add(id);
                    } else if (recentList.size() == maxLength) {
                        recentList.remove(0);
                        recentList.add(id);
                    } else {
                        recentList.add(id);
                    }
            } else if (presentFood){
                int aux = recentList.indexOf(id);
                recentList.remove(aux);
                recentList.add(id);
            }
        } else {
            recentList.add(id);
        }

        for (int idAux = (recentList.size()-1); idAux >= 0; idAux--){
            finalCurrent.append(recentList.get(idAux));

            if (idAux != 0)
                finalCurrent.append(",");
        }

        editor.putString(sharedProperty, finalCurrent.toString());
        editor.commit();
    }

    public static void commonUpdateToPreferencesList(SharedPreferences settings,
                                                     SharedPreferences.Editor editor,
                                                     String sharedProperty,
                                                     String newV, String oldV){
        List<String> recentList = commonPreferencesIdList(settings, sharedProperty);
        List<String> recentAuxList = new ArrayList<>();
        StringBuilder finalCurrent = new StringBuilder();

        if (recentList.size() != 0) {
            if (recentList.contains(oldV)){
                int index = recentList.indexOf(oldV);
                recentList.remove(index);

                for (int i = 0; i <= recentList.size(); i++){
                    if (index == i){
                        recentAuxList.add(newV);
                    }
                    if (!recentList.isEmpty()){
                        recentAuxList.add(recentList.get(i));
                    }
                }
            }
        }

        for (int idAux = (recentAuxList.size()-1); idAux >= 0; idAux--){
            finalCurrent.append(recentAuxList.get(idAux));

            if (idAux != 0)
                finalCurrent.append(",");
        }

        editor.putString(sharedProperty, finalCurrent.toString());
        editor.commit();
    }

    public static void commonUpdateSugar(SharedPreferences settings,
                                                     SharedPreferences.Editor editor,
                                                     String sharedProperty,
                                                     BigDecimal newV, BigDecimal oldV){
        BigDecimal currentSugarUser;

        try {
            currentSugarUser = new BigDecimal(Float.valueOf(settings.getString(sharedProperty, null)));
        } catch (Exception e){
            currentSugarUser = BigDecimal.ZERO;
        }

        currentSugarUser = currentSugarUser.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        newV = newV.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        currentSugarUser = currentSugarUser.subtract(oldV);
        currentSugarUser = currentSugarUser.add(newV);

        editor.putString(sharedProperty, currentSugarUser.toString()).commit();
    }

    public static Cursor commonQueryIdFood(String hint, ContentResolver resolver) {
        String mSelectionClause = FoodContract.FoodEntry.Columns.FOOD_ID + " = ?";
        String [] mSelectionClauseArgs = {hint};
        Cursor cursor = resolver.query(FoodContract.CONTENT_URI, null, mSelectionClause,
                mSelectionClauseArgs, null);

        cursor.moveToFirst();

        return cursor;
    }

    public static void commonOnClickDetailsList(Activity activity, String hint, ContentResolver resolver, Intent intent){
        intent.putExtra(CommonData.getFoodBundleKey(), commonCreateFoodObjectCursor(
                                commonQueryIdFood(hint, resolver)));
        activity.startActivity(intent);
    }

    public static void commonCreateLikeQueryNameBrand (List<String> listIds,
                                                         List<String> listParameters, String query,
                                                         String column){
        listParameters.add(column + getSqlLike());
        listIds.add("%" + query + "%");
    }

    public static void commonOnQuerySearch(Context context, ContentResolver resolver, List<Food> list,
                                           String query, RecyclerView.Adapter adapter, String sort) {
        String mSelectionClause;
        String[] mAux, mSelectionClauseArgs;
        List<String> recentList = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        list.clear();

        if (query != null){
            mAux = query.split(context.getString(R.string.string_regex_space));

            if (query.contains("ñ") || query.contains("Ñ")){
                commonCreateLikeQueryNameBrand(recentList, parameters, query,
                        FoodContract.FoodEntry.Columns.FOOD_NAME);
                commonCreateLikeQueryNameBrand(recentList, parameters, query,
                        FoodContract.FoodEntry.Columns.FOOD_BRAND);
            }

            commonCreateLikeQueryNameBrand(recentList, parameters, query,
                    FoodContract.FoodEntry.Columns.FOOD_NAME_NORMALIZED);
            commonCreateLikeQueryNameBrand(recentList, parameters, query,
                    FoodContract.FoodEntry.Columns.FOOD_BRAND_NORMALIZED);

            for (String auxRecent : mAux){
                if (query.contains("ñ") || query.contains("Ñ")){
                    commonCreateLikeQueryNameBrand(recentList, parameters, auxRecent,
                            FoodContract.FoodEntry.Columns.FOOD_NAME);
                    commonCreateLikeQueryNameBrand(recentList, parameters, auxRecent,
                            FoodContract.FoodEntry.Columns.FOOD_BRAND);
                }
                commonCreateLikeQueryNameBrand(recentList, parameters, auxRecent,
                        FoodContract.FoodEntry.Columns.FOOD_NAME_NORMALIZED);
                commonCreateLikeQueryNameBrand(recentList, parameters, auxRecent,
                        FoodContract.FoodEntry.Columns.FOOD_BRAND_NORMALIZED);
            }

            mSelectionClauseArgs = new String[recentList.size()];
            recentList.toArray(mSelectionClauseArgs);
        } else {
            mSelectionClauseArgs = new String[] {};
        }

        mSelectionClause = TextUtils.join(" OR ", parameters);
        Cursor cursor = resolver.query(FoodContract.CONTENT_URI, null,
                mSelectionClause, mSelectionClauseArgs, sort);

        if (cursor.moveToFirst()){
            do{
                list.add(commonCreateFoodObjectCursor(cursor));
            } while (cursor.moveToNext());
        }

        adapter.notifyDataSetChanged();
    }

    public static Food commonCreateFoodObjectCursor(Cursor cursor){
        return new Food(cursor.getString(cursor.getColumnIndex(FOOD_ID)),
                cursor.getString(cursor.getColumnIndex(FOOD_NAME)),
                cursor.getString(cursor.getColumnIndex(FOOD_BRAND)),
                cursor.getString(cursor.getColumnIndex(FOOD_CATEGORIES)),
                cursor.getString(cursor.getColumnIndex(FOOD_BARCODE)),
                cursor.getString(cursor.getColumnIndex(FOOD_UNIT)),
                cursor.getFloat(cursor.getColumnIndex(FOOD_SUGAR_HUNDRED)),
                cursor.getFloat(cursor.getColumnIndex(FOOD_SUGAR_PORTION)),
                cursor.getFloat(cursor.getColumnIndex(FOOD_SUGAR_ITEM)),
                cursor.getFloat(cursor.getColumnIndex(FOOD_QUANTITY_PORTION)),
                cursor.getFloat(cursor.getColumnIndex(FOOD_QUANTITY)),
                cursor.getInt(cursor.getColumnIndex(FOOD_CONFIRMED)),
                cursor.getInt(cursor.getColumnIndex(FOOD_CONFIRMED_BY_USER)),
                cursor.getString(cursor.getColumnIndex(FOOD_NAME_NORMALIZED)),
                cursor.getString(cursor.getColumnIndex(FOOD_BRAND_NORMALIZED)));
    }

    public static ContentValues commonCreateFoodMValues (Food food){
        ContentValues contentValues = new ContentValues();

        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_NAME, food.getmName());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_BRAND, food.getmBrand());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_CATEGORIES, food.getmCategories());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_BARCODE, food.getmBarcode());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_UNIT, food.getmUnit());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_SUGAR_HUNDRED, food.getmSugarPerHundred());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_SUGAR_PORTION, food.getmSugarPerPortion());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_QUANTITY_PORTION, food.getmQuantityPortion());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_SUGAR_ITEM, food.getmSugarPerItem());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_QUANTITY, 0f);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_CONFIRMED, 0);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_CONFIRMED_BY_USER, 1);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_NAME_NORMALIZED, food.getmNormilizedName());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_BRAND_NORMALIZED, food.getmNormilizedBrand());

        return contentValues;
    }

    public static ContentValues commonCreateFoodMValues (Float quantityItem,
                                                         BigDecimal quantityPortion,
                                                         BigDecimal sugarHundred,
                                                         BigDecimal sugarItem,
                                                         BigDecimal sugarPortion,
                                                         String barcode,
                                                         String brand,
                                                         String categories,
                                                         String name,
                                                         String unit){
        ContentValues contentValues = new ContentValues();

        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_NAME, name);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_BRAND, brand);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_CATEGORIES, categories);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_BARCODE, barcode);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_UNIT, unit);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_SUGAR_HUNDRED, sugarHundred.floatValue());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_SUGAR_PORTION, sugarPortion.floatValue());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_QUANTITY_PORTION, quantityPortion.floatValue());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_SUGAR_ITEM, sugarItem.floatValue());
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_QUANTITY, 0f);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_CONFIRMED, 0);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_CONFIRMED_BY_USER, 1);
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_NAME_NORMALIZED,
                commonNormalizeString(name));
        contentValues.put(FoodContract.FoodEntry.Columns.FOOD_BRAND_NORMALIZED,
                commonNormalizeString(brand));

        return contentValues;
    }

    public static String commonCreateCardinal(Activity activity, int i){
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = activity.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = activity.getResources().getConfiguration().locale;
        }

        String aux = String.valueOf(i);
        String a = locale.toString();

        if (a.contains("es_")){
            return i + "º";
        } else if (locale.getDisplayLanguage().equalsIgnoreCase("en")){
            if (i == 11 || i == 12 || i == 13){
                return i + "th";
            } else if (aux.charAt(aux.length()-1) == '1'){
                return i + "st";
            } else if (aux.charAt(aux.length()-1) == '2'){
                return i + "nd";
            } else if (aux.charAt(aux.length()-1) == '3'){
                return i + "rd";
            } else {
                return i + "th";
            }
        } else {
            return String.valueOf(i);
        }
     }

    public static void commonInitActivity(Activity activity,
                                          int adView,
                                          int color){
        MobileAds.initialize(activity, CommonData.getAddId());
        AdView adViewElem = activity.findViewById(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewElem.loadAd(adRequest);

        activity.getWindow().setStatusBarColor(commonGetColor(activity, color));
        activity.getWindow().setNavigationBarColor(commonGetColor(activity, color));
    }

    public static void commonInitEtChangeTextListenerDecimal (final EditText editText,
                                                              final int numberDecimals,
                                                              final int numberInteger){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mLastEtValue = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().contains(".")){
                    editText.setText("");
                } else if (!s.toString().contains(".") && s.toString().length() > numberInteger){
                    editText.setText(mLastEtValue);
                } else if (s.toString().contains(".")){
                    int auxDot = s.toString().indexOf(".");
                    if (s.toString().substring(auxDot).length() > numberDecimals){
                        editText.setText(mLastEtValue);
                    }
                }
                editText.setSelection(editText.getText().length());
            }
        });
    }

    public static String commonRetrieveMonthName (Activity activity, int i){
        String[] arrayMonths = activity.getResources().getStringArray(R.array.months_array);

        switch (i){
            case 0:
                return arrayMonths[0];
            case 1:
                return arrayMonths[1];
            case 2:
                return arrayMonths[2];
            case 3:
                return arrayMonths[3];
            case 4:
                return arrayMonths[4];
            case 5:
                return arrayMonths[5];
            case 6:
                return arrayMonths[6];
            case 7:
                return arrayMonths[7];
            case 8:
                return arrayMonths[8];
            case 9:
                return arrayMonths[9];
            case 10:
                return arrayMonths[10];
            case 11:
                return arrayMonths[11];
            default:
                return "";
        }
    }

    public static void commonLongTapDelete (final Activity activity, final ContentResolver resolver,
                                            final SharedPreferences sharedPreferences, final SharedPreferences.Editor editor,
                                            final View v, RecyclerView rv, final RecyclerView.Adapter adapter, final List<Food> foodList,
                                            final int position, final DecoView dv, final int SERIES_ID, final TextView rTitle, final TextView rSubtitle) {
        final TextView tvN = v.findViewById(R.id.main_recycler_item_text_view_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(R.string.string_common_delete_list_message);
        builder.setTitle(R.string.string_common_delete_list_title);

        builder.setPositiveButton(R.string.string_common_delete_list_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Food auxFoodDelete = commonCreateFoodObjectCursor(commonQueryIdFood(tvN.getHint().toString(), resolver));

                Float currentSugarUser = Float.valueOf(sharedPreferences.getString
                        (activity.getString(R.string.string_shared_preferences_user_sugar),
                                null));

                Float maxSugar = Float.valueOf(sharedPreferences.getString(activity.getResources().getString(R.string.string_shared_preferences_user_sugar_per_day), null));

                Float extraSugar = Float.valueOf(auxFoodDelete.getmSugarPerPortion());

                BigDecimal bdcurrentSugar = new BigDecimal(currentSugarUser);
                BigDecimal bdItemSugar = new BigDecimal(extraSugar);
                BigDecimal bdMaxSugar = new BigDecimal(maxSugar);
                bdcurrentSugar = bdcurrentSugar.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                bdItemSugar = bdItemSugar.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                bdMaxSugar = bdMaxSugar.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                if (bdcurrentSugar.compareTo(BigDecimal.ZERO) == 1)
                    bdcurrentSugar = bdcurrentSugar.subtract(bdItemSugar);

                if (bdcurrentSugar.compareTo(BigDecimal.ZERO) == -1)
                    bdcurrentSugar = BigDecimal.ZERO;

                if (bdcurrentSugar.compareTo(BigDecimal.ZERO) == -1 ||
                        commonPreferencesIdList(sharedPreferences, activity.getString(R.string.string_shared_preferences_user_added)).size() == 0){
                    editor.putString(activity.getString(R.string.string_shared_preferences_user_sugar), "0.0").commit();
                } else {
                    editor.putString(activity.getString(R.string.string_shared_preferences_user_sugar), bdcurrentSugar.toString()).commit();
                }

                adapter.notifyItemRemoved(position);

                int levelsCurrent = Math.round(bdcurrentSugar.divide(bdMaxSugar,RoundingMode.DOWN).floatValue() * 10);

                if (levelsCurrent > 10)
                    levelsCurrent = 10;

                dv.addEvent(new DecoEvent.Builder(bdcurrentSugar.floatValue())
                        .setIndex(SERIES_ID)
                        .build());

                mainPrepareSugarText(
                        activity,
                        levelsCurrent,
                        bdcurrentSugar,
                        bdMaxSugar,
                        (TextView) activity.findViewById(R.id.main_tv_ring_title),
                        (TextView) activity.findViewById(R.id.main_tv_ring_subtitle));

                int  i = -1;

                for (Food food : foodList){
                    if (food.getmId().equalsIgnoreCase(auxFoodDelete.getmId())){
                        i = foodList.indexOf(food);
                    }
                }

                foodList.remove(i);

                commonDeleteFromPreferences(sharedPreferences, editor, auxFoodDelete.getmId(),
                        activity.getString(R.string.string_shared_preferences_user_added));

                Common.mainPrepareSugarText(activity, levelsCurrent, bdcurrentSugar, bdMaxSugar, rTitle, rSubtitle);

                if (foodList.isEmpty()){
                    RelativeLayout llAux = activity.findViewById(R.id.main_ll_empty_list_view);
                    llAux.setVisibility(View.VISIBLE);
                }

                Toast.makeText(activity,
                        activity.getString(R.string.string_common_delete_list_removed)
                                + auxFoodDelete.getmSugarPerPortion()
                                + activity.getString(R.string.string_common_delete_list_removed_rest),
                        Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(R.string.string_common_delete_list_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void mainPrepareSugarText (Activity activity, int levels, BigDecimal actualSugar,
                                             BigDecimal maxSugar, TextView tvRTitle, TextView tvRSubtitle){
        if (levels < 7){
            tvRTitle.setText(R.string.string_main_levels_1);
        } else if (levels < 10){
            tvRTitle.setText(R.string.string_main_levels_2);
        } else {
            if (actualSugar.compareTo(BigDecimal.valueOf(100)) == 1){
                tvRTitle.setText(R.string.string_main_levels_4);
            } else {
                tvRTitle.setText(R.string.string_main_levels_3);        
            }
        
        }

        tvRSubtitle.setText(actualSugar.toString()+ activity.getString(R.string.string_main_ring_subtitle)
                + maxSugar.toString() + activity.getString(R.string.string_details_common_gr));
    }

    public static String commonNormalizeString (String s){
        String ss = Normalizer.normalize(s, Normalizer.Form.NFD);
        ss = ss.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return ss;
    }
}
