package com.example.asus.instagram.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.instagram.Models.Comment;
import com.example.asus.instagram.Models.Like;
import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.User;
import com.example.asus.instagram.Models.UserAccountSettings;
import com.example.asus.instagram.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


// set the bundle
public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Photo photo);
    }
    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    private Photo mPhoto;
    private int ActivityNumber = 0;
    private String mProtraitString;
    private String mProtraitURLs;
    private UserAccountSettings mUserAccountSettings;
    private Heart mHeart;
    private GestureDetector mGestureDetector;
    private boolean likedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";
    private User mCurrentUser;

    //firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //widgets

    private ImageView mBackArrow, mProtrait, mEllipses, mHeartRed, mHeartWhite, mComment;
    private TextView mCaption, mUsername, mPostTime, mBackLabel, mLikes, mComments;
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
        mLikes = (TextView) view.findViewById(R.id.image_likes);
//        mBackLabel = (TextView)view.findViewById(R.id.backArrowPost);

        mHeart = new Heart(mHeartWhite,mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        mComment = (ImageView) view.findViewById(R.id.comment_view_post);
        mComments = (TextView) view.findViewById(R.id.view_all_comments);



        setupFirebaseAuth();

        setupButtons();




        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    // get the string of liked people.
    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // get the liked user name from firebase
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: found like: " + singleSnapshot.getValue(User.class).getUsername());

                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");

                            if(mUsers.toString().contains(mCurrentUser.getUsername()+",")){
                                likedByCurrentUser = true;
                            }else {
                                likedByCurrentUser = false;
                            }

                            int userNumbers = splitUsers.length;
                            if(userNumbers==1){
                                mLikesString = "Liked by "+ splitUsers[0];

                            }else if(userNumbers == 2){
                                mLikesString = "Liked by "+ splitUsers[0]+
                                        " and "+splitUsers[1];

                            }else if(userNumbers == 3){
                                mLikesString = "Liked by "+ splitUsers[0]
                                        +" , "+splitUsers[1]
                                        +" and "+splitUsers[1];

                            }else if(userNumbers == 4){
                                mLikesString = "Liked by "+ splitUsers[0]
                                        +" , "+splitUsers[1]
                                        +" , "+splitUsers[2]
                                        +" and "+splitUsers[3];

                            }else if(userNumbers > 4){
                                mLikesString = "Liked by "+ splitUsers[0]
                                        +" , "+splitUsers[1]
                                        +" , "+splitUsers[2]
                                        +" and " + (splitUsers.length - 3) + " others";
                            }
                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    mLikesString = "";
                    likedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void init(){
        try {
            UniversalImageLoader.setImage(retrievePhotoFromBundle().getImage_path(), mPostImage, null, "");
            ActivityNumber = retrieveActivityNumberFromBundle();
            String photo_id = retrievePhotoFromBundle().getPhoto_id();

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_photos))
                    .orderByChild(getString(R.string.field_photo_id))
                    .equalTo(photo_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        Photo newPhoto = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        List<Comment> commentsList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);

                        mPhoto = newPhoto;

                        getCurrentUser();
                        getPhotoDetails();
//                        getLikesString();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });


        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: photo is null in bundle "+ e.getMessage());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()){
            init();
        }
    }

    private void getCurrentUser(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds :  dataSnapshot.getChildren()){
                    mCurrentUser = ds.getValue(User.class);
                }
                getLikesString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }
        //single click the like button.
        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();
                        //1. the user already liked the photo.

                        if(likedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            myRef.child(getString(R.string.dbname_photos))
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            myRef.child(getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mHeart.toggleLike();
                            getLikesString();
                        }

                        else if(!likedByCurrentUser){
                            addNewLike();
                            break;
                        }

                    }
                    if(!dataSnapshot.exists()){
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            mHeart.toggleLike(); the click heart function.
            return true;
        }
    }

    private void addNewLike(){
        String newLikeID = myRef.push().getKey();
        Like like =  new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mHeart.toggleLike();
        getLikesString();
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
        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProtrait,null,"");
        mUsername.setText(mUserAccountSettings.getUsername());
        mLikes.setText(mLikesString);
        mCaption.setText(mPhoto.getCaption());

        if(mPhoto.getComments().size()>0) {
            mComments.setText("View all " + mPhoto.getComments().size() + "comments");
        }else{
            mComments.setText("");
        }

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments thread");

                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back");
                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
            }
        });

        if(likedByCurrentUser){
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }else {
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }



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
                    mUserAccountSettings = ds.getValue(UserAccountSettings.class);
                }
//                setupWidgets();
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
