package com.example.asus.instagram.Login;
/**
 * Yuan: Register logic
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

import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    //firebase Authentication & DB
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference myRef;
    private String randomString="";

    private Context mContext;
    private EditText mEmail, mPassword, mUsername;
    private String email, password,username;
    private Button mButtonRegister;
    private TextView mPleaseWait;
    private ProgressBar mProgressBar;

    // use methods of firabase
    private FirebaseMethods firebaseMethods;

    /*
    TODO XI
   [fixed] one thing change: there is no activity_register, so use activity_account_setting
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);//there is no activity_login
        Log.d(TAG, "onCreate: started.");

        mContext = RegisterActivity.this;

        //initialize the firebaseMethods object
        firebaseMethods= new FirebaseMethods(mContext);

        initWidget();

        setupFirebaseAuth();

        initRegisterInfo();
    }

    /**
     *
     * Initialize the activity widgets
     */
    private void initWidget(){
        Log.d(TAG, "initWidget: initialize the Widgets");
        mProgressBar = (ProgressBar)findViewById(R.id.Register_ProgressLoadingBar);
        mPleaseWait = (TextView)findViewById(R.id.Register_PleaseWait);
        mEmail = (EditText)findViewById(R.id.Register_input_email);
        mPassword = (EditText)findViewById(R.id.Register_input_password);
        mUsername = (EditText)findViewById(R.id.Register_input_username);
        mButtonRegister = (Button) findViewById(R.id.Register_button);
        mContext = RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
    }

    private void initRegisterInfo(){
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = mEmail.getText().toString();
                 username = mUsername.getText().toString();
                 password = mPassword.getText().toString();

                 if(checkInput(email,username,password)){
                     mProgressBar.setVisibility(View.VISIBLE);
                     mPleaseWait.setVisibility(View.VISIBLE);

                     firebaseMethods.registerNewUserEmail(email,username,password);
                     mProgressBar.setVisibility(View.GONE);
                     mPleaseWait.setVisibility(View.GONE);
                 }
            }
        });

    }

    private boolean checkInput(String email, String username, String password){
        Log.d(TAG, "checkInput: start to check the input ");
        if(email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(mContext,"You have to fill all the fields",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }




    //--------------------------firebase-----------------------------
    //Yuan
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
        myRef = mFirebaseDB.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in " + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override //show when the data is changed
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // 1. check if the username is already exist
                            if(firebaseMethods.checkUserNameDuplication(username,dataSnapshot)){
                                randomString = myRef.push().getKey().substring(0,4);
                                Log.d(TAG, "onDataChange: userNmae already exist! the name has append "+ randomString);
                                username = username + randomString;
                            }


                            // Add new user to the DB
                            firebaseMethods.addNewUser(email,username,"All Your Posts","");


                            Toast.makeText(mContext,"signup successful! ",Toast.LENGTH_SHORT).show();
                            //TODO signout

                            //mAuth.signOut();



                        }

                        @Override//show when error occurs
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish(); // back to the previous activity
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
