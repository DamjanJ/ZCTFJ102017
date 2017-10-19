package com.funny.bjokes.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by CJH on 2016.01.14.
 */
public class HackyViewPager extends ViewPager {
    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
        /*try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException ex2) {
            ex2.printStackTrace();
            return false;
        }
        */
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
