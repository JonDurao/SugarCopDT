package com.dtapps.sugarcop;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.dtapps.sugarcop.common.Common;
import com.dtapps.sugarcop.common.CommonData;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

import static com.dtapps.sugarcop.FoodContract.FoodEntry.*;
import static com.dtapps.sugarcop.SugarHistoryContract.*;
import static com.dtapps.sugarcop.common.UICommon.setAnimations;
import static com.dtapps.sugarcop.common.UICommon.setAnimationsSearch;
import static com.dtapps.sugarcop.common.Common.*;
import static com.dtapps.sugarcop.common.UICommon.uiCommonOpenAboutDialog;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MaterialIntroListener {
    private static final String MENU_MAIN_FAB_ID_TAG = "menuMainFabIdTag";
    private static final String MENU_MENU_ID_TAG = "menuMenuIdTag";
    private static final String MENU_SEARCH_ID_TAG = "menuSearchIdTag";

    private BigDecimal sugarBd = BigDecimal.ZERO;
    private BigDecimal maxSugarBd = BigDecimal.ONE;
    private List<Food> mFoodList = new ArrayList<>();
    private List<Food> mFoodListSearch = new ArrayList<>();
    private RecyclerView mRecyclerViewRecent, mRecyclerViewSearch;
    private FoodAdapter mAdapter;
    private FoodSearchAdapter mSearchAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Activity mActivityHelper;
    private Calendar mCalendar;
    private SimpleDateFormat mDateFormat;
    private Cursor mCursor;
    private ContentResolver mResolver;
    private ContentValues mValues = new ContentValues();
    private DecoView mDecoView;
    private int SERIES_ID;
    private FloatingActionButton mFabInsert;
    private RelativeLayout mLlEmptyList;
    private LinearLayout mLlContent;
    private TextView mTvRingTitle, mTvRingSubtitle;
    public static HashMap<String, String> OLD_NEW_IDS = new HashMap<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main);
        setSupportActionBar(toolbar);

        mActivityHelper = this;
        mResolver = getContentResolver();
        mCalendar = Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.string_shared_preferences_name), 0);
        mEditor = mSharedPreferences.edit();

        if (!mSharedPreferences.getBoolean(getString(R.string.string_key_first_run), false)){
            Intent introUserCreation = new Intent(this, IntroActivity.class);
            this.startActivityForResult(introUserCreation,102);
        }

        if (mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_date), null) != null){
            mainCheckDate(this);
        }

        commonInitActivity(this, R.id.adView, CommonData.getMainColor());

        mainCreateElements(this);
        mainPrepareUi();

        mFabInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(mActivityHelper, AddEditActivity.class);
                addIntent.putExtra(CommonData.getExtraCaller(), mActivityHelper.getLocalClassName());
                startActivity(addIntent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView textNavigation = header.findViewById(R.id.main_text_view_title);
        textNavigation.setText(mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_username), null));

        mAdapter = new FoodAdapter(mFoodList);
        mSearchAdapter = new FoodSearchAdapter(mFoodListSearch);

        mainCheckModifiedIds(OLD_NEW_IDS);

        mainSelectData(mAdapter, mFoodList,
                mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_added),
                        null));

        setAnimations(mActivityHelper, mRecyclerViewRecent, new LinearLayoutManager(getApplicationContext()), mAdapter);
        setAnimationsSearch(mActivityHelper, mRecyclerViewSearch, new LinearLayoutManager(getApplicationContext()), mSearchAdapter);

        if (mRecyclerViewRecent.getAdapter().getItemCount() == 0){
            mLlEmptyList.setVisibility(View.VISIBLE);
        } else {
            mLlEmptyList.setVisibility(View.GONE);
        }

        mRecyclerViewRecent.addOnItemTouchListener(new
                RecyclerItemClickListener(this, mRecyclerViewRecent, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onClickItem(View v, int position) {
                TextView tvN = v.findViewById(R.id.main_recycler_item_text_view_name);
                Intent detailsIntent = new Intent(mActivityHelper, DetailsActivity.class);
                commonOnClickDetailsList(mActivityHelper, tvN.getHint().toString(), mResolver, detailsIntent);
            }

            @Override
            public void onLongClickItem(final View v, int position) {
                commonLongTapDelete(mActivityHelper, mResolver, mSharedPreferences, mEditor,
                        v, mRecyclerViewRecent, mAdapter, mFoodList, position, mDecoView, SERIES_ID, mTvRingTitle, mTvRingSubtitle);
            }
        }));

        mRecyclerViewSearch.addOnItemTouchListener(new
                RecyclerItemClickListener(this, mRecyclerViewSearch, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onClickItem(View v, int position) {
                TextView tvN = v.findViewById(R.id.main_search_result_item_name);
                Intent detailsIntent = new Intent(mActivityHelper, DetailsActivity.class);
                commonOnClickDetailsList(mActivityHelper, tvN.getHint().toString(), mResolver, detailsIntent);
            }

            @Override
            public void onLongClickItem(View v, int position) {}
        }));

        if (!mSharedPreferences.getBoolean(getString(R.string.string_key_first_run_main_screen), false)){
            showIntro(mActivityHelper.findViewById(R.id.action_search), MENU_SEARCH_ID_TAG,
                    getString(R.string.string_tutorial_message_main_search), FocusGravity.CENTER,
                    Focus.MINIMUM, ShapeType.CIRCLE);
        }
    }

    /**
     * Method that handles display of intro screens
     *
     * @param view         View to show guide
     * @param id           Unique ID
     * @param text         Display message
     * @param focusGravity Focus Gravity of the display
     */
    public void showIntro(View view, String id, String text, FocusGravity focusGravity, Focus focus, ShapeType shapeType) {
        new MaterialIntroView.Builder(mActivityHelper)
                .setFocusGravity(focusGravity)
                .setFocusType(focus)
                .setDelayMillis(100)
                .enableFadeAnimation(true)
                .dismissOnTouch(true)
                .setInfoText(text)
                .setShape(shapeType)
                .setTextColor(R.color.colorTextDark)
                .enableIcon(false)
                .enableDotAnimation(false)
                .setTarget(view)
                .setListener(this)
                .setUsageId(id)
                .show();
    }

    private void mainCheckModifiedIds(HashMap map) {
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            HashMap.Entry<String, String> pair = (Map.Entry)it.next();

            commonUpdateToPreferencesList(mSharedPreferences,
                    mEditor,
                    mActivityHelper.getString(R.string.string_shared_preferences_user_added),
                    pair.getValue(), pair.getKey());

            commonUpdateToPreferencesList(mSharedPreferences,
                    mEditor,
                    mActivityHelper.getString(R.string.string_shared_preferences_user_recent),
                    pair.getValue(), pair.getKey());

            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    private void mainPrepareUi (){
        sugarBd = sugarBd.setScale(1, BigDecimal.ROUND_HALF_EVEN);
        maxSugarBd = maxSugarBd.setScale(1, BigDecimal.ROUND_HALF_EVEN);

        int levels = sugarBd.divide(maxSugarBd, BigDecimal.ROUND_DOWN).multiply(BigDecimal.TEN).intValue();

        if (levels > 10)
            levels = 10;

        Common.mainPrepareSugarText(mActivityHelper, levels, sugarBd, maxSugarBd, mTvRingTitle, mTvRingSubtitle);

        //Background Track
        mDecoView.addSeries(new SeriesItem.Builder(commonGetColor(mActivityHelper, R.color.colorDividerLight))
                .setRange(0, maxSugarBd.floatValue(), maxSugarBd.floatValue())
                .setInitialVisibility(true)
                .build());

            SeriesItem seriesItem = new SeriesItem.Builder(commonGetColor(mActivityHelper, R.color.colorAccent))
                    .setInitialVisibility(false)
                    .setRange(0, maxSugarBd.floatValue(), 0f)
                    .build();

            SERIES_ID = mDecoView.addSeries(seriesItem);

            mDecoView.addEvent(new DecoEvent.Builder(sugarBd.floatValue())
                    .setIndex(SERIES_ID)
                    .build());
    }

    @Override
    protected void onResume() {super.onResume();}

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void mainSelectData(RecyclerView.Adapter adapter, List<Food> list, String recentIds){
        Food foodRecovered;
        List<String> parameters = new ArrayList<>();

        String selectionId;
        String[] selectionIdArgs;
        String[] selectionIdArgsAux = {"1"};
        List<String> recentList;

        if (recentIds != null){
            selectionIdArgs = commonEliminateBrackets(recentIds).split(",");
            recentList = new ArrayList<>();
            list.clear();

            for (String auxRecent : selectionIdArgs){
                recentList.add(auxRecent);
                selectionIdArgsAux[0] = auxRecent;
                parameters.add("?");

                selectionId = Columns.FOOD_ID + " in (?)";
                mCursor = mResolver.query(FoodContract.CONTENT_URI, null, selectionId, selectionIdArgsAux, null);

                if (mCursor.moveToFirst()){
                    do {
                        String id = mCursor.getString(mCursor.getColumnIndex(Columns.FOOD_ID));
                        int occurrences = Collections.frequency(recentList, id);

                        foodRecovered = new Food();
                        foodRecovered.setmId(id);
                        if (occurrences == 1){
                            foodRecovered.setmName(mCursor.getString(mCursor.getColumnIndex(Columns.FOOD_NAME)));
                        } else {
                            foodRecovered.setmName(mCursor.getString(mCursor.getColumnIndex(Columns.FOOD_NAME))
                                    + " ("
                                    + commonCreateCardinal(this, occurrences)
                                    + getString(R.string.string_main_number_times));
                        }
                        foodRecovered.setmBrand(mCursor.getString(mCursor.getColumnIndex(Columns.FOOD_BRAND)));
                        foodRecovered.setmSugarPerHundred(mCursor.getFloat(mCursor.getColumnIndex(Columns.FOOD_SUGAR_HUNDRED)));
                        foodRecovered.setmSugarPerPortion(mCursor.getFloat(mCursor.getColumnIndex(Columns.FOOD_SUGAR_PORTION)));
                        foodRecovered.setmQuantity(mCursor.getFloat(mCursor.getColumnIndex(Columns.FOOD_QUANTITY)));
                        list.add(foodRecovered);
                    } while (mCursor.moveToNext());

                    mCursor.close();
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(750);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final String sortOrder = "";

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Seaching: ", "onQueryTextSubmit ");

                commonOnQuerySearch(mActivityHelper, mResolver, mFoodListSearch, s, mSearchAdapter, sortOrder);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("Searching: ", "onQueryTextChange ");

                commonOnQuerySearch(mActivityHelper, mResolver, mFoodListSearch, s, mSearchAdapter, sortOrder);

                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.main_toolbar), new AutoTransition().setDuration(150));

                mLlContent.setVisibility(View.GONE);
                mFabInsert.setVisibility(View.GONE);
                mRecyclerViewSearch.setVisibility(View.VISIBLE);

                mFoodListSearch.clear();
                mainSelectData(mSearchAdapter, mFoodListSearch,
                        mSharedPreferences.getString(getResources().getString(R.string.string_shared_preferences_user_recent),
                                null));
                mSearchAdapter.notifyDataSetChanged();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.main_toolbar),
                        new AutoTransition().setDuration(150));

                mFoodListSearch.clear();
                mSearchAdapter.notifyDataSetChanged();
                mLlContent.setVisibility(View.VISIBLE);
                mFabInsert.setVisibility(View.VISIBLE);
                mRecyclerViewSearch.setVisibility(View.GONE);

                return true;
            }
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {super.onSaveInstanceState(outState);}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                MenuItemCompat.expandActionView(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_about:
                uiCommonOpenAboutDialog(mActivityHelper, getString(R.string.string_main_about), "");
                break;
            case R.id.nav_all_products:
                Intent allIntent = new Intent(mActivityHelper, AllProductsActivity.class);
                allIntent.putExtra(CommonData.getExtraCaller(), mActivityHelper.getLocalClassName());
                startActivity(allIntent);
                break;
            case R.id.nav_history:
                Intent historyIntent = new Intent(mActivityHelper, HistoryActivity.class);
                historyIntent.putExtra(CommonData.getExtraCaller(), mActivityHelper.getLocalClassName());
                startActivity(historyIntent);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(mActivityHelper, SettingsActivity.class);
                settingsIntent.putExtra(CommonData.getExtraCaller(), mActivityHelper.getLocalClassName());
                startActivity(settingsIntent);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void mainCreateElements(Activity activity){
        mDecoView = findViewById(R.id.main_dav_ring);
        mFabInsert = activity.findViewById(R.id.main_fab);
        mLlContent = activity.findViewById(R.id.main_content_linear_layout);
        mLlEmptyList = activity.findViewById(R.id.main_ll_empty_list_view);
        mTvRingTitle = activity.findViewById(R.id.main_tv_ring_title);
        mTvRingSubtitle = activity.findViewById(R.id.main_tv_ring_subtitle);
        mRecyclerViewRecent = activity.findViewById(R.id.main_recycler_view_recent);
        mRecyclerViewSearch = activity.findViewById(R.id.main_recycler_view_search_results);
    }

    private void mainCheckDate(Activity activity) {
        Calendar auxCalendar = Calendar.getInstance();
        String dateSaved = mSharedPreferences.getString(activity.getString(R.string.string_shared_preferences_user_date), null);
        String dateCurrent = mDateFormat.format(mCalendar.getTime());

        try {
            mCalendar.setTime(mDateFormat.parse(dateSaved));
            auxCalendar.setTime(mDateFormat.parse(dateCurrent));
        } catch (ParseException e) {
        }


        if (!dateSaved.equalsIgnoreCase(dateCurrent)) {

            mValues.put(SugarHistoryEntry.Columns.SUGAR_SUGAR,
                    mSharedPreferences.getString(activity.getString(R.string.string_shared_preferences_user_sugar),
                            null));
            mValues.put(SugarHistoryEntry.Columns.SUGAR_DATE, dateSaved);
            mResolver.insert(SugarHistoryContract.CONTENT_URI, mValues);

            mCalendar.add(Calendar.DATE, 1);

            while (!mDateFormat.format(mCalendar.getTime()).equalsIgnoreCase(dateCurrent)) {
                mValues.clear();

                mValues.put(SugarHistoryEntry.Columns.SUGAR_SUGAR, "0.0");
                mValues.put(SugarHistoryEntry.Columns.SUGAR_DATE, mDateFormat.format(mCalendar.getTime()));
                mResolver.insert(SugarHistoryContract.CONTENT_URI, mValues);

                mCalendar.add(Calendar.DATE, 1);
            }

            mEditor.putString(activity.getString(R.string.string_shared_preferences_user_date), dateCurrent);
            mEditor.putString(activity.getString(R.string.string_shared_preferences_user_sugar), "0.0");
            mEditor.commit();
            mEditor.putString(activity.getString(R.string.string_shared_preferences_user_added), "");
            mEditor.commit();

            mValues.clear();

            if (mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_sugar), null) != null){
                try{
                    maxSugarBd = BigDecimal.valueOf(Float.valueOf(mSharedPreferences.getString(getResources().getString(R.string.string_shared_preferences_user_sugar_per_day), null)));
                } catch (Exception e){
                    Toast.makeText(activity, "¡Error mainCheckDate!", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_sugar), null) != null){
                try {
                    sugarBd = BigDecimal.valueOf(Float.valueOf(mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_sugar), null)));
                    maxSugarBd = BigDecimal.valueOf(Float.valueOf(mSharedPreferences.getString(getResources().getString(R.string.string_shared_preferences_user_sugar_per_day), null)));
                } catch (Exception e){
                    Toast.makeText(activity, "¡Error mainCheckDate!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {
        switch (materialIntroViewId) {
            case MENU_SEARCH_ID_TAG:
                showIntro(mActivityHelper.findViewById(R.id.main_fab), MENU_MAIN_FAB_ID_TAG,
                        getString(R.string.string_tutorial_message_main_fab), FocusGravity.CENTER,
                        Focus.MINIMUM, ShapeType.CIRCLE);
                break;
            case MENU_MAIN_FAB_ID_TAG:
                showIntro(mActivityHelper.findViewById(R.id.main_toolbar), MENU_MENU_ID_TAG,
                        getString(R.string.string_tutorial_message_main_menu), FocusGravity.LEFT,
                        Focus.NORMAL, ShapeType.RECTANGLE);
                mEditor.putBoolean(mActivityHelper.getResources().getString(R.string.string_key_first_run_main_screen), true);
                break;
            default:
                break;
        }
    }
}
