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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.asus.instagram.Login.LoginActivity;
import com.example.asus.instagram.Models.Like;
import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.User;
import com.example.asus.instagram.Models.UserAccountsettings;
import com.example.asus.instagram.Models.UserSettings;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Upload.UploadActivity;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.FirebaseMethods;
import com.example.asus.instagram.Utils.GridImageAdapter;
import com.example.asus.instagram.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ProfileFragment extends Fragment{
    //firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    // GridImage Listener interface
    // Because there are lots of ways to get to view post image fragment
    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    //static numbers
    private static final int ACTIVITY_NUM = 4;
    private static final int GRID_COLUNNS = 3;

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
        firebaseMethods = new FirebaseMethods(mContext);

        Log.d(TAG, "onCreateView: profile fragment started  ");

        setupFirebaseAuth();
        setupGridView();  //
        setupBottomNavigationView();
        setupSignOutButton();

        editProtrait(view);

        return view;
    }

    /**
     * edit protrait if click the editProtrait button.
     * @param view
     */
    private void editProtrait(View view) {
        TextView editProtrait = (TextView) view.findViewById(R.id.editProtraitText);
        editProtrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+ mContext.getString(R.string.edit_protrait));
                Intent editProtrait = new Intent(mContext, UploadActivity.class);
                editProtrait.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// Give this intent a flag.
                startActivity(editProtrait);
            }
        });
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

    /**
     * setup several Profile widgets
     * @param userSettings
     */
    private  void setProfileWidgets(UserSettings userSettings){
        User user = userSettings.getUser();
        UserAccountsettings userAccountsettings = userSettings.getUserAccountsettings();

        mUsername.setText(userAccountsettings.getUsername());
        mDisplayName.setText(userAccountsettings.getDisplay_name());
        mDescription.setText(userAccountsettings.getDescription());
        mPosts.setText(String.valueOf(userAccountsettings.getPosts()));
        mFollowers.setText(String.valueOf(userAccountsettings.getFollowers()));
        mFollowings.setText(String.valueOf(userAccountsettings.getFollowings()));

        UniversalImageLoader.setImage(userAccountsettings.getProfile_photo(),mProfilePhoto,null,"");
        mProgressBar.setVisibility(View.GONE);

    }

    /**
     * call back interface used when several fragments are managed by a single activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        try{

            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch(ClassCastException c){

            Log.d(TAG, "onAttach: ClassCastException " + c.getMessage().toString());
        }
        super.onAttach(context);
    }

    /**
     * setup grid view, and retrieval uploaded photos of current user
     */

    private void setupGridView(){
        Log.d(TAG, "setupGridView: begin to setup grid view in the profile ");

        final ArrayList<Photo> profilePhotos= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //retrieval photos according to the user ID using the user_photos in firebase DB
        Query query = reference.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override      //each snapshot here represents a user object.
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren() ){

                    Photo photo = new Photo();
                    Map<String,Object> objectMap = (HashMap<String,Object>) ds.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                    List<Like> likeList = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot: ds
                            .child(getString(R.string.field_likes)).getChildren()){
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likeList.add(like);
                    }
                    photo.setLikes(likeList);
                    profilePhotos.add(photo);

                }
            // put these photos to our image grid

                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/GRID_COLUNNS;
                gridView.setColumnWidth(imageWidth);  // set the width of each photo

                //put all the imageURLs into a list
                ArrayList<String> imageURLS = new ArrayList<String>();
                for (int i = 0 ;i < profilePhotos.size();i++){
                    imageURLS.add(profilePhotos.get(i).getImage_path());
                }

                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,"",imageURLS);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(profilePhotos.get(position),ACTIVITY_NUM);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query is stop ");
            }
        });





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

        // read or write to the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user info from the DB

                UserSettings userSettings = firebaseMethods.getUserSettings(dataSnapshot);
                setProfileWidgets(userSettings);

                //retrieve images for the user in question

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
