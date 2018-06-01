package com.dtapps.sugarcop;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.dtapps.sugarcop.common.CommonData;

import java.util.ArrayList;
import java.util.List;

import static com.dtapps.sugarcop.common.Common.commonCreateFoodObjectCursor;
import static com.dtapps.sugarcop.common.Common.commonGetColor;
import static com.dtapps.sugarcop.common.Common.commonInitActivity;
import static com.dtapps.sugarcop.common.Common.commonOnClickDetailsList;
import static com.dtapps.sugarcop.common.Common.commonOnQuerySearch;

public class AllProductsActivity extends BaseActivity {
    private Activity mActivityHelper;
    private ContentResolver mResolver;
    private Cursor mCursor;
    private FoodAllAdapter mAdapter, mAdapterSearch;
    private List<Food> mFoodList = new ArrayList<>();
    private List<Food> mFoodListSearch = new ArrayList<>();
    private RecyclerView mRvAllProducts, mRvAllProductsSearch;
    private int mLastValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        Toolbar toolbar = findViewById(R.id.all_products_toolbar);
        toolbar.inflateMenu(R.menu.main);
        setSupportActionBar(toolbar);

        commonInitActivity(this,
                R.id.adView_all,
                CommonData.getAllColor());

        mActivityHelper = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mResolver = getContentResolver();
        mRvAllProducts = findViewById(R.id.all_products_rv);
        mRvAllProductsSearch = findViewById(R.id.all_products_rv_search);

        FastScroller fastScroller = findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(mRvAllProducts);
        fastScroller.setHandleColor(commonGetColor(this, R.color.colorAccentDark));

        mAdapterSearch = new FoodAllAdapter(mFoodListSearch);
        mRvAllProductsSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRvAllProductsSearch.setItemAnimator(new DefaultItemAnimator());
        mRvAllProductsSearch.setAdapter(mAdapterSearch);
        mRvAllProductsSearch.setVisibility(View.GONE);

        mAdapter = new FoodAllAdapter(mFoodList);
        mRvAllProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRvAllProducts.setItemAnimator(new DefaultItemAnimator());
        mRvAllProducts.setAdapter(mAdapter);

        allProductSelectData(mAdapter, mFoodList);

        mRvAllProducts.addOnItemTouchListener(new
                RecyclerItemClickListener(this, mRvAllProducts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onClickItem(View v, int position) {
                TextView tvN = v.findViewById(R.id.main_recycler_item_text_view_name);
                Intent detailsIntent = new Intent(AllProductsActivity.this, DetailsActivity.class);
                commonOnClickDetailsList(AllProductsActivity.this, tvN.getHint().toString(),
                        mResolver, detailsIntent);
            }

            @Override
            public void onLongClickItem(final View v, int position) {}
        }));

        mRvAllProductsSearch.addOnItemTouchListener(new
                RecyclerItemClickListener(this, mRvAllProductsSearch, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onClickItem(View v, int position) {
                TextView tvN = v.findViewById(R.id.main_recycler_item_text_view_name);
                Intent detailsIntent = new Intent(AllProductsActivity.this, DetailsActivity.class);
                commonOnClickDetailsList(AllProductsActivity.this, tvN.getHint().toString(),
                        mResolver, detailsIntent);
            }

            @Override
            public void onLongClickItem(final View v, int position) {}
        }));

        mRvAllProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    allProductSelectData(mAdapter, mFoodList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        recreate();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final String sortOrder = FoodContract.FoodEntry.Columns.FOOD_BRAND + " ASC";

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Seaching: ", "onQueryTextSubmit ");

                commonOnQuerySearch(mActivityHelper, mResolver, mFoodListSearch, s, mAdapterSearch, sortOrder);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("Searching: ", "onQueryTextChange ");

                commonOnQuerySearch(mActivityHelper, mResolver, mFoodListSearch, s, mAdapterSearch, sortOrder);

                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.all_products_toolbar),
                        new AutoTransition().setDuration(150));

                mRvAllProducts.setVisibility(View.GONE);
                mRvAllProductsSearch.setVisibility(View.VISIBLE);
                mFoodListSearch.clear();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.all_products_toolbar),
                        new AutoTransition().setDuration(150));

                mRvAllProductsSearch.setVisibility(View.GONE);
                mRvAllProducts.setVisibility(View.VISIBLE);
                //mAdapter.notifyDataSetChanged();

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                MenuItemCompat.expandActionView(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void allProductSelectData(RecyclerView.Adapter adapter, List<Food> list){
        Food foodRecovered;
        final String sortOrder = FoodContract.FoodEntry.Columns.FOOD_BRAND + " ASC LIMIT 500";
        final String selection = FoodContract.FoodEntry.Columns.FOOD_ID + " > ?";
        final String[] selectionArgs = {String.valueOf(mLastValue)};

        mCursor = mResolver.query(FoodContract.CONTENT_URI, null, selection, selectionArgs, sortOrder);
        //list.clear();

        if (mCursor.moveToFirst()){
            do {
                foodRecovered = commonCreateFoodObjectCursor(mCursor);
                list.add(foodRecovered);
                mLastValue = Integer.valueOf(foodRecovered.getmId());
            } while (mCursor.moveToNext());
        }

        mCursor.close();
        adapter.notifyDataSetChanged();
    }

}
