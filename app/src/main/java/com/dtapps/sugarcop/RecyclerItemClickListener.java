package com.dtapps.sugarcop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jon.durao on 10/23/2017.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    private GestureDetector gestureDetector;

    public interface OnItemClickListener{
        public void onClickItem(View v, int position) ;
        public void onLongClickItem(View v, int position);
    }

    public RecyclerItemClickListener(Context context, final RecyclerView recycleView, final OnItemClickListener listener){
        this.mListener = listener;
        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                if(child!=null && mListener!=null){
                    mListener.onLongClickItem(child,recycleView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child=rv.findChildViewUnder(e.getX(),e.getY());
        if(child!=null && mListener!=null && gestureDetector.onTouchEvent(e)){
            mListener.onClickItem(child,rv.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
