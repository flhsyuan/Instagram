package com.example.asus.instagram.Discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.asus.instagram.Models.User;
import com.example.asus.instagram.Profile.ProfileActivity;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.BottomNavigationViewHelper;
import com.example.asus.instagram.Utils.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DiscoverActivity extends AppCompatActivity {
    private static final String TAG = "DiscoverActivity";
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = DiscoverActivity.this;



    private List<User> mUserList;
    private UserListAdapter mAdapter;

    private ListView mListView;
    private EditText mSearchParameter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchParameter = (EditText) findViewById(R.id.search);
        mListView = (ListView)findViewById(R.id.listView);
        Log.d(TAG, "onCreate: started");
        hideKeyBoard();
        setupBottomNavigationView();
        initTextListener();
//        ListAllSuggestedUser(); // yuan list all suggested users.
        ListAllFriendsOfFriends();

        Log.d(TAG, "searchForMatch: Time to show the suggested");

    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }


    private void hideKeyBoard(){
        if (getCurrentFocus()!= null){

            InputMethodManager imm =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing the textListener");

        mUserList = new ArrayList<>();
        mSearchParameter.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mSearchParameter.getText().toString();
                searchForMatch(text);

            }
        });
    }

    private void searchForMatch(String keyBoardInput){
        Log.d(TAG, "searchForMatch: searching for matches..."+keyBoardInput);

        mUserList.clear();

        
        if(keyBoardInput.length() ==0){
//            ListAllSuggestedUser(); //yuan
            ListAllFriendsOfFriends();

        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username)).equalTo(keyBoardInput);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: We found an user "+singleSnapshot.getValue(User.class).toString());

                        mUserList.add(singleSnapshot.getValue(User.class));

                        updateUserList();
                        
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }



    //List all suggested users : all other users.
    private void ListAllSuggestedUser(){
        Log.d(TAG, "ListAllSuggestedUser: bigin to find suggested users");

        mUserList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: begin to add suggested users");

                    if(!dataSnapshot1.getValue(User.class).getUser_id()
                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        mUserList.add(dataSnapshot1.getValue(User.class));

                    }
                }
                updateUserList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    //List all suggested users : The friends' friends.
    private void ListAllFriendsOfFriends(){
        Log.d(TAG, "ListAllFriendsOfFriends: begin to find all friends' friends");

        //User id of all my following friends.
        final ArrayList<String> myFollowings = new ArrayList<String>();

        //User id of all my friends' friends
        final ArrayList<String> friendsFollowings = new ArrayList<String>();

        //Removed duplicated id
        final ArrayList<String> unDupFriendsFollowings = new ArrayList<String>();

        mUserList.clear();
        //        String currentUserId =
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    String friendsID =singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString();
                    System.out.println("friend: " + friendsID);
                    myFollowings.add(friendsID);
                }

                //        Log.d(TAG, "ListAllFriendsOfFriends:  all of my followings are "+ myFollowings.toString());

                // 对于我follow的所有好友，找出它们的所有follow。
                for (String myFollowing:myFollowings){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1.child(getString(R.string.dbname_following))
                            .child(myFollowing);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot1 :  dataSnapshot.getChildren()){
                                String friendsfriendsID = singleSnapshot1.child(getString(R.string.field_user_id)).getValue().toString();
                                if (!friendsfriendsID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    friendsFollowings.add(friendsfriendsID);
                                }
                            }

                            Log.d(TAG, "ListAllFriendsOfFriends:  all of my friends are "+ myFollowings.toString());
                            Log.d(TAG, "ListAllFriendsOfFriends:  all of my friends' friends are "+ friendsFollowings.toString());
                            // Use set to remove duplication.

                            Set set = new HashSet(friendsFollowings);
                            friendsFollowings.clear();

                            unDupFriendsFollowings.addAll(set);


                            Log.d(TAG, "onDataChange: unduplicate are "+unDupFriendsFollowings.toString());

                            //retrieve all user class.
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                            Query query2 = reference2.child(getString(R.string.dbname_users));
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                                        if( unDupFriendsFollowings.contains(dataSnapshot1.getValue(User.class).getUser_id())&&
                                                !myFollowings.contains(dataSnapshot1.getValue(User.class).getUser_id()) ){
                                            mUserList.add(dataSnapshot1.getValue(User.class));
                                        }
                                    }
                                    removeDuplicate();

                                    Log.d(TAG, "onDataChange: the two are"+mUserList.toString());

                                    updateUserList();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeDuplicate() {
        for  ( int  i  =   0 ; i  <  mUserList.size()  -   1 ; i ++ )   {
            for  ( int  j  =  mUserList.size()  -   1 ; j  >  i; j -- )   {
                if  (mUserList.get(j).equals(mUserList.get(i)))   {
                    mUserList.remove(j);
                }
            }
        }
    }

    private void updateUserList(){

        Log.d(TAG, "updateUserList: updating the user list");

        mAdapter = new UserListAdapter(DiscoverActivity.this,R.layout.layout_user_listitem,mUserList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());

                // click to enter the profile activity.

                Intent intent = new Intent(DiscoverActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.discover_activity));
                intent.putExtra(getString(R.string.intent_user),mUserList.get(position));
                startActivity(intent);
            }
        });

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
}
