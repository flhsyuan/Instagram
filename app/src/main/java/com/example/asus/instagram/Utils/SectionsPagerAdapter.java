package com.example.asus.instagram.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store fragment tags: CameraFragment...
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter{

    private static final String TAG = "SectionsPagerAdapter";

    private final List<Fragment> myFragementList = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return myFragementList.get(i);
    }


    @Override
    public int getCount() {
        return myFragementList.size();
    }

    public void addFragment(Fragment f ){
        myFragementList.add(f);
    }
}
