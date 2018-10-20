package com.example.asus.instagram.Feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.asus.instagram.Home.HomeFragment;
import com.example.asus.instagram.Home.PositionFragment;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.SectionsPagerAdapter;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = "FeedActivity";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = FeedActivity.this;
    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFramLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_feed);
        Log.d(TAG, "onCreate: started");

        setupBottomNavigationView();
        setupViewPager();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FollowingFragment());// Following fragment :index 0
        adapter.addFragment(new YouFragment()); //You fragment :index 1
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);

        tabLayout.getTabAt(0).setText("Following");
        tabLayout.getTabAt(1).setText("You");

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        Log.e("aAAAAAAAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAAAAAAA" );
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
