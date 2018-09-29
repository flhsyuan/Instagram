package com.example.asus.instagram.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity{

    private static final String TAG = "AccountSettingsActivity";

    private Context mcontext;

    private SectionsStatePagerAdapter pagerAdapter;
    private ViewPager myViewPager;
    private RelativeLayout myRelativeLayout;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mcontext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: start to click account settings ");
        myViewPager  = (ViewPager) findViewById(R.id.container);
        myRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        setupSettings();
        setupGoBack();
        setupFragments();
    }

    private void setupSettings(){
        Log.d(TAG, "setupSettings: initializing the list of account settings. ");
        ArrayList<String> settings = new ArrayList<>();//store all fragments
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);
        settings.add(getString(R.string.edit_profile));//fragment 0
        settings.add(getString(R.string.sign_out));//fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mcontext,android.R.layout.simple_list_item_1,settings);
        listView.setAdapter(adapter);// using adaptor to show settings in a listView way.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: go to fragment "+ position);
                setupViewPager(position);
            }
        });
    }


    private void setupGoBack(){
        ImageView goBack = (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked go back button.");
                finish();
            }
        });
    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
//        pagerAdapter.addFragment(new EditProfileFragment(),getString(R.string.edit_profile)); //fragment 0
//        pagerAdapter.addFragment(new SingOutFragment(),getString(R.string.sign_out));// fragment 1

    }

    private void setupViewPager(int fragmentNumber){
        myRelativeLayout.setVisibility(View.GONE);//hide the current layout
        Log.d(TAG, "setupViewPager: go to the fragment NO. "+ fragmentNumber);
        myViewPager.setAdapter(pagerAdapter);
        myViewPager.setCurrentItem(fragmentNumber);
    }

}
