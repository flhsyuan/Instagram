package com.example.asus.instagram.Feed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.asus.instagram.Models.Comment;
import com.example.asus.instagram.Models.Like;
import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.YouFeed;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.GridImageAdapter;
import com.example.asus.instagram.Utils.MainfeedListAdapter;
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
public class YouFragment extends Fragment {

    private ArrayList<YouFeed> mYouFeed;
    private ArrayList<YouFeed> mPaginatedFeeds;
    private ListView mListView;
    private ArrayList<String> mFollower;
    private int mResults;
    private YouFeedAdapter mAdapter;
    private ArrayList<Photo> mPhotos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mFollower = new ArrayList<>();
        mYouFeed = new ArrayList<>();
        mPhotos = new ArrayList<>();
        getFollower();
        getPhotos();


        return view;
    }

    private void getFollower(){
        Log.d(TAG, "getFollower: search for follower.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // get the liked user name from firebase
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollower.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                    YouFeed youFeed = new YouFeed();
                    youFeed.setUser_id(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                    youFeed.setDate_created(singleSnapshot.child(getString(R.string.field_date_created)).getValue().toString());
                    mYouFeed.add(youFeed);
                    displayYouFeeds();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos(){
        Log.d(TAG, "getFollower: search for follower.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //retrieval photos according to the user ID using the user_photos in firebase DB
        Query query = reference.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override      //each snapshot here represents a user object.
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren() ){

                    Photo photo = new Photo();
                    Map<String,Object> objectMap = (HashMap<String,Object>) ds.getValue();

                    try{
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot: ds
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();

                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likeList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot: ds
                                .child(getString(R.string.field_likes)).getChildren()){
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likeList.add(like);

                            //add to youfeed
                            YouFeed youFeed = new YouFeed();
                            youFeed.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            youFeed.setDate_created(dSnapshot.getValue(Like.class).getDate_created());
                            youFeed.setPhoto_id(photo.getPhoto_id());
                            mYouFeed.add(youFeed);
                            displayYouFeeds();
                        }
                        photo.setLikes(likeList);
                        mPhotos.add(photo);
                    }
                    catch (NullPointerException e){
                        Log.d(TAG, "onDataChange: NullPointerException ");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query is stop ");
            }
        });
    }


    private void displayYouFeeds(){
        mPaginatedFeeds = new ArrayList<>();

        if(mYouFeed!=null){
            try{
                Collections.sort(mYouFeed, new Comparator<YouFeed>() {
                    @Override
                    public int compare(YouFeed o1, YouFeed o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mYouFeed.size();

                if(iterations > 20){
                    iterations = 20;
                }

                mResults = 20;
                for(int i = 0; i < iterations; i++){
                    mPaginatedFeeds.add(mYouFeed.get(i));
                }

                mAdapter = new YouFeedAdapter(getActivity(), R.layout.layout_you_list_activity_feed, mPaginatedFeeds);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException" + e.getMessage());
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException" + e.getMessage());
            }
        }
    }


}

