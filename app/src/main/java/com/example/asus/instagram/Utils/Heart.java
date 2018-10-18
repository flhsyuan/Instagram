package com.example.asus.instagram.Utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Heart {
    private static final String TAG = "Heart";

    private static final DecelerateInterpolator DE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator AC_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    public ImageView heartWhite, heartRed;

    public Heart(ImageView heartWhite,ImageView heartRed){

        this.heartRed = heartRed;
        this.heartWhite = heartWhite;
    }

    public void toggleLike(){
        Log.d(TAG, "toggleLike: toggling heart! ");

        AnimatorSet animationSet = new AnimatorSet();

        if(heartRed.getVisibility()== View.VISIBLE){
            Log.d(TAG, "toggleLike: toggling red off");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator downX = ObjectAnimator.ofFloat(heartRed,"scaleX",1f,0f);
            downX.setDuration(300);
            downX.setInterpolator(AC_INTERPOLATOR);


            ObjectAnimator downY = ObjectAnimator.ofFloat(heartRed,"scaleY",1f,0f);
            downY.setDuration(300);
            downY.setInterpolator(AC_INTERPOLATOR);

            heartRed.setVisibility(View.GONE);
            heartWhite.setVisibility(View.VISIBLE);

            animationSet.playTogether(downX,downY);
            
        }
        else if(heartRed.getVisibility()== View.GONE){
            Log.d(TAG, "toggleLike: toggling red on");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator downX = ObjectAnimator.ofFloat(heartRed,"scaleX",0.1f,1f);
            downX.setDuration(300);
            downX.setInterpolator(DE_INTERPOLATOR);


            ObjectAnimator downY = ObjectAnimator.ofFloat(heartRed,"scaleY",0.1f,1f);
            downY.setDuration(300);
            downY.setInterpolator(DE_INTERPOLATOR);

            heartRed.setVisibility(View.VISIBLE);
            heartWhite.setVisibility(View.GONE);

            animationSet.playTogether(downX,downY);

        }
        animationSet.start();
    }
}
