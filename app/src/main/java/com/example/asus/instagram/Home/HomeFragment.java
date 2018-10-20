package com.example.asus.instagram.Home;

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
import com.example.asus.instagram.Models.Photo;
import com.example.asus.instagram.Models.UserAccountSettings;
import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.MainfeedListAdapter;
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
import java.util.Map;

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mFollowing = new ArrayList<>();
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
                getPhotos();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size();i++){
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                // get the liked user name from firebase
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if(count >= mFollowing.size() - 1){
                        //display photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos(){
        if(mPhotos!=null){
            Collections.sort(mPhotos, new Comparator<Photo>() {
                @Override
                public int compare(Photo o1, Photo o2) {
                    return o2.getDate_created().compareTo(o1.getDate_created());
                }
            });

            mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPhotos);
            mListView.setAdapter(mAdapter);
        }
    }

}
