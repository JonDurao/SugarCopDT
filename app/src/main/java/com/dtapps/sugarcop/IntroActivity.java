package com.dtapps.sugarcop;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.dtapps.sugarcop.common.Common.commonGetColor;
import static com.dtapps.sugarcop.common.Common.commonInitEtChangeTextListenerDecimal;
import static com.dtapps.sugarcop.common.UICommon.*;

public class IntroActivity extends BaseActivity {
    private static Activity mActivityHelper;
    private static Button mButtonNext;
    private static Calendar mCalendar;
    private static int[] mColorsActive, mColorsInactive,
            mColorsPages = {R.color.bg_slider_screen1, R.color.bg_slider_screen2, R.color.bg_slider_screen3},
            mLayoutPages;
    private static LinearLayout mLlDots;
    private static MyViewPagerAdapter mViewPagerAdapter;
    private static RadioGroup mRgGenre;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SimpleDateFormat mDateFormat;
    private static SwitchCompat mSwitchCompat;
    private static TextInputLayout mTilName, mTilHeight, mTilInches, mTilWeight, mTilSurname,
                                    mTilUsername, mTilAge;
    private static TextView[] mTvDots;
    private static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mActivityHelper = this;
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.string_shared_preferences_name), 0);
        mEditor = mSharedPreferences.edit();
        mCalendar = Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        introAddAllElements(this);

        introAddBottomDots(this, 0);
        introChangeBarsColor(this, 0);

        getWindow().setNavigationBarColor(commonGetColor(mActivityHelper, mColorsPages[0]));
        getWindow().setStatusBarColor(commonGetColor(mActivityHelper, mColorsPages[0]));

        mViewPagerAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    protected static void introAddAllElements(Activity activity){
        mLayoutPages = new int[]{R.layout.intro_slide_screen_one,
                                    R.layout.intro_slide_screen_two,
                                    R.layout.intro_slide_screen_three};

        mButtonNext = activity.findViewById(R.id.intro_finish_button);
        mLlDots = activity.findViewById(R.id.intro_linear_layout_dots);
        mRgGenre = activity.findViewById(R.id.intro_rg_slide_three_genre);
        mSwitchCompat = activity.findViewById(R.id.intro_sw_slide_three_units);
        mTilHeight = activity.findViewById(R.id.intro_til_slide_three_height);
        mTilInches = activity.findViewById(R.id.intro_til_slide_three_height_inches);
        mTilWeight = activity.findViewById(R.id.intro_til_slide_three_weight);
        mTilName = activity.findViewById(R.id.intro_til_slide_two_name);
        mTilSurname = activity.findViewById(R.id.intro_til_slide_two_surname);
        mTilUsername = activity.findViewById(R.id.intro_til_slide_two__username);
        mTilAge = activity.findViewById(R.id.intro_til_slide_two__age);
        mViewPager = activity.findViewById(R.id.intro_view_pager);

        mTvDots = new TextView[mLayoutPages.length];

        mColorsActive = activity.getResources().getIntArray(R.array.array_dot_active);
        mColorsInactive = activity.getResources().getIntArray(R.array.array_dot_inactive);
    }

    protected static void introAddBottomDots(Activity activity, int currentPage){
        mLlDots.removeAllViews();

        for (int i=0; i < mTvDots.length; i++){
            mTvDots[i] = new TextView(activity);

            if (Build.VERSION.SDK_INT > 23){
                mTvDots[i].setText(Html.fromHtml("&#8226;", 0));
            } else {
                mTvDots[i].setText(Html.fromHtml("&#8226;"));
            }

            mTvDots[i].setTextColor(commonGetColor(mActivityHelper, R.color.dot_inactive_screen1));
            mTvDots[i].setTextSize(35);
            mLlDots.addView(mTvDots[i]);
        }

        if (mTvDots.length > 0){
            mTvDots[currentPage].setTextColor(commonGetColor(mActivityHelper, R.color.dot_active_screen1));
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            introAddAllElements(mActivityHelper);

            if (position == mLayoutPages.length -1){
                mButtonNext.setVisibility(View.VISIBLE);

                mButtonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        introNextListener(mActivityHelper);
                    }
                });

                mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    introModifyBySwitch(mActivityHelper);
                    }
                });

                commonInitEtChangeTextListenerDecimal(mTilAge.getEditText(), 0,2);
                commonInitEtChangeTextListenerDecimal(mTilHeight.getEditText(), 0,3);
                commonInitEtChangeTextListenerDecimal(mTilWeight.getEditText(), 2,3);
            } else {
                mButtonNext.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageSelected(int position) {
            introAddBottomDots(mActivityHelper, position);
            introChangeBarsColor(mActivityHelper, position);

            getWindow().setNavigationBarColor(commonGetColor(mActivityHelper, mColorsPages[position]));
            getWindow().setStatusBarColor(commonGetColor(mActivityHelper, mColorsPages[position]));
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    protected static void introNextListener(Activity activity){
        if (mTilName.getEditText().getText().toString().isEmpty()
                || mTilUsername.getEditText().getText().toString().isEmpty()
                || mTilSurname.getEditText().getText().toString().isEmpty()
                || mTilAge.getEditText().getText().toString().isEmpty()){

            if (mTilName.getEditText().getText().toString().isEmpty()){
                setTextFieldError(mTilName, activity.getString(R.string.string_til_error_name));
            }
            if (mTilSurname.getEditText().getText().toString().isEmpty()){
                setTextFieldError(mTilSurname, activity.getString(R.string.string_til_error_surname));
            }
            if (mTilUsername.getEditText().getText().toString().isEmpty()){
                setTextFieldError(mTilUsername, activity.getString(R.string.string_til_error_username));
            }
            if (mTilAge.getEditText().getText().toString().isEmpty()){
                setTextFieldError(mTilAge, activity.getString(R.string.string_til_error_age));
            }
            mViewPager.setCurrentItem(1);
        } else {
            int age = Integer.parseInt(mTilAge.getEditText().getText().toString());
            String  height,
                    genre = activity.findViewById(mRgGenre.getCheckedRadioButtonId()).getContentDescription().toString(),
                    unit = mSwitchCompat.getText().toString(),
                    weight = mTilWeight.getEditText().getText().toString();;

            if (unit.equalsIgnoreCase(activity.getString(R.string.string_intro_switch_unit_me))){
                height = mTilHeight.getEditText().getText().toString();
            } else {
                height = mTilHeight.getEditText().getText().toString() + "' " +
                            mTilInches.getEditText().getText().toString() + "''";
            }

            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_name),
                                mTilName.getEditText().getText().toString());
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_surname),
                                mTilSurname.getEditText().getText().toString());
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_username),
                                mTilUsername.getEditText().getText().toString());
            mEditor.putInt(activity.getResources().getString(R.string.string_shared_preferences_user_age), age);
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_genre), genre);
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_height), height);
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_weight), weight);
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_sugar), "0.0");
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_sugar_per_day),
                                setSugarLimit(activity, genre, height, weight, age, unit));
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_unit), unit);
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_date), mDateFormat.format(mCalendar.getTime()));
            mEditor.putString(activity.getResources().getString(R.string.string_shared_preferences_user_recent), "");
            mEditor.putBoolean(activity.getResources().getString(R.string.string_key_first_run), true);

            mEditor.commit();

            mActivityHelper.finish();
        }
    }

    protected static void introModifyBySwitch (Activity activity) {
        if (mSwitchCompat.isChecked()){
            mSwitchCompat.setText(activity.getString(R.string.string_intro_switch_unit_im));

            mTilHeight.setVisibility(View.VISIBLE);
            mTilInches.setVisibility(View.VISIBLE);

            mTilHeight.setHint(activity.getString(R.string.string_intro_hint_height)
                    + activity.getString(R.string.string_intro_switch_height_ft)
                    + ")");
            mTilInches.setHint(activity.getString(R.string.string_intro_hint_height)
                    + activity.getString(R.string.string_intro_switch_height_in)
                    + ")");
            mTilWeight.setHint(activity.getString(R.string.string_intro_hint_weight)
                    + activity.getString(R.string.string_intro_switch_weight_lb)
                    + ")");

            //mTvHeightText.setText(activity.getString(R.string.string_intro_switch_height_ft));
            //mTvInchesText.setVisibility(View.VISIBLE);
            //mTvWeight.setText(activity.getString(R.string.string_intro_switch_weight_lb));

            InputFilter[] filterH = new InputFilter[1];
            filterH[0] = new InputFilter.LengthFilter(1);
            mTilHeight.getEditText().setFilters(filterH);
        } else {
            mSwitchCompat.setText(activity.getString(R.string.string_intro_switch_unit_me));
            mTilInches.setVisibility(View.GONE);

            mTilHeight.setHint(activity.getString(R.string.string_intro_hint_height)
                    + activity.getString(R.string.string_intro_switch_height_cm)
                    + ")");
            mTilWeight.setHint(activity.getString(R.string.string_intro_hint_weight)
                    + activity.getString(R.string.string_intro_switch_weight_kg)
                    + ")");

            //mTvHeightText.setText(activity.getString(R.string.string_intro_switch_height_cm));
            //mTvWeight.setText(activity.getString(R.string.string_intro_switch_weight_kg));

            InputFilter[] filterH = new InputFilter[1];
            filterH[0] = new InputFilter.LengthFilter(3);
            mTilHeight.getEditText().setFilters(filterH);
        }

        mTilHeight.getEditText().setText("");
        mTilInches.getEditText().setText("");
        mTilWeight.getEditText().setText("");
    }

    private void introChangeBarsColor(Activity activity, int position){
        int[] colorsScreens = activity.getResources().getIntArray(R.array.array_dot_screens);
        mLlDots = activity.findViewById(R.id.intro_linear_layout_dots);

        setColorScreen(mLlDots, colorsScreens[position], activity);
    }

    @Override
    public void onBackPressed() {}

    public class MyViewPagerAdapter extends PagerAdapter{
        LayoutInflater inflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(mLayoutPages[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return mLayoutPages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
