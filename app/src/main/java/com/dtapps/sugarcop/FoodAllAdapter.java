package com.dtapps.sugarcop;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jon.durao on 10/5/2017.
 */

public class FoodAllAdapter extends RecyclerView.Adapter<FoodAllAdapter.MyViewHolder> {

    private List<Food> list;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTvName, mTvBrand, mTvSugar;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.main_recycler_item_text_view_name);
            mTvBrand = itemView.findViewById(R.id.main_recycler_item_text_view_brand);
            mTvSugar = itemView.findViewById(R.id.main_recycler_item_text_view_sugar);
        }
    }

    public FoodAllAdapter(List<Food> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_all_products_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Food food = list.get(position);

        holder.mTvName.setText(food.getmName());
        holder.mTvName.setHint(food.getmId());
        holder.mTvBrand.setText(food.getmBrand());
        holder.mTvSugar.setText(Float.toString(food.getmSugarPerPortion()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}