package com.example.asus.instagram.Utils;

/**
 * Yuan: some firebase methods
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.asus.instagram.Models.User;
import com.example.asus.instagram.Models.UserAccountsettings;
import com.example.asus.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.example.asus.instagram.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseMethods {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    private String userID;

    private static final String TAG = "FirebaseMethods";


    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        this.mContext = context;
        if (mAuth.getCurrentUser() != null){
            this.userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, String caption, int count, String imgUrl){
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo.");

        FilePaths filePaths = new FilePaths();
        //case new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photo' node and 'user_photos' node

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed.", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }
        //case profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading PROFILE photo");
        }
    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
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
