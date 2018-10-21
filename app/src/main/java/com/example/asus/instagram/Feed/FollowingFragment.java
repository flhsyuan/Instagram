package com.example.asus.instagram.Feed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.asus.instagram.Models.Comment;
import com.example.asus.instagram.Models.FollowingFeed;
import com.example.asus.instagram.Models.Like;
import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.YouFeed;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.FollowingFeedAdapter;
import com.example.asus.instagram.Utils.YouFeedAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/**
 * @author : Yujie Lyu
 * @date : 21-10-2018
 * @time : 09:39
 */
public class FollowingFragment extends Fragment {

    private ArrayList<FollowingFeed> mFollowingFeed;
    private ArrayList<FollowingFeed> mPaginatedFeeds;
    private ListView mListView;
    private ArrayList<String> mFollowing;
    private int mResults;
    private FollowingFeedAdapter mAdapter;
    private ArrayList<Photo> mPhotos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mFollowing = new ArrayList<>();
        mFollowingFeed = new ArrayList<>();
        mPhotos = new ArrayList<>();
        getFollowing();


        return view;
    }

    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // get the liked user name from firebase
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());

                }

                for(String user_id: mFollowing){
                    getFollowingActivity(user_id);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getFollowingActivity(final String user_id){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // get the liked user name from firebase
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                    FollowingFeed followingFeed = new FollowingFeed();
                    followingFeed.setUser_id(user_id);
                    followingFeed.setDate_created(singleSnapshot.child(getString(R.string.field_date_created)).getValue().toString());
                    followingFeed.setFollower_id(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                    mFollowingFeed.add(followingFeed);
                    displayFollowingFeed();
                    System.out.println("123");
                    System.out.println(mFollowingFeed);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void displayFollowingFeed(){
        mPaginatedFeeds = new ArrayList<>();

        if(mFollowingFeed !=null){
            try{
                Collections.sort(mFollowingFeed, new Comparator<FollowingFeed>() {
                    @Override
                    public int compare(FollowingFeed o1, FollowingFeed o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mFollowingFeed.size();

                if(iterations > 20){
                    iterations = 20;
                }

                mResults = 20;
                for(int i = 0; i < iterations; i++){
                    mPaginatedFeeds.add(mFollowingFeed.get(i));
                }

                mAdapter = new FollowingFeedAdapter(getActivity(), R.layout.layout_following_list_activity_feed, mPaginatedFeeds);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException" + e.getMessage());
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException" + e.getMessage());
            }
        }
    }


}

