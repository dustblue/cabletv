package com.rakesh.cabletv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence mTitles[];
    private int mNumbOfTabs;
    private String mCluster;

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, String cluster) {
        super(fm);
        this.mTitles = mTitles;
        this.mNumbOfTabs = mNumbOfTabsumb;
        this.mCluster = cluster;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("cluster", mCluster);
        if (position == 0) {
            AllTab allTab = new AllTab();
            allTab.setArguments(bundle);
            return allTab;
        } else {
            UnpaidTab unpaidTab = new UnpaidTab();
            unpaidTab.setArguments(bundle);
            return unpaidTab;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }
}