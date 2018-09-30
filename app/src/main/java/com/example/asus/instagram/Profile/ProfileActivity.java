package com.example.asus.instagram.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.util.ArrayList;
import android.widget.GridView;

import com.example.asus.instagram.Login.LoginActivity;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.GridImageAdapter;
import com.example.asus.instagram.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int NUM_GRID_PER_COLUMN = 3;
    private static final int ACTIVITY_NUM = 4;
    private ImageView headProtrait;

    private ProgressBar myProgressbar;
    private Context mContext = ProfileActivity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        Log.d(TAG, "onCreate: personal_profile started");
        init();
    }

    private void init(){
        Log.d(TAG, "init: inflating "+getString(R.string.profile_fragment));
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_container, fragment);
//        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }


//        setupFirebaseAuth();
//
//        setupBottomNavigationView();
//        setupToolBar();
//        setupActivityWidgets();
//        setProfileImage();
//        tempGridSetup();
//    }
//
//    /**
//     * setup Toolbar on the right top of the profile
//     */
//    private void setupToolBar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
//        setSupportActionBar(toolbar);
//        ImageView signOut = (ImageView) findViewById(R.id.signout_button);
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: clicked signOut");
//                Intent intent = new Intent(mContext,SingOutFragment.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//
//    /**
//     * BottomNavigationView setup
//     */
//    private void setupBottomNavigationView() {
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
//        Menu menu = bottomNavigationView.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
//
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: setting profile photo.");
//        String imgURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IeLf";
//        UniversalImageLoader.setImage(imgURL, headProtrait, myProgressbar, "https://");
//    }
//
//    private void setupActivityWidgets(){
//        myProgressbar = (ProgressBar) findViewById(R.id.profile_progress_bar);
//        myProgressbar.setVisibility(View.GONE);
//        headProtrait =(ImageView) findViewById(R.id.profile_protrait);
//    }
//
//
//    private void setupImageGrid(ArrayList<String> imgURLs){
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        int grid_Width = getResources().getDisplayMetrics().widthPixels;
//        int image_Width = grid_Width/NUM_GRID_PER_COLUMN;
//        gridView.setColumnWidth(image_Width);
//
//        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
//        gridView.setAdapter(adapter);
//    }
//
//    private void tempGridSetup(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://pbs.twimg.com/profile_images/616076655547682816/6gMRtQyY.jpg");
//        imgURLs.add("https://i.redd.it/9bf67ygj710z.jpg");
//        imgURLs.add("https://c1.staticflickr.com/5/4276/34102458063_7be616b993_o.jpg");
//        imgURLs.add("http://i.imgur.com/EwZRpvQ.jpg");
//        imgURLs.add("http://i.imgur.com/JTb2pXP.jpg");
//        imgURLs.add("https://i.redd.it/59kjlxxf720z.jpg");
//        imgURLs.add("https://i.redd.it/pwduhknig00z.jpg");
//        imgURLs.add("https://i.redd.it/clusqsm4oxzy.jpg");
//        imgURLs.add("https://i.redd.it/svqvn7xs420z.jpg");
//        imgURLs.add("http://i.imgur.com/j4AfH6P.jpg");
//        imgURLs.add("https://i.redd.it/89cjkojkl10z.jpg");
//        imgURLs.add("https://i.redd.it/aw7pv8jq4zzy.jpg");
//
//        setupImageGrid(imgURLs);
//    }

    //--------------------------firebase-----------------------------
    //Yuan

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        checkCurrentUser(currentUser);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
//    /*
//    checks to see if the @param user is logged in
//     */
//    private void checkCurrentUser(FirebaseUser user){
//        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
//        if(user == null){
//            Intent intent = new Intent(mContext,LoginActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user != null){
//                    //user is signed in
//                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
//                }else{
//                    //user is signed out
//                    Log.d(TAG, "onAuthStateChanged: signed_out");
//                }
//            }
//        };
//    }
}
