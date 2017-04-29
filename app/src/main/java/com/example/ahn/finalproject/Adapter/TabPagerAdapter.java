package com.example.ahn.finalproject.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ahn.finalproject.CapsuleGroup.CapsuleGroupMainFragment;
import com.example.ahn.finalproject.CapsulePrivate.CapsulePrivateMainFragment;
import com.example.ahn.finalproject.CapsuleShow.CapsuleShowMainFragment;
import com.example.ahn.finalproject.Option.SetOption;

/**
 * Created by Ahn on 2017-04-16.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;
    private String userId;

    public TabPagerAdapter(FragmentManager fm, int tabCount, String userId) {
        super(fm);
        this.tabCount = tabCount;
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                CapsulePrivateMainFragment tabFragment1 = new CapsulePrivateMainFragment();
                Bundle args = new Bundle();
                args.putString(userId, userId);
                tabFragment1.setArguments(args);
                return tabFragment1;
            case 1:
                CapsuleGroupMainFragment tabFragment2 = new CapsuleGroupMainFragment();
                return tabFragment2;
            case 2:
                CapsuleShowMainFragment tabFragment3 = new CapsuleShowMainFragment();
                return tabFragment3;
            case 3:
                SetOption tabFragment4 = new SetOption();
                return tabFragment4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}