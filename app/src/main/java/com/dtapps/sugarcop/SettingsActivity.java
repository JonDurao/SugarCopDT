package com.dtapps.sugarcop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.dtapps.sugarcop.common.CommonData;

import static com.dtapps.sugarcop.common.UICommon.*;
import static com.dtapps.sugarcop.common.Common.*;

public class SettingsActivity extends BaseActivity {
    protected Activity mActivity;
    protected TextView mSettingsTvName, mSettingsTvSurname, mSettingsTvAge, mSettingsTvHeight,
            mSettingsTvWeight, mSettingsTvMaximunSugar;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commonInitActivity(this, R.id.adView_settings, CommonData.getSettingsColor());

        mActivity = this;
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.string_shared_preferences_name), 0);
        mEditor = mSharedPreferences.edit();
        settingsInitViews();
        settingsInitListenerTv();
    }

    protected void settingsInitViews (){
        mSettingsTvName = findViewById(R.id.settings_tv_info_name);
        mSettingsTvSurname = findViewById(R.id.settings_tv_info_surname);
        mSettingsTvAge = findViewById(R.id.settings_tv_info_age);
        mSettingsTvHeight = findViewById(R.id.settings_tv_body_height);
        mSettingsTvWeight = findViewById(R.id.settings_tv_body_weight);
        mSettingsTvMaximunSugar = findViewById(R.id.settings_tv_body_sugar_quantity);
    }

    protected void settingsInitListenerTv (){
        mSettingsTvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCommonOpenEtDialog(mActivity,
                        mSettingsTvName.getText().toString(),
                        getString(R.string.string_settings_subtitle) + mSettingsTvName.getText().toString().toLowerCase(),
                        getString(R.string.string_button_ok),
                        getString(R.string.string_button_cancel),
                        mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_name), null),
                        getString(R.string.string_settings_string),
                        mEditor,
                        getString(R.string.string_shared_preferences_user_name));
            }
        });

        mSettingsTvSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCommonOpenEtDialog(mActivity,
                        mSettingsTvSurname.getText().toString(),
                        getString(R.string.string_settings_subtitle) + mSettingsTvSurname.getText().toString().toLowerCase(),
                        getString(R.string.string_button_ok),
                        getString(R.string.string_button_cancel),
                        mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_surname), null),
                        getString(R.string.string_settings_string),
                        mEditor,
                        getString(R.string.string_shared_preferences_user_surname));
            }
        });

        mSettingsTvAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCommonOpenEtDialog(mActivity,
                        mSettingsTvAge.getText().toString(),
                        getString(R.string.string_settings_subtitle) + mSettingsTvAge.getText().toString().toLowerCase(),
                        getString(R.string.string_button_ok),
                        getString(R.string.string_button_cancel),
                        String.valueOf(mSharedPreferences.getInt(getString(R.string.string_shared_preferences_user_age), 0)),
                        getString(R.string.string_settings_number),
                        mEditor,
                        getString(R.string.string_shared_preferences_user_age));
            }
        });

        mSettingsTvHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCommonOpenEtDialog(mActivity,
                        mSettingsTvHeight.getText().toString(),
                        getString(R.string.string_settings_subtitle) + mSettingsTvHeight.getText().toString().toLowerCase(),
                        getString(R.string.string_button_ok),
                        getString(R.string.string_button_cancel),
                        mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_height), null),
                        getString(R.string.string_settings_number),
                        mEditor,
                        getString(R.string.string_shared_preferences_user_height));
            }
        });

        mSettingsTvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCommonOpenEtDialog(mActivity,
                        mSettingsTvWeight.getText().toString(),
                        getString(R.string.string_settings_subtitle) + mSettingsTvWeight.getText().toString().toLowerCase(),
                        getString(R.string.string_button_ok),
                        getString(R.string.string_button_cancel),
                        mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_weight), null),
                        getString(R.string.string_settings_number_decimal),
                        mEditor,
                        getString(R.string.string_shared_preferences_user_weight));
            }
        });

        mSettingsTvMaximunSugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCommonOpenEtDialog(mActivity,
                        mSettingsTvMaximunSugar.getText().toString(),
                        getString(R.string.string_settings_subtitle) + mSettingsTvMaximunSugar.getText().toString().toLowerCase(),
                        getString(R.string.string_button_ok),
                        getString(R.string.string_button_cancel),
                        mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_sugar_per_day), null),
                        getString(R.string.string_settings_number_decimal),
                        mEditor,
                        getString(R.string.string_shared_preferences_user_sugar_per_day));
            }
        });
    }
}
