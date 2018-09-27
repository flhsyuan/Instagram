package com.example.asus.instagram.Login;

/*
video session 22, cooperate with yuan
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.asus.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

//    //TODO XI
//    //firebase
//     private FirebaseAuth mAuth;
//     private FirebaseAuth.AuthStateListener mAuthListener;
//     private Context mContext;
//     private ProgressBar mProgressBar;
//     private EditText mEmail, mPassword;
//     private TextView mPleaseWait;


    /*
    TODO XI
    [fixed] one thing change: there is no activity_login, so use activity_home.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: login start. ");
        setContentView(R.layout.activity_login);//there is no activity_login
//        mProgressBar = (ProgressBar)findViewById(R.id.profile_progress_bar);
//        mPleaseWait = (TextView)findViewById(R.id.pleaseWait);
//        mEmail = (EditText)findViewById(R.id.input_email);
//        mPassword = (EditText)findViewById(R.id.input_password);
//        mContext = LoginActivity.this;
//        Log.d(TAG, "onCreate: started.");
//
//        mProgressBar.setVisibility(View.GONE);
//        mPleaseWait.setVisibility(View.GONE);

    }

    //--------------------------firebase-----------------------------
    //Yuan

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
//
//    /*
//    checks to see if the @param user is logged in
//     */
//    private void checkCurrentUser(FirebaseUser user){
//        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
//        if(user == null){
//            Intent intent = new Intent(mContext,LoginActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private void updateUI(FirebaseUser user){
//        //check if the user is logged in
//        checkCurrentUser(user);
//        if(user != null){
//            //user is signed in
//            Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
//        }else{
//            //user is signed out
//            Log.d(TAG, "onAuthStateChanged: signed_out");
//        }
//    }
}
