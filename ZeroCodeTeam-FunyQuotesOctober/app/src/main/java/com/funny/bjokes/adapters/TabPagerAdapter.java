package com.funny.bjokes.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.funny.bjokes.Constants;
import com.funny.bjokes.components.TabFrg;

/**
 * Created by CJH on 2016.01.14.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    public TabPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        TabFrg fragment = TabFrg.newInstance(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return Constants.TAB_COUNT;
    }

    @Override
    public int getItemPosition(Object object) {
        return -2;
    }
}
