package com.example.asus.instagram.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.UserAccountsettings;
import com.example.asus.instagram.Models.UserSettings;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.SquareImageView;
import com.example.asus.instagram.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.support.constraint.Constraints.TAG;


// set the bundle
public class ViewPostFragment extends Fragment {

    private Photo mPhoto;
    private int ActivityNumber = 0;
    private String mProtraitString;
    private String mProtraitURLs;
    private UserAccountsettings mUserAccountsettings;

    //firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //widgets
    private TextView mCaption, mUsername, mPostTime, mBackLabel;
    private ImageView mBackArrow, mProtrait, mEllipses, mHeartRed, mHeartWhite;
    private SquareImageView mPostImage;
    private BottomNavigationView bottomNavigationView;

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post,container,false);
        mPostImage = (SquareImageView) view.findViewById(R.id.image_view_post);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        mPostImage =(SquareImageView)view.findViewById(R.id.image_view_post);
        mBackArrow = (ImageView)view.findViewById(R.id.backArrowPost);
        mProtrait = (ImageView)view.findViewById(R.id.protrait_view_post);
        mEllipses = (ImageView)view.findViewById(R.id.ivEllipses_view_post);
        mHeartRed = (ImageView)view.findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView)view.findViewById(R.id.image_heart);
        mCaption = (TextView)view.findViewById(R.id.image_captions);
        mUsername = (TextView)view.findViewById(R.id.username_view_post);
        mPostTime = (TextView)view.findViewById(R.id.image_time_post);
//        mBackLabel = (TextView)view.findViewById(R.id.backArrowPost);

        try {
            mPhoto = retrievePhotoFromBundle(); // using universal image loader to show image.
            UniversalImageLoader.setImage(mPhoto.getImage_path(),mPostImage,null,"");
            ActivityNumber = retrieveActivityNumberFromBundle();


        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: photo is null in bundle "+ e.getMessage());
        }

        setupFirebaseAuth();

        setupButtons();


        getPhotoDetails();

        return view;
    }



    /**
     * retrieve Activity Number From incoming Bundle from profile
     * @return
     */
    private int retrieveActivityNumberFromBundle() {
        Log.d(TAG, "retrievePhotoFromBundle: arguments "+getArguments());
        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    /**
     * calculate how many days the photo was updated
     */
    private String getUpdatedTime()  {
        final String photoTime = mPhoto.getDate_created();

        Log.d(TAG, "getUpdatedTime: calculating how many days the photo was updated ");
        String period = "";
        Calendar c =Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        timeFormat.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));
        timeFormat.format(today);

        Date timeStamp;
        try {
            timeStamp = timeFormat.parse(photoTime);
            period = String.valueOf(Math.round( today.getTime() - timeStamp.getTime() )/1000/60/60/24);

        }catch (ParseException p){
            Log.d(TAG, "getUpdatedTime: Parsing time data form DB failed");
            period = "0";
        }

        return  period;
    }

    /**
     * setup time difference
     */

    private void setupWidgets(){
        String timeDifference = getUpdatedTime();
        if (!timeDifference.equals("0")){
            mPostTime.setText(timeDifference + " Days Ago");
        }else{
            mPostTime.setText("Today");
        }
        UniversalImageLoader.setImage(mUserAccountsettings.getProfile_photo(), mProtrait,null,"");
        mUsername.setText(mUserAccountsettings.getUsername());


    }

    /**
     * setup some buttons: goback ,bottom navigation bar
     */

    private void setupButtons(){
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked go back button.");
                getActivity().onBackPressed();
            }
        });

        // call setupBottomNavigationView() after getting the activity number.
        setupBottomNavigationView();
    }
    /**
     * get details of posted photo
     */

    private void getPhotoDetails(){
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds :  dataSnapshot.getChildren()){
                    mUserAccountsettings = ds.getValue(UserAccountsettings.class);
                }
                setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }


    /** 
     * retrieve Photos From incoming Bundle from profile
     * @return
     */
    private Photo retrievePhotoFromBundle(){
        Log.d(TAG, "retrievePhotoFromBundle: arguments "+getArguments());
        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.enableNavigation(getActivity(),bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ActivityNumber);
        menuItem.setChecked(true);
    }

    //--------------------------firebase-----------------------------
    //Yuan

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
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

}
