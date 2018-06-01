package com.dtapps.sugarcop.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtapps.sugarcop.FoodAdapter;
import com.dtapps.sugarcop.FoodSearchAdapter;
import com.dtapps.sugarcop.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.dtapps.sugarcop.common.Common.*;

/**
 * Created by jon.durao on 10/3/2017.
 */

public abstract class UICommon extends BaseAdapter {
    private static final BigDecimal RECOMMENDED_SUGAR_DAY_MEN_KG = BigDecimal.valueOf(36);
    private static final BigDecimal RECOMMENDED_SUGAR_DAY_MEN_LB =
            RECOMMENDED_SUGAR_DAY_MEN_KG.multiply(BigDecimal.valueOf(0.0022));
    private static final BigDecimal RECOMMENDED_SUGAR_DAY_WOMEN_KG = BigDecimal.valueOf(25);
    private static final BigDecimal RECOMMENDED_SUGAR_DAY_WOMEN_LB =
            RECOMMENDED_SUGAR_DAY_WOMEN_KG.multiply(BigDecimal.valueOf(0.0022));


    public static void setTextFieldError(TextInputLayout til, String errorMessage) {
        til.setError(errorMessage);
    }

    public static void setColorScreen(LinearLayout layout, int color, Activity activity) {
        if (layout != null) {
            layout.setBackgroundColor(color);
        }
    }

    public static void setAnimations(Activity activity, RecyclerView recyclerView,
                                     RecyclerView.LayoutManager layoutManager, FoodAdapter adapter) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public static void setAnimationsSearch(Activity activity, RecyclerView recyclerView,
                                           RecyclerView.LayoutManager layoutManager, FoodSearchAdapter adapter) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public static String setSugarLimit(Activity activity, String genre, String height, String weight, int age, String unit) {
        if (weight.equalsIgnoreCase("")
                || height.equalsIgnoreCase("")) {

            if (genre.equalsIgnoreCase(activity.getResources().getString(R.string.string_intro_info_female))) {
                if (unit.equalsIgnoreCase(activity.getResources().getString(R.string.string_intro_switch_unit_me))) {
                    return String.valueOf(RECOMMENDED_SUGAR_DAY_WOMEN_KG);
                } else {
                    return String.valueOf(RECOMMENDED_SUGAR_DAY_WOMEN_LB);
                }
            } else {
                if (unit.equalsIgnoreCase(activity.getResources().getString(R.string.string_intro_switch_unit_me))) {
                    return String.valueOf(RECOMMENDED_SUGAR_DAY_MEN_KG);
                } else {
                    return String.valueOf(RECOMMENDED_SUGAR_DAY_MEN_LB);
                }
            }
        } else {
            try {
                BigDecimal BMR = BigDecimal.ZERO;
                BigDecimal ageD = BigDecimal.valueOf(age);
                BigDecimal heightD = BigDecimal.ZERO;
                BigDecimal weightD = BigDecimal.valueOf(Float.valueOf(weight));

                String mUnit = activity.getString(R.string.string_intro_switch_unit_me);

                if (unit.equalsIgnoreCase(mUnit)) {
                    heightD = BigDecimal.valueOf(Float.valueOf(height));
                } else {
                    String heightAux = height;
                    int feet = heightAux.indexOf("'");
                    int inchesAux = 12 * Integer.valueOf(heightAux.substring(0, 1));
                    int inch = heightAux.indexOf("''");
                    int auxInches = Integer.valueOf(heightAux.substring(feet + 1, inch));
                    heightD = heightD.add(BigDecimal.valueOf(inchesAux).add(BigDecimal.valueOf(auxInches)));
                }

                if (genre.equalsIgnoreCase(activity.getResources().getString(R.string.string_intro_info_female))) {
                    ageD = ageD.multiply(BigDecimal.valueOf(4.7));

                    if (unit.equalsIgnoreCase(activity.getResources().getString(R.string.string_intro_switch_unit_me))) {
                        weightD = weightD.multiply(BigDecimal.valueOf(9.6));
                        heightD = heightD.multiply(BigDecimal.valueOf(1.8));
                    } else {
                        weightD = weightD.multiply(BigDecimal.valueOf(4.35));
                        heightD = heightD.multiply(BigDecimal.valueOf(4.7));
                    }

                    BMR = BMR.add(BigDecimal.valueOf(655))
                            .add(weightD)
                            .add(heightD)
                            .subtract(ageD);
                } else {
                    ageD = ageD.multiply(BigDecimal.valueOf(6.8));

                    if (unit.equalsIgnoreCase(activity.getResources().getString(R.string.string_intro_switch_unit_me))) {
                        weightD = weightD.multiply(BigDecimal.valueOf(13.7));
                        heightD = heightD.multiply(BigDecimal.valueOf(5));
                    } else {
                        weightD = weightD.multiply(BigDecimal.valueOf(6.23));
                        heightD = heightD.multiply(BigDecimal.valueOf(12.7));
                    }

                    BMR = BMR.add(BigDecimal.valueOf(66))
                            .add(weightD)
                            .add(heightD)
                            .subtract(ageD);
                }

                BMR = BMR.multiply(BigDecimal.valueOf(0.06))
                        .divide(BigDecimal.valueOf(4));

                BMR = BMR.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                String result = BMR.toString();

                return result.replace(",",".");
            } catch (Exception e){
                Toast.makeText(activity, "¡Error!",
                        Toast.LENGTH_LONG).show();

                return null;
            }
        }
    }

