package com.example.asus.instagram.Models;

public class Like {

    private String user_id;

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    private String date_created;

    public Like(String user_id) {
        this.user_id = user_id;
    }

    public Like(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Like{" +
                "user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
