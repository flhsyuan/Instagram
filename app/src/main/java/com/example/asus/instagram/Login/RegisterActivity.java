package com.example.asus.instagram.Login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.asus.instagram.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";


    /*
    TODO XI
   [fixed] one thing change: there is no activity_register, so use activity_account_setting
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);//there is no activity_login
        Log.d(TAG, "onCreate: started.");
    }
}
