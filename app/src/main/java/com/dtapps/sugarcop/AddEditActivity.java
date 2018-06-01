package com.dtapps.sugarcop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dtapps.sugarcop.common.CommonData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.dtapps.sugarcop.common.Common.*;
import static com.dtapps.sugarcop.common.UICommon.*;

public class AddEditActivity extends BaseActivity{
    private Activity mActivityHelper;
    private BigDecimal newV = BigDecimal.ZERO, oldV = BigDecimal.ZERO;
    private ContentResolver mResolver;
    private ContentValues mValues;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Food mFoodIntent;
    private ImageView mIvCategoryDropDown;
    private RelativeLayout mRlCategories;
    private String mCaller, mArgs;
    private String[] mArgsSelection, mCategories;
    private TextInputLayout mTilName, mTilBrand, mTilBarcode,
            mTilQuantityPortion, mTilSugarPerHundred;
    private TextView textViewMessage, mTvCategoryError, mTvCategoryTitle, mTvCategoryValue;
    private View mDCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.add_edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commonInitActivity(this, R.id.adView_add_edit, CommonData.getAddColor());

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.string_shared_preferences_name), 0);
        mEditor = mSharedPreferences.edit();

        mActivityHelper = this;
        mResolver = getContentResolver();
        mValues = new ContentValues();
        mCaller = getIntent().getExtras().getString(getString(R.string.string_intent_caller_activity));

        mCategories = getResources().getStringArray(R.array.categories_array);

        addEditGetViews(this);

        if (mCaller.equalsIgnoreCase(DetailsActivity.class.getSimpleName())){
            mFoodIntent = (Food) getIntent().getExtras().getSerializable(CommonData.getFoodBundleKey());
            addEditFillDetails(this, mFoodIntent);

            mArgs = FoodContract.FoodEntry.Columns.FOOD_ID + " = ?";
            mArgsSelection = new String[]{mFoodIntent.getmId()};

            oldV = BigDecimal.valueOf(mFoodIntent.getmSugarPerPortion());
        }

        addEditTextListener();
    }

    private void addEditTextListener(){
        commonInitEtChangeTextListenerDecimal(mTilQuantityPortion.getEditText(), 2,3);
        commonInitEtChangeTextListenerDecimal(mTilSugarPerHundred.getEditText(), 3,3);
        mRlCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uiCommonOpenListDialog(mActivityHelper,
                        R.array.categories_array,
                        getString(R.string.string_add_edit_categories),
                        mTvCategoryValue,
                        mTilQuantityPortion.getEditText());
            }
        });
    }

    private void addEditGetViews(Activity activity){
        mDCategory = activity.findViewById(R.id.add_edit_divider_categories);
        mIvCategoryDropDown = activity.findViewById(R.id.add_edit_iv_drop_down);
        mRlCategories = activity.findViewById(R.id.add_edit_rl_categories);
        mTilName = activity.findViewById(R.id.add_edit_til_name);
        mTilBrand = activity.findViewById(R.id.add_edit_til_brand);
        mTilBarcode = activity.findViewById(R.id.add_edit_til_barcode);
        mTilQuantityPortion = activity.findViewById(R.id.add_edit_til_quantity_portion);
        mTilSugarPerHundred = activity.findViewById(R.id.add_edit_til_sugar_per_100);
        mTvCategoryError = activity.findViewById(R.id.add_edit_tv_categories_error);
        mTvCategoryTitle = activity.findViewById(R.id.add_edit_tv_categories);
        mTvCategoryValue = activity.findViewById(R.id.add_edit_tv_categories_value);
        textViewMessage = activity.findViewById(R.id.add_edit_tv_validation);
    }

    private void addEditFillDetails(Activity activity, Food food) throws NullPointerException{
        try {
            if (food != null){
                mTilName.getEditText().setText(food.getmName());
                mTilBrand.getEditText().setText(food.getmBrand());
                mTvCategoryValue.setText(mCategories[Integer.valueOf(food.getmCategories())]);
                mTvCategoryValue.setHint(food.getmCategories());
                mTilBarcode.getEditText().setText(mFoodIntent.getmBarcode());

                if (mFoodIntent.getmQuantityPortion() == 0.0){
                    mTilQuantityPortion.getEditText().setText("0");
                } else {
                    mTilQuantityPortion.getEditText().setText(String.valueOf(Math.round(food.getmQuantityPortion())));
                }

                mTilSugarPerHundred.getEditText().setText(String.valueOf(food.getmSugarPerHundred()));

                textViewMessage.setText(activity.getString(R.string.string_add_edit_editing_text));
            }
        } catch (Exception e){
            Toast.makeText(activity, "¡Error addEditFillDetails!", Toast.LENGTH_LONG).show();
        }
    }

    private void addEditFillValues(Activity activity){
        try {
            newV = newV.add(BigDecimal.valueOf(Float.valueOf(mTilSugarPerHundred.getEditText().getText().toString())))
                    .multiply(BigDecimal.valueOf(Float.valueOf(mTilQuantityPortion.getEditText().getText().toString())))
                    .divide(BigDecimal.valueOf(100));

            newV = newV.setScale(2,BigDecimal.ROUND_HALF_EVEN);

            mValues = commonCreateFoodMValues(0f,
                    BigDecimal.valueOf(Float.valueOf(mTilQuantityPortion.getEditText().getText().toString())).setScale(2,BigDecimal.ROUND_HALF_EVEN),
                    BigDecimal.valueOf(Float.valueOf(mTilSugarPerHundred.getEditText().getText().toString())).setScale(2,BigDecimal.ROUND_HALF_EVEN),
                    BigDecimal.ZERO,
                    newV,
                    mTilBarcode.getEditText().getText().toString(),
                    mTilBrand.getEditText().getText().toString(),
                    mTvCategoryValue.getHint().toString(),
                    mTilName.getEditText().getText().toString(),
                    mSharedPreferences.getString(activity.getString(R.string.string_shared_preferences_user_unit), ""));

            Uri uri = mResolver.insert(FoodContract.CONTENT_URI, mValues);
            String id = uri.getPathSegments().get(uri.getPathSegments().size()-1);

            commonAddToPreferencesList(true, mSharedPreferences, mEditor, id,
                    CommonData.getListRecentLimit(), mActivityHelper.getString(R.string.string_shared_preferences_user_recent));
        } catch (Exception e){
            Toast.makeText(activity, "¡Error addEditFillValues!", Toast.LENGTH_LONG).show();
        }
    }

    private void addEditUpdateValues(Activity activity){
        try {
            newV = BigDecimal.valueOf(Float.valueOf(mTilSugarPerHundred.getEditText().getText().toString()))
                    .multiply(BigDecimal.valueOf(Float.valueOf(mTilQuantityPortion.getEditText().getText().toString())))
                    .divide(BigDecimal.valueOf(100));

            newV = newV.setScale(2,BigDecimal.ROUND_HALF_EVEN);

            BigDecimal auxSugarItem =
                    newV.multiply(BigDecimal.valueOf(Float.valueOf(mTilQuantityPortion.getEditText().getText().toString())))
                            .divide(BigDecimal.valueOf(100));
            auxSugarItem = auxSugarItem.setScale(2, BigDecimal.ROUND_HALF_EVEN);

            mValues = commonCreateFoodMValues(mFoodIntent.getmQuantity(),
                    BigDecimal.valueOf(Float.valueOf(mTilQuantityPortion.getEditText().getText().toString())).setScale(2, RoundingMode.HALF_EVEN),
                    BigDecimal.valueOf(Float.valueOf(mTilSugarPerHundred.getEditText().getText().toString())).setScale(2, RoundingMode.HALF_EVEN),
                    auxSugarItem,
                    newV,
                    mTilBarcode.getEditText().getText().toString(),
                    mTilBrand.getEditText().getText().toString(),
                    mTvCategoryValue.getHint().toString(),
                    mTilName.getEditText().getText().toString(),
                    mSharedPreferences.getString(activity.getString(R.string.string_shared_preferences_user_unit), ""));

            mResolver.update(FoodContract.CONTENT_URI, mValues, mArgs, mArgsSelection);

            List<String> recentList = commonPreferencesIdList(mSharedPreferences,
                    mActivityHelper.getString(R.string.string_shared_preferences_user_added));

            if (recentList.contains(mFoodIntent.getmId())){
                commonUpdateSugar(mSharedPreferences,
                        mEditor,
                        mActivityHelper.getString(R.string.string_shared_preferences_user_sugar),
                        newV,
                        oldV);
            }
        } catch (Exception e){
            Toast.makeText(activity, "¡Error addEditUpdateValues!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCaller.equalsIgnoreCase(DetailsActivity.class.getSimpleName())){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mTilName.getEditText().getText().toString().isEmpty()
                || mTilBrand.getEditText().getText().toString().isEmpty()
                || mTilSugarPerHundred.getEditText().getText().toString().isEmpty()
                || mTilQuantityPortion.getEditText().getText().toString().isEmpty()
                || mTvCategoryValue.getText().toString().isEmpty()){

            if (mTilName.getEditText().getText().toString().trim().isEmpty())
                setTextFieldError(mTilName, getString(R.string.string_add_edit_name_error));

            if (mTilBrand.getEditText().getText().toString().trim().isEmpty())
                setTextFieldError(mTilBrand, getString(R.string.string_add_edit_brand_error));

            if (mTilSugarPerHundred.getEditText().getText().toString().trim().isEmpty())
                setTextFieldError(mTilSugarPerHundred, getString(R.string.string_add_edit_sugar_hundred_error));

            if (mTilQuantityPortion.getEditText().getText().toString().trim().isEmpty())
                setTextFieldError(mTilQuantityPortion, getString(R.string.string_add_edit_quantity_portion_error));
            if (mTvCategoryValue.getText().toString().isEmpty()){
                mTvCategoryError.setVisibility(View.VISIBLE);
                mDCategory.setBackgroundColor(commonGetColor(this, R.color.colorLevelNine9));
                mIvCategoryDropDown.setColorFilter(commonGetColor(this, R.color.colorLevelNine9));
                mTvCategoryTitle.setTextColor(commonGetColor(this, R.color.colorLevelNine9));
            }
        } else {
            switch (item.getItemId()){
                case android.R.id.home:
                    if (mCaller.equalsIgnoreCase(DetailsActivity.class.getSimpleName())){
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    } else {
                        finish();
                    }
                    break;
                case R.id.action_add_edit_confirm:
                    if (mCaller.equalsIgnoreCase(DetailsActivity.class.getSimpleName())){
                        addEditUpdateValues(mActivityHelper);

                        mValues.clear();
                        Toast.makeText(AddEditActivity.this, R.string.string_add_edit_success, Toast.LENGTH_LONG).show();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    } else {
                        addEditFillValues(mActivityHelper);

                        mValues.clear();
                        Toast.makeText(AddEditActivity.this, R.string.string_add_edit_add_success, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}