package com.example.asus.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.example.asus.instagram.Discover.DiscoverActivity;
import com.example.asus.instagram.Feed.FeedActivity;
import com.example.asus.instagram.Home.HomeActivity;
import com.example.asus.instagram.Profile.ProfileActivity;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Upload.UploadActivity;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void enableNavigation(final Context context, BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.ic_user_feed:
                        Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_discover:
                        Intent intent2 = new Intent(context, DiscoverActivity.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_upload_photo:
                        Intent intent3 = new Intent(context, UploadActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_activity_feed:
                        Intent intent4 = new Intent(context, FeedActivity.class);//ACTIVITY_NUM = 3
                        context.startActivity(intent4);
                        break;
                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 4
                        context.startActivity(intent5);
                        break;
                }

                return false;
            }
        });
    }
}
