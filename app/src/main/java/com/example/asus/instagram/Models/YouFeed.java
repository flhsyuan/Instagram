package com.example.asus.instagram.Models;

import android.os.Parcel;
import android.os.Parcelable;


public class YouFeed implements Parcelable{

    private String user_id;
    private String photo_id;
    private String date_created;

    public YouFeed() {

    }

    protected YouFeed(Parcel in) {
        user_id = in.readString();
        photo_id = in.readString();
        date_created = in.readString();
    }

    public static final Creator<YouFeed> CREATOR = new Creator<YouFeed>() {
        @Override
        public YouFeed createFromParcel(Parcel in) {
            return new YouFeed(in);
        }

        @Override
        public YouFeed[] newArray(int size) {
            return new YouFeed[size];
        }
    };

    @Override
    public String toString() {
        return "YouFeed{" +
                "user_id='" + user_id + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }

    public YouFeed(String user_id, String photo_id, String date_created) {
        this.user_id = user_id;
        this.photo_id = photo_id;
        this.date_created = date_created;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getUser_id() {

        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(photo_id);
        dest.writeString(date_created);
    }
}
