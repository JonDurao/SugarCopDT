package com.dtapps.sugarcop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.dtapps.sugarcop.common.CommonData;

import java.math.BigDecimal;

import static com.dtapps.sugarcop.common.Common.*;
import static com.dtapps.sugarcop.FoodContract.FoodEntry.Columns.*;

public class DetailsActivity extends BaseActivity {
    private Activity mActivityHelper;
    private Button mButtonYes, mButtonNo;
    private ConstraintLayout mConstraintLayoutValidation;
    private ContentResolver mResolver;
    private ContentValues mValues;
    private Food mFoodObject;
    private FloatingActionButton mFabInsertPortion, mFabEdit;
    private LinearLayout mLlButtons, mLlSugarHundred, mLlProgressSugar, mLlSugarPortion;
    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private String[] mCategories;
    private TextView mTvName, mTvBrand, mTvBarcode, mTvCategories, mTvSugarPerHundred,
                            mTvSugarPerPortion, mTvQuantityPortion, mTvConfirm, mTvValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mFoodObject = (Food) getIntent().getExtras().getSerializable(CommonData.getFoodBundleKey());
        Toolbar toolbar = findViewById(R.id.details_toolbar);
        toolbar.setTitle(mFoodObject.getmName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commonInitActivity(this, R.id.adView_details, CommonData.getDetailsColor());

        mActivityHelper = this;
        mResolver = getContentResolver();
        mSharedPreferences = getSharedPreferences(getString(R.string.string_shared_preferences_name), 0);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mValues = new ContentValues();

        mCategories = getResources().getStringArray(R.array.categories_array);

        detailsGetAllViews(this);
        detailsSetUp();

        mFabInsertPortion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsFabInsertListener();
            }
        });

        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(mActivityHelper, AddEditActivity.class);

                addIntent.putExtra(getString(R.string.string_intent_food_bundle), mFoodObject);
                addIntent.putExtra(getString(R.string.string_intent_caller_activity), mActivityHelper.getLocalClassName());

                startActivityForResult(addIntent, 101);
            }
        });

        mButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsButtonsListener(mButtonYes.getText().toString());
            }
        });

        mButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsButtonsListener(mButtonNo.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101){
            if (resultCode == Activity.RESULT_OK){
                Intent newdetailsIntent = new Intent(mActivityHelper, DetailsActivity.class);
                commonOnClickDetailsList(mActivityHelper, mFoodObject.getmId(), mResolver, newdetailsIntent);
                finish();
                startActivity(newdetailsIntent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            return true;
        }
        return false;
    }

    private void detailsButtonsListener(String response){
        int conf = mFoodObject.getmConfirmed();

        if (response.equalsIgnoreCase(mActivityHelper.getString(R.string.string_details_yes))){
            mValues.put(FOOD_CONFIRMED, conf++);
        } else {
            mValues.put(FOOD_CONFIRMED, conf--);
        }

        mValues.put(FOOD_CONFIRMED_BY_USER, 1);

        String args = FOOD_ID + " = ?";
        String[] selecArgs = {mFoodObject.getmId()};
        mResolver.update(FoodContract.CONTENT_URI, mValues, args, selecArgs);

        mConstraintLayoutValidation.setVisibility(View.GONE);
        mFabInsertPortion.setVisibility(View.VISIBLE);

        mValues.clear();
    }

    private void detailsFabInsertListener(){
        BigDecimal currentSugarUser, extraSugar;

        try {
            currentSugarUser = new BigDecimal(Float.valueOf(mSharedPreferences.getString
                    (mActivityHelper.getString(R.string.string_shared_preferences_user_sugar), null)));
        } catch (Exception e){
            currentSugarUser = BigDecimal.ZERO;
        }

        extraSugar = new BigDecimal(mFoodObject.getmSugarPerPortion());

        currentSugarUser = currentSugarUser.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        extraSugar = extraSugar.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        currentSugarUser = currentSugarUser.add(extraSugar);

        mSharedPreferencesEditor.putString(mActivityHelper.getString(R.string.string_shared_preferences_user_sugar), currentSugarUser.toString()).commit();

        commonAddToPreferencesList(false,
                mSharedPreferences,
                mSharedPreferencesEditor,
                mFoodObject.getmId(),
                0,
                mActivityHelper.getString(R.string.string_shared_preferences_user_added));

        Toast.makeText(this,
                getString(R.string.string_details_message_added)
                        + extraSugar
                        + getString(R.string.string_details_message_added_rest),
                Toast.LENGTH_LONG).show();
    }

    private void detailsGetAllViews(Activity activity){
        mButtonNo = activity.findViewById(R.id.details_button_no);
        mButtonYes = activity.findViewById(R.id.details_button_yes);
        mConstraintLayoutValidation = activity.findViewById(R.id.details_cl_validation);
        mFabEdit = activity.findViewById(R.id.details_fab_edit);
        mFabInsertPortion = activity.findViewById(R.id.details_fab_insert_portion);
        mLlButtons = activity.findViewById(R.id.details_ll_buttons);
        mLlProgressSugar = activity.findViewById(R.id.details_ll_progress_bar);
        mLlSugarHundred = activity.findViewById(R.id.details_ll_sugar_hundred);
        mLlSugarPortion = activity.findViewById(R.id.details_ll_sugar_portion);
        mProgressBar = activity.findViewById(R.id.details_pr_sugar);
        mTvBarcode = activity.findViewById(R.id.details_tv_barcode);
        mTvBrand = activity.findViewById(R.id.details_tv_brand);
        mTvCategories = activity.findViewById(R.id.details_tv_categories);
        mTvConfirm = activity.findViewById(R.id.details_tv_confirm);
        mTvName = activity.findViewById(R.id.details_tv_name);
        mTvQuantityPortion = activity.findViewById(R.id.details_tv_sugar_portion_text);
        mTvSugarPerHundred = activity.findViewById(R.id.details_tv_sugar_hundred_value);
        mTvSugarPerPortion = activity.findViewById(R.id.details_tv_sugar_portion_value);
        mTvValidation = activity.findViewById(R.id.details_tv_validation);
    }

    private void detailsSetUp(){
        float sugarAux = Float.valueOf(mSharedPreferences.getString(getResources().getString(R.string.string_shared_preferences_user_sugar_per_day), "100"));

        commonAddToPreferencesList(true,
                mSharedPreferences,
                mSharedPreferencesEditor,
                mFoodObject.getmId(),
                CommonData.getListRecentLimit(),
                mActivityHelper.getString(R.string.string_shared_preferences_user_recent));

        int conf = mFoodObject.getmConfirmed();
        boolean confByUser = (mFoodObject.getmConfirmedByThisUser() == 1) ? true : false;
        float quantityPortionV = mFoodObject.getmQuantityPortion();

        //if (mFoodObject.getmQuantity() == 0.0 || mFoodObject.getmQuantityPortion() == 0.0){
        if (quantityPortionV == 0.0){
            mLlButtons.setVisibility(View.GONE);

            mTvConfirm.setText(R.string.string_details_confirm_edit);
            mTvValidation.setText(R.string.string_details_validation_edit);

            mLlProgressSugar.setVisibility(View.GONE);
            mLlSugarPortion.setVisibility(View.GONE);
        } else{
            if (conf >= 5 || confByUser){
                mConstraintLayoutValidation.setVisibility(View.GONE);
            } else {
                mFabInsertPortion.setVisibility(View.GONE);
            }

            if (quantityPortionV < 0.0){
                mTvQuantityPortion.setText(R.string.string_details_portion_nogr_message);
            } else {
                mTvQuantityPortion.setText(getString(R.string.string_details_portion_message)
                        + mFoodObject.getmQuantityPortion()
                        + getString(R.string.string_details_portion_message_rest));
            }

            mTvSugarPerPortion.setText(String.valueOf(mFoodObject.getmSugarPerPortion())
                    + getString(R.string.string_details_common_gr));

            mProgressBar.setMax(Math.round(sugarAux));
            mProgressBar.setProgress(Math.round(mFoodObject.getmSugarPerPortion()));
        }

        mTvName.setText(mFoodObject.getmName());
        mTvBrand.setText(mFoodObject.getmBrand());

        if (mFoodObject.getmBarcode().equalsIgnoreCase("")){
            mTvBarcode.setVisibility(View.GONE);
        } else {
            mTvBarcode.setText(mFoodObject.getmBarcode());
        }

        if (mFoodObject.getmCategories().equalsIgnoreCase("{}")){
            mTvCategories.setVisibility(View.GONE);
        } else {
            mTvCategories.setText(mCategories[Integer.valueOf(mFoodObject.getmCategories())]);
        }

        if (mFoodObject.getmQuantityPortion() != 100.0 && mFoodObject.getmSugarPerHundred() > 0.0){
            mTvSugarPerHundred.setText(String.valueOf(mFoodObject.getmSugarPerHundred()) + getString(R.string.string_details_common_gr));
        } else {
            mLlSugarHundred.setVisibility(View.GONE);
        }

    }
}
