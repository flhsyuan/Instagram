
package com.example.asus.instagram.Upload;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.asus.instagram.Utils.SectionsPagerAdapter;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.Permissions;

/**
 * Created by Yiqun
 */
public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "UploadActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager mViewPager;

    private Context mContext = UploadActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Log.d(TAG, "onCreate: started");

        if(checkPermissionsArray(Permissions.PERMISSIONS)) {
            setupViewPager();
        }else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        //setupBottomNavigationView();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     * @return
     */
    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();
    }

    /**
     *  setup viewpager for manager the tabs
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tablayout = (TabLayout) findViewById(R.id.tabsBottom);
        tablayout.setupWithViewPager(mViewPager);

        tablayout.getTabAt(0).setText(getString(R.string.gallery));
        tablayout.getTabAt(1).setText(getString(R.string.photo));

    }

    public int getTask(){
        Log.d(TAG, "getTask: Task: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * verify all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                UploadActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     *  Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if(!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been varified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

       int permissionRequest = ActivityCompat.checkSelfPermission(UploadActivity.this,permission);

       if(permissionRequest != PackageManager.PERMISSION_GRANTED) {
           Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
           return false;
       }
       else {
           Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
           return true;
       }
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
