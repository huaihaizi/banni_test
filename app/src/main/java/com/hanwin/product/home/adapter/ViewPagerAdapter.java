package com.hanwin.product.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by zhaopf on 2018/1/3.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Object> items;
    public ViewPagerAdapter(FragmentManager fm, ArrayList<Object> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return (Fragment) items.get(position);
            case 1:
                return (Fragment) items.get(position);
            case 2:
                return (Fragment) items.get(position);
        }
        return (Fragment) items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
