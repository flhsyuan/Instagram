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
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.MainfeedListAdapter;
import com.example.asus.instagram.Utils.SectionsPagerAdapter;
import com.example.asus.instagram.Utils.UniversalImageLoader;

import com.example.asus.instagram.Utils.ViewCommentsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;


public class HomeActivity extends AppCompatActivity
        implements MainfeedListAdapter.OnLoadMoreItemsListener{

        @Override
        public void onLoadMoreItems(){
            Log.d(TAG, "onLoadMoreItems: displaying more photos");
            HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                    .findFragmentByTag("android.switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
            if(fragment !=null){
                fragment.displayMorePhotos();
            }
        }

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

    //xi location
    double userLatitude;
    double userLongitude;

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
        adapter.addFragment(new PositionFragment());// Camera icon on the left top of the homePage :index 0
        adapter.addFragment(new HomeFragment()); //icon on the top of the homePage :index 1
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);

        tabLayout.getTabAt(0).setText("Sort by location");
        tabLayout.getTabAt(1).setText("Sort by date");

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


    /*
    ** XI   userfeed sort by location+++++++++++++++++++++++++++++++++++++++++++++++++++++++
    * TODO user feed sort by location
     */


//    /**
//     * Handles the behaviour of the Switch sort by Date/Time (not checked) or Location (checked)
//     * @param item_switch
//     */
//    public void HandleSwitchSortBy(MenuItem item_switch) {
//
//        final boUserFeed objUserFeed = new boUserFeed();
//
//        item_switch.setActionView(R.layout.actionbar_switchsortby);
//
//        sortBy = (SwitchCompat) item_switch.getActionView().findViewById(R.id.switchSortBy);
//
//        sortBy.setChecked(Globals.switchState);
//
//        sortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    GPSLocation gpsLocation = new GPSLocation();
//                    gpsLocation.execute();
//                } else {
//                    Toast.makeText(getApplication(), "Feeds based on Date/Time", Toast.LENGTH_SHORT).show();
//                    UserFeedFragment.getData(SORT_BY_DATETIME, 0, 0);
//                    Globals.switchState = false;
//
//                }
//            }
//        });
//
//    }
//
//
//
//    /**
//     * Get the visible fragemnt
//     * @return the fragment where the user is
//     */
//    public Fragment getVisibleFragment() {
//        return visibleFragment;
//    }
//
//    /**
//     * Set the visible fragment
//     * @param fragment where the user is
//     */
//    public void setVisibleFragment(Fragment fragment) {
//        this.visibleFragment = fragment;
//    }
//
//
//
//    /**
//     * Handles the getting of gps coordinates from the mobile phone
//     * both providers (network and GPS) make the requestlocation
//     */
//    public class GPSLocation extends AsyncTask<String, Integer, String> {
//
//        ProgressDialog progDailog = null;
//
//        public int time = 0;
//        public boolean outOfTime = false;
//
//        public double lati = 0;
//        public double longi = 0;
//
//        public LocationManager mLocationManager;
//        public MyLocationListener mLocationListener;
//
//        String provider1,provider2;
//
//        @Override
//        protected void onPreExecute() {
//            mLocationListener = new MyLocationListener();
//            mLocationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
//
//            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Utils.displayPromptForEnablingGPS(MainActivity.this);
//                sortBy.setChecked(false); //the switch continue in its default state (Datetime)
//                Globals.switchState = false;
//                GPSLocation.this.cancel(true);
//            }else{
//                //Network provider criteria
//                Criteria criteria1 = new Criteria();
//                criteria1.setAccuracy(Criteria.ACCURACY_COARSE);
//                criteria1.setPowerRequirement(Criteria.POWER_LOW);
//                provider1 = mLocationManager.getBestProvider(criteria1,true);
//
//                //GPS provider criteria
//                Criteria criteria2 = new Criteria();
//                criteria2.setAccuracy(Criteria.ACCURACY_FINE);
//                provider2 = mLocationManager.getBestProvider(criteria2,true);
//
//
//                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    mLocationManager.requestLocationUpdates(
//                            provider1, 0, 0,
//                            mLocationListener);
//                }
//
//                mLocationManager.requestLocationUpdates(
//                        provider2, 0, 0,
//                        mLocationListener);
//            }
//
//            progDailog = new ProgressDialog(MainActivity.this);
//            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    GPSLocation.this.cancel(true);
//                }
//            });
//            progDailog.setMessage("Getting GPS location... ");
//            progDailog.setIndeterminate(true);
//            progDailog.setCancelable(true);
//            progDailog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            Timer t =new Timer();
//
//            TimerTask tk = new TimerTask() {
//
//                @Override
//                public void run() {
//                    outOfTime = true;
//                }
//            };
//
//            t.schedule(tk, 1700000); //to wait approximately 30 secs.
//
//            while(!outOfTime){
//                if (lati != 0 && longi != 0) {
//                    t.cancel();
//                    tk.cancel();
//                    break;
//                }
//            }
//
//            if (outOfTime) return "err";
//
//            return "ok";
//        }
//
//
//        @Override
//        protected void onCancelled(){
//            System.out.println("Cancelled by user!");
//            progDailog.dismiss();
//            mLocationManager.removeUpdates(mLocationListener);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            progDailog.dismiss();
//            mLocationManager.removeUpdates(mLocationListener);
//
//            if (result.equals("ok")) {
//                userLatitude = lati;
//                userLongitude = longi;
//
//                UserFeedFragment.getData(SORT_BY_LOCATION, userLatitude, userLongitude);
//                Globals.switchState = true;
//                Toast.makeText(getApplication(), "Feeds based on Location", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, "GPS location: " + String.valueOf(userLatitude) + " "+ String.valueOf(userLongitude) , Toast.LENGTH_LONG).show();
//
//            }else {
//                Toast.makeText(MainActivity.this,
//                        "Please be located in an area of greater coverage or try again",
//                        Toast.LENGTH_LONG).show();
//                sortBy.setChecked(false);
//
//            }
//
//        }
//
//
//        public class MyLocationListener implements LocationListener {
//
//            @Override
//            public void onLocationChanged(Location location) {
//                try {
//                    lati = location.getLatitude();
//                    longi = location.getLongitude();
//                } catch (Exception e) {
//                    Log.i(TAG,e.getMessage());
//                }
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.i(TAG, "OnProviderDisabled");
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.i(TAG, "onProviderEnabled");
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status,
//                                        Bundle extras) {
//                Log.i(TAG, "onStatusChanged");
//
//            }
//
//        }
//
//    }
//



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
