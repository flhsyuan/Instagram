package com.example.asus.instagram.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class YouFeedAdapter extends ArrayAdapter<YouFeed>{
    private ArrayList<YouActivityFeed> data = new ArrayList<YouActivityFeed>();
    private Context context;
    private int layoutResourceId;

    public YouFeedAdapter(Context context, int resource, ArrayList<YouActivityFeed> data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public YouActivityFeed getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(YouActivityFeed item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.profilepic = (ImageView) row.findViewById(R.id.iv_follower_photo);
            holder.uploadedphoto = (ImageView) row.findViewById(R.id.iv_unfollow);
            holder.description = (TextView) row.findViewById(R.id.tv_description);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        YouActivityFeed item = (YouActivityFeed) data.get(position);

        String timePublish="";
        Bitmap profilepic_bitmap = Utils.getBitmap(item.getUrlProfilePic());
        holder.profilepic.setImageBitmap(profilepic_bitmap);
        if (item.getUrlPhoto()!=null) {
            Bitmap uploadpic_bitmap = Utils.getBitmap(item.getUrlPhoto());
            holder.uploadedphoto.setImageBitmap(uploadpic_bitmap);
            timePublish = Utils.getElapsedtime(item.getTimePublication(), "short");
        }
        else
        {
            holder.uploadedphoto.setImageResource(R.drawable.btn_user_followed);
        }
        holder.description.setText(Html.fromHtml("<font color='#2B547E'><b>" + item.getUsername() + "</b></font>" + " " +"<font color='#2C3539'>"+ item.getType() + item.getComment() +"</font>"+ " "  + timePublish ));

        return row;
    }

    static class ViewHolder {
        ImageView profilepic;
        ImageView uploadedphoto;
        TextView description;

    }
}


