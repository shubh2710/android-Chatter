package com.shubh.androidchatter.extra_classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by root on 17/6/17.
 */

public  class RecyclerTouchListner implements RecyclerView.OnItemTouchListener{

    private GestureDetector gestureDetector;
    private ClickListener clicklistner;
    public RecyclerTouchListner(Context context, final RecyclerView recyclerview, final ClickListener clicklistner){
        this.clicklistner=clicklistner;
        gestureDetector =new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView= recyclerview.findChildViewUnder(e.getX(),e.getY());
                if(childView!=null && clicklistner!=null){
                    clicklistner.onLongClick(childView,recyclerview.getChildPosition(childView));
                }
                super.onLongPress(e);
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView= rv.findChildViewUnder(e.getX(),e.getY());
        if(childView!=null && clicklistner!=null && gestureDetector.onTouchEvent(e)){
            clicklistner.onClick(childView,rv.getChildPosition(childView));

        }
        return false;
    }
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int postion);
    }
}
