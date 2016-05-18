package com.ypunval.pcbang.util;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by baek_uncheon on 2016. 5. 17..
 */
public class CustomBottomSheetBehavior extends BottomSheetBehavior {
    private static final String TAG = "CustomBottomSheetBehavior";
    boolean canScroll = true;

    public CustomBottomSheetBehavior() {
    }

    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
        Log.i(TAG, "onSaveInstanceState: ");
        return super.onSaveInstanceState(parent, child);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
        Log.i(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(parent, child, state);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
        return super.onInterceptTouchEvent(parent, child, event);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        return false;
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (canScroll)
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    public void setCanScroll(boolean canScroll){
        this.canScroll = canScroll;
    }
}