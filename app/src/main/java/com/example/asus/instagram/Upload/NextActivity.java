package com.example.asus.instagram.Upload;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.instagram.R;
import com.example.asus.instagram.Utils.UniversalImageLoader;

/**
 * Created by Yiqun
 */
public class NextActivity extends AppCompatActivity{

    private static final String TAG = "NextActivity";

    //firebase

    //vars
    private  String mAppend = "file:/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        //setupFirebaseAuth();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });

        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to the final upload screen");
                //upload the image to firebase
            }
        });

        setImage();
    }

    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        Intent intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);
        UniversalImageLoader.setImage(intent.getStringExtra(getString(R.string.selected_image)), image,null, mAppend);
    }
}
