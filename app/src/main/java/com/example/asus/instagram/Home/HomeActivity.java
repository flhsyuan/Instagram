package com.example.asus.instagram.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.example.asus.instagram.Login.LoginActivity;
import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.UserAccountsettings;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.SectionsPagerAdapter;
import com.example.asus.instagram.Utils.UniversalImageLoader;

import com.example.asus.instagram.Utils.ViewCommentsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;

    private Context mContext = HomeActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private  ViewPager mViewPager;
    private FrameLayout mFramLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFramLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        setupFirebaseAuth();

        initImageLoader();

        setupBottomNavigationView();
        setupViewPager();


    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /**
     * Adding the 2 tabs: Camera, and Home
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment());// Camera icon on the left top of the homePage :index 0
        adapter.addFragment(new HomeFragment()); //icon on the top of the homePage :index 1
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFramLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFramLayout.setVisibility(View.GONE);
    }

    public  void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFramLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    /**
     * Initializing the universalImageLoader
     */
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    //--------------------------firebase-----------------------------
    //Yuan

        private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                checkCurrentUser(mAuth.getCurrentUser());

                if(user != null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                }else{
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }
    /*
    checks to see if the @param user is logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if(user == null){
            Intent intent = new Intent(mContext,LoginActivity.class);
            startActivity(intent);
        }
    }

        @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
