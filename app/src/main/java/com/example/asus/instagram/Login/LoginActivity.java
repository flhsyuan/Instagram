package com.example.asus.instagram.Login;

/*
video session 22, cooperate with yuan
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.instagram.Home.HomeActivity;
import com.example.asus.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //TODO XI
    //firebase
     private FirebaseAuth mAuth;
     private FirebaseAuth.AuthStateListener mAuthListener;

     private Context mContext;
     private ProgressBar mProgressBar;
     private EditText mEmail, mPassword;
     private TextView mPleaseWait;


    /*
    TODO XI
    [fixed] one thing change: there is no activity_login, so use activity_home.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: login start. ");
        setContentView(R.layout.activity_login);//there is no activity_login
        mProgressBar = (ProgressBar)findViewById(R.id.loginProgressLoadingBar);
        mPleaseWait = (TextView)findViewById(R.id.pleaseWait);
        mEmail = (EditText)findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);


        setupFirebaseAuth();
        initLoginButton();
    }

    // The logic of click the login button
    private void initLoginButton(){
        Button login = (Button)findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked login button!");
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(email.equals("") || password.equals("")){
                    Toast.makeText(mContext,"Need to fill Email and password! ",Toast.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    // sign in existing users
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

//                                    try {
//
//                                    }catch (NullPointerException e){
//                                        Log.d(TAG, "onComplete: NullPointerException "+ e.getMessage());
//                                    }

//                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//                                    startActivity(intent);



                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                        startActivity(intent);
                                        Toast.makeText(LoginActivity.this, "Authentication success.",
                                                Toast.LENGTH_SHORT).show();
//                                        mProgressBar.setVisibility(View.GONE);
//                                        mPleaseWait.setVisibility(View.GONE);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                    }

                                    // ...
                                }
                            });
                }

            }
        });
        /**
         * Go to the home page if login successfully
         */
        if(mAuth.getCurrentUser() != null){
            Intent goToHomePage = new Intent();
            goToHomePage.setClass(LoginActivity.this,HomeActivity.class);
            startActivity(goToHomePage);
            finish();              // will not go back to the login page
        }
        /**
         * Go to the register page if the register button is clicked.
         */
        TextView signUp = (TextView)findViewById(R.id.link_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to the register page ");
                Intent goToRegisterPage = new Intent(mContext,RegisterActivity.class);
                startActivity(goToRegisterPage);
                finish();//***
            }
        });
    }

    /*
    checks to see if the @param user is logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if(user == null){
            Intent intent = new Intent(mContext,LoginActivity.class);
            startActivity(intent);
        }
    }

    //--------------------------firebase-----------------------------
    //Yuan

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                }else{
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
