package com.example.asus.instagram.Utils;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.instagram.Models.FollowingFeed;
import com.example.asus.instagram.Models.User;
import com.example.asus.instagram.Models.UserAccountSettings;
import com.example.asus.instagram.Models.YouFeed;
import com.example.asus.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class FollowingFeedAdapter extends ArrayAdapter<FollowingFeed>{
    private static final String TAG= "FollowingFeedAdapter";

    private LayoutInflater mInflater;
    private List<FollowingFeed> mFollowingFeed = null;
    private int layoutResource;
    private Context mContext;


    public FollowingFeedAdapter(@NonNull Context context, int resource, ArrayList<FollowingFeed> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
        this.mFollowingFeed = objects;
        System.out.println("adapter:");
        System.out.println(mFollowingFeed);

    }


    private static class ViewHolder{
        TextView username, description, timeDelta, username2;
        ImageView profileImage, likesPhoto;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final FollowingFeedAdapter.ViewHolder holder;

        if(convertView ==null){
            convertView = mInflater.inflate(layoutResource, parent,false);
            holder = new FollowingFeedAdapter.ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.tv_user);
            holder.username2 = (TextView) convertView.findViewById(R.id.tv_user2);
            holder.profileImage = (ImageView) convertView.findViewById(R.id.iv_follower_photo);
            holder.likesPhoto = (ImageView) convertView.findViewById(R.id.iv_following_photo);
            holder.description = (TextView) convertView.findViewById(R.id.tv_like_post);
            holder.timeDelta = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        }else {
            holder = (FollowingFeedAdapter.ViewHolder) convertView.getTag();
        }


        String timestampDifference = getUpdatedTime(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDelta.setText(timestampDifference + "d");
            System.out.println(timestampDifference);
        }else {
            holder.timeDelta.setText("Today");
            System.out.println(timestampDifference);
        }

        holder.description.setText("follows");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+
                            singleSnapshot.getValue(UserAccountSettings.class).toString());

                    System.out.println("profile:" + singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo());
                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query2 = reference.child(mContext.getString(R.string.dbname_photos))
                .orderByChild(mContext.getString((R.string.field_photo_id)))
                .equalTo(getItem(position).getPhoto_id());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(objectMap.get("image_path").toString(),
                            holder.likesPhoto);
                    holder.description.setText("likes post");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get the user object
        Query userQuery = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // get the liked user name from firebase
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(User.class).getUsername());

                    holder.username.setText(singleSnapshot.getValue(User.class).getUsername());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get the user object
        Query userQuery2 = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getFollower_id());
        userQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // get the liked user name from firebase
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(User.class).getUsername());

                    holder.username2.setText(singleSnapshot.getValue(User.class).getUsername());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return convertView;
    }

    /**
     * calculate how many days the photo was updated
     */
    private String getUpdatedTime(FollowingFeed feed)  {
        final String feedTime = feed.getDate_created();

        Log.d(TAG, "getUpdatedTime: calculating how many days the photo was updated ");
        String period = "";
        Calendar c =Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        timeFormat.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));
        timeFormat.format(today);

        Date timeStamp;
        try {
            timeStamp = timeFormat.parse(feedTime);
            period = String.valueOf(Math.round( today.getTime() - timeStamp.getTime() )/1000/60/60/24);

        }catch (ParseException p){
            Log.d(TAG, "getUpdatedTime: Parsing time data form DB failed");
            period = "0";
        }

        return  period;
    }

}


