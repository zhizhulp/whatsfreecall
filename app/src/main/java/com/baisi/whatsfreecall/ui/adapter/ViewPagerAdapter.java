package com.baisi.whatsfreecall.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by MnyZhao on 2017/11/14.
 * @author Mny
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> mTitleList;
    private List<Fragment> mFragmentList;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, List<String> mTitleList, List<Fragment> mFragmentList) {
        super(fm);
        this.mTitleList = mTitleList;
        this.mFragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
