package com.ghp55.eli.ghp;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by elijn on 7/1/2018.
 */

public class CustomViewPager extends ViewPager {

    static public boolean enabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomViewPager.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (CustomViewPager.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (CustomViewPager.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public static void setPagingEnabled(boolean enabled) {
        CustomViewPager.enabled = enabled;
    }
}