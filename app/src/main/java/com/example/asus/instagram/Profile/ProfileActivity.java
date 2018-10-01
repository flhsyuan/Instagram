package com.example.asus.instagram.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.ViewPostFragment;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener{
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

    //implements the interface
    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {

        Log.d(TAG, "onGridImageSelected: selected an image gridView: " +photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putInt(getString(R.string.activity_number),activityNumber);

        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_container,fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }



}
