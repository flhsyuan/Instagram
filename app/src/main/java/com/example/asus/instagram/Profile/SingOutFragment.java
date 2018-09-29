package com.example.asus.instagram.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.asus.instagram.Login.LoginActivity;
import com.example.asus.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SingOutFragment extends AppCompatActivity {

    private static final String TAG = "SingOutFragment";
    private Context mContext = SingOutFragment.this;
    private ProgressBar mProgressbar;
    private TextView signOut_text,signingOut_text;

    //firebase tools
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Nullable
    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signout_profile);
        signOut_text = (TextView)findViewById(R.id.text_SureToSignOut);
        signingOut_text = (TextView)findViewById(R.id.SignOut_signingout);
        Button button_comfirm_signout = (Button)findViewById(R.id.button_sureToSignOut);
        mProgressbar = (ProgressBar)findViewById(R.id.signOut_ProgressLoadingBar);

        mProgressbar.setVisibility(View.GONE);
        signingOut_text.setVisibility(View.GONE);

        setupFirebaseAuth();

        button_comfirm_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempt to sign out ");
                mProgressbar.setVisibility(View.VISIBLE);
                signingOut_text.setVisibility(View.VISIBLE);
                mAuth.signOut();
                finish();
            }
        });

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

                    Log.d(TAG, "onAuthStateChanged: go to the login page");
                    // go back to login activity
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
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
