package com.example.asus.instagram.Utils;

/**
 * Yuan: some firebase methods
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.asus.instagram.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class FirebaseMethods {

    private FirebaseAuth mAuth;

    private Context mContext;

    private String userID;

    private static final String TAG = "FirebaseMethods";


    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        this.mContext = context;
        if (mAuth.getCurrentUser() != null){
            this.userID = mAuth.getCurrentUser().getUid();
        }
    }

/**
 * Create a new user using a Email address
 */

    public void registerNewUserEmail(final String email,final String username,final String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // register success, update UI with the signed-in user's information


                            userID = mAuth.getCurrentUser().getUid();

                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.d(TAG, "createUserWithEmail:success" + userID);

                            Toast.makeText(mContext, "Register success.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If register fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Register failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void updateUI(FirebaseUser user){
        //check if the user is logged in
//        checkCurrentUser(user);
        if(user != null){
            //user is signed in
            Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
        }else{
            //user is signed out
            Log.d(TAG, "onAuthStateChanged: signed_out");
        }
    }

      /*
//    checks to see if the @param user is logged in
//     */
//    private void checkCurrentUser(FirebaseUser user){
//        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
//        if(user == null){
//            Intent intent = new Intent(mContext,LoginActivity.class);
//            startActivity(intent);
//        }
//    }


    /**
     * Check if the user is already in this DB
     */
    public boolean checkUserNameDuplication(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkUserNameDuplication: checking if "+username+" is exist");

        User user = new User();

        for(DataSnapshot ds :dataSnapshot.getChildren()){
            Log.d(TAG, "checkUserNameDuplication: dataSnapShot  " + ds);//iterate node in the DB

            user.setUsername(ds.getValue(User.class).getUsername());// set the username to the retrievaled name
            Log.d(TAG, "checkUserNameDuplication: username  " + user.getUsername());

            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkUserNameDuplication: find a duplicated username: "+ user.getUsername());
                return true;
            }
        }
        return false;
    }

//    public

}
