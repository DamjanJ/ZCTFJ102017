package com.funny.bjokes.listeners;

import android.support.v4.view.ViewPager;

import com.funny.bjokes.DetailActivity;

/**
 * Created by CJH on 2016.01.14.
 */
public class PageChangeListener implements ViewPager.OnPageChangeListener {

    DetailActivity mInstance;
    boolean isLoading = false;
    int oldCount = 0;

    public PageChangeListener(DetailActivity activity) {
        mInstance = activity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mInstance.mPosition = position;
        mInstance.showRating(position);

        int count = mInstance.mAdapter.getCount();
        if (position > count - 5 && count > 20 && !isLoading) {
            mInstance.mPage++;
            isLoading = true;
            mInstance.loadMoreData(mInstance.mPage);
            oldCount = count;
        } else if (count > oldCount && isLoading) {
            isLoading = false;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
