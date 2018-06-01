package com.dtapps.sugarcop;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.List;

/**
 * Created by jon.durao on 12/4/2017.
 */

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.MyViewHolder> implements SectionTitleProvider{

    private List<Food> mFoodList;

    @Override
    public String getSectionTitle(int position) {
        return String.valueOf(mFoodList.get(position).getmName().charAt(0));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTvName, mTvBrand;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.main_search_result_item_name);
            mTvBrand = (TextView) itemView.findViewById(R.id.main_search_result_item_brand);
        }
    }

    public FoodSearchAdapter(List<Food> foodList){
        this.mFoodList = foodList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Food food = mFoodList.get(position);
        holder.mTvName.setText(food.getmName());
        holder.mTvName.setHint(food.getmId());
        holder.mTvBrand.setText(food.getmBrand());
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_main_search_results_item, parent, false);
        return new MyViewHolder(v);
    }
}
