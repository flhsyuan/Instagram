package com.example.asus.instagram.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentPagerAdapter{

    // store all the fragments
    private final List<Fragment> myFragment = new ArrayList<>();

    //The following three hashmaps are used to retrieval info of fragments using their attributes
    private final HashMap<Fragment,Integer> fragments = new HashMap<>();
    private final HashMap<String,Integer> fragmentNumbers = new HashMap<>();
    private final HashMap<Integer,String> fragmentNames = new HashMap<>();


    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Add new fragment to the fragment list and update related dataset.
     * @param f
     * @param name
     */
    public void addFragment(Fragment f,String name){
        myFragment.add(f);
        fragments.put(f,myFragment.size()-1); //give it a new fragment number
        fragmentNumbers.put(name,myFragment.size()-1);
        fragmentNames.put(myFragment.size()-1 ,name);
    }

    @Override
    public int getCount() {
        return myFragment.size();
    }


    @Override
    public Fragment getItem(int i) {
        return myFragment.get(i);
    }

    /**
     * Retrireval fragment ID using fragment name
     * @param name
     * @return
     */
    public Integer getFragmentID(String name){
        if (fragmentNumbers.containsKey(name)) {
            return fragmentNumbers.get(name);
        }else {
            return null;
        }
    }

    /**
     * Retrireval fragment ID using fragment name
     * @param fragment
     * @return
     */
    public Integer getFragmentID(Fragment fragment){
        if (fragments.containsKey(fragment)) {
            return fragments.get(fragment);
        }else {
            return null;
        }
    }

    /**
     * Retrireval fragment ID using fragment name
     * @param fragment id
     * @return
     */
    public String getFragmentName(Integer ID){
        if (fragmentNames.containsKey(ID)) {
            return fragmentNames.get(ID);
        }else {
            return null;
        }

    }
}
