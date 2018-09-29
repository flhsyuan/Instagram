package com.example.asus.instagram.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ProfileFragment extends Fragment{

    private static final int ACTIVITY_NUM = 4;

    private Context mContext;

    private static final String TAG = "ProfileFragment";
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private Toolbar mToolBar;
    private GridView gridView;
    private ImageView profileSignout;
    private BottomNavigationView bottomNavigationView;
    private TextView mPosts, mFollowings, mFollowers, mUsername, mDisplayName, mDescription;

    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        mContext = getActivity();

        mDisplayName =(TextView)view.findViewById(R.id.profileName);
        mUsername = (TextView)view.findViewById(R.id.protrait_name);
        mDescription = (TextView)view.findViewById(R.id.protrait_description);
        mPosts = (TextView)view.findViewById(R.id.postNumber);
        mFollowings = (TextView)view.findViewById(R.id.followingNumber);
        mFollowers = (TextView)view.findViewById(R.id.followerNumber);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        mToolBar = (Toolbar) view.findViewById(R.id.profileToolBar);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_protrait);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profile_progress_bar);
        profileSignout = (ImageView) view.findViewById(R.id.signout_button);

        Log.d(TAG, "onCreateView: profile fragment started  ");

        setupBottomNavigationView();
        setupSignOutButton();


        return view;
    }

    /**
     * setup Toolbar on the right top of the profile
     */
    private void setupSignOutButton(){
        ((ProfileActivity)getActivity()).setSupportActionBar(mToolBar);

        profileSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked signOut");
                Intent intent = new Intent(mContext,SingOutFragment.class);
                startActivity(intent);
            }
        });

    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
