package com.example.asus.instagram.Models;
//Yuan: the data structure of user information

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String username;
    private String user_id;
    private String email;
    private long phone_number;

    public User(String username, String user_id, String email, long phone_number) {
        this.username = username;
        this.user_id = user_id;
        this.email = email;
        this.phone_number = phone_number;
    }

    public User() {

    }

    @Override
    public boolean equals(Object obj) {
        if (user_id.equals(((User)obj).user_id)){
            return true;
        }
        return false;
    }

    protected User(Parcel in) {
        username = in.readString();
        user_id = in.readString();
        email = in.readString();
        phone_number = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(user_id);
        dest.writeString(email);
        dest.writeLong(phone_number);
    }
}

