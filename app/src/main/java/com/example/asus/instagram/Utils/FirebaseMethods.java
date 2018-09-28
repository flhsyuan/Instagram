package com.example.asus.instagram.Utils;

/**
 * Yuan: some firebase methods
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.asus.instagram.Models.User;
import com.example.asus.instagram.Models.UserAccountsettings;
import com.example.asus.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {

    private FirebaseAuth mAuth;

    private Context mContext;

    private String userID;

    private static final String TAG = "FirebaseMethods";
    //Yuan: firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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

                            Log.d(TAG, "createUserWithEmail:success" + userID);

                            Toast.makeText(mContext, "Register success.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // If register fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Register failed.",
                                    Toast.LENGTH_SHORT).show();
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




    /**
     * Check if the user is already in this DB
     */
    public boolean checkUserNameDuplication(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkUserNameDuplication: checking if "+username+" is exist");

        User user = new User();

        for(DataSnapshot ds :dataSnapshot.child(userID).getChildren()){
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

    public void addNewUser(String email ,String username, String description, String profilePhoto ){
        Log.d(TAG, "addNewUser: new data added");
        // put the user object to the firebase DB
        User user = new User(StringManipulation.condenseUsername(username),userID,email,1);
        myRef.child(mContext.getString(R.string.dbname_users)).child(userID).setValue(user);

        // put the user_account_settings object to the firebase DB
        UserAccountsettings user_account_setting = new UserAccountsettings(StringManipulation.condenseUsername(username),description,username,0,0,0,profilePhoto);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).setValue(user_account_setting);
    }

}