    public static void uiCommonOpenListDialog (final Activity activity, int array, String title,
                                               final TextView tvResult, final EditText etQuantity){
        final String[] listItems = activity.getResources().getStringArray(array);
        final List<String> lista = new ArrayList<>();
        Collections.addAll(lista, listItems);
        Arrays.sort(listItems);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setTitle(title);
        dialogBuilder.setItems(listItems,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BigDecimal[] quantities = CommonData.getCategoryArray();
                int auxOldCat = -1;

                if (!tvResult.getText().toString().isEmpty()){
                    try {
                        auxOldCat = Integer.valueOf(tvResult.getHint().toString());
                    } catch (NullPointerException e){
                        auxOldCat = -2;
                    }
                }

                tvResult.setText(listItems[i]);
                tvResult.setHint(String.valueOf(lista.indexOf(listItems[i])));

                if (etQuantity.getText().toString().isEmpty()){
                    etQuantity.setText(String.valueOf(quantities[lista.indexOf(listItems[i])]));
                }

                if (auxOldCat >= 0){
                     if (quantities[auxOldCat].equals(BigDecimal.valueOf(Float.valueOf(etQuantity.getText().toString())))){
                        etQuantity.setText(String.valueOf(quantities[lista.indexOf(listItems[i])]));
                    }
                }


                dialogInterface.dismiss();
            }});

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static void uiCommonOpenEtDialog (final Activity activity, String title, String subtitle,
                                             String pButton, String nButton, String hint, String type,
                                             final SharedPreferences.Editor editor, final String property){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_et_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextInputLayout til = dialogView.findViewById(R.id.custom_dialog_til);
        final EditText edt = til.getEditText();
        til.setHint(title);
        edt.setText(hint);

        if (type.equalsIgnoreCase(activity.getString(R.string.string_settings_string))){
            edt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        } else if (type.equalsIgnoreCase(activity.getString(R.string.string_settings_number))){
            edt.setInputType(InputType.TYPE_CLASS_NUMBER);
            commonInitEtChangeTextListenerDecimal(edt,0,3);
        } else if (type.equalsIgnoreCase(activity.getString(R.string.string_settings_number_decimal))){
            edt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            commonInitEtChangeTextListenerDecimal(edt, 3,3);
        }

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(subtitle);
        dialogBuilder.setPositiveButton(pButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (property.equalsIgnoreCase(activity.getString(R.string.string_shared_preferences_user_age))){
                    if (edt.getText().toString().isEmpty()){
                        editor.putInt(property, 0).commit();
                    } else {
                        try {
                            editor.putInt(property, Integer.valueOf(edt.getText().toString())).commit();
                        } catch (Exception e){
                            Toast.makeText(activity, "¡Error!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    editor.putString(property, edt.getText().toString()).commit();
                }
            }
        });

        dialogBuilder.setNegativeButton(nButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static void uiCommonOpenAboutDialog (final Activity activity, String title, String subtitle){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_about_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(title);
        //dialogBuilder.setMessage(subtitle);

        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
